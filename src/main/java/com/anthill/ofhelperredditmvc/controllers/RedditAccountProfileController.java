package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.AbstractEntity;
import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import com.anthill.ofhelperredditmvc.domain.dto.PageDto;
import com.anthill.ofhelperredditmvc.domain.dto.RedditAccountProfileFilterDto;
import com.anthill.ofhelperredditmvc.domain.dto.accounts.RedditAccountProfileUpdateDto;
import com.anthill.ofhelperredditmvc.exceptions.RedditBotServiceException;
import com.anthill.ofhelperredditmvc.interfaces.IAuthenticatedRestService;
import com.anthill.ofhelperredditmvc.services.RedditBotService;
import com.anthill.ofhelperredditmvc.services.redditprofile.RedditAccountProfileUpdateService;
import com.anthill.ofhelperredditmvc.services.redditprofile.RedditAccountProfileExcelService;
import com.anthill.ofhelperredditmvc.services.redditprofile.RedditAccountProfileGoogleSheetsService;
import com.anthill.ofhelperredditmvc.services.search.RedditAccountProfileSearchService;
import com.anthill.ofhelperredditmvc.services.security.JwtUserRetriever;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Tag(name = "RedditAccountProfile")
@RequestMapping("/redditAccountProfile")
@RestController
public class RedditAccountProfileController extends AbstractAuthenticatedRestController<RedditAccountProfile> {

    private final RedditBotService botService;
    private final RedditAccountProfileUpdateService updateService;
    private final RedditAccountProfileExcelService excelService;
    private final RedditAccountProfileGoogleSheetsService sheetsService;
    private final RedditAccountProfileSearchService searchService;

    protected RedditAccountProfileController(IAuthenticatedRestService<RedditAccountProfile> rest,
                                             RedditAccountProfileExcelService excelService,
                                             RedditAccountProfileGoogleSheetsService sheetsService,
                                             RedditAccountProfileUpdateService updateService,
                                             JwtUserRetriever jwt,
                                             RedditBotService botService,
                                             RedditAccountProfileSearchService searchService) {
        super(rest, jwt);
        this.updateService = updateService;
        this.excelService = excelService;
        this.sheetsService = sheetsService;
        this.botService = botService;
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ResponseEntity<PageDto<RedditAccountProfile>> searchAllPageable(
            @PageableDefault(page = 0, size = 10, sort = {"id"}, direction = Sort.Direction.DESC) Pageable pageable,
            RedditAccountProfileFilterDto filter,
            HttpServletRequest request) {
        var user = jwt.getUserFromRequest(request);

        var profiles = user.getProfiles();
        if(nonNull(filter)){
            profiles = searchService.searchInProfiles(profiles, filter);
        }

        var data = profiles
                .stream()
                .sorted((o1, o2) -> Long.compare(o2.getId(), o1.getId()))
                .skip(pageable.getPageSize() * pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        var dto = new PageDto<>(data, profiles.size());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PostMapping(value = "/excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Iterable<RedditAccountProfile>> saveExcel(
            @RequestBody MultipartFile file, HttpServletRequest request) throws IOException {
        var profiles = excelService.getFromFile(file);

        return super.saveAll(profiles, request);
    }

    @PostMapping("/googleSheet")
    public ResponseEntity<Iterable<RedditAccountProfile>> saveGoogleSheets(
            @RequestParam String url, HttpServletRequest request) throws IOException {
        var profiles = sheetsService.getFromUrl(url);

        return super.saveAll(profiles, request);
    }

    @PutMapping("/list")
    public ResponseEntity<Iterable<RedditAccountProfile>> updateFromDto(
            @RequestBody RedditAccountProfileUpdateDto dto, HttpServletRequest request) {
        var user = jwt.getUserFromRequest(request);

        var profiles = user.getProfiles().stream()
                .filter(profile -> dto.getIds().contains(profile.getId()))
                .collect(Collectors.toList());

        var updated = updateService.updateListBySingle(profiles, dto.getUpdate());

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @PutMapping("/list/detachProxy")
    public ResponseEntity<Iterable<RedditAccountProfile>> detachProxy(
            @RequestBody List<Long> ids, HttpServletRequest request) {
        var user = jwt.getUserFromRequest(request);

        var profiles = user.getProfiles().stream()
                .filter(profile -> ids.contains(profile.getId()))
                .collect(Collectors.toList());

        var detached = updateService.detachProxies(profiles);

        return new ResponseEntity<>(detached, HttpStatus.OK);
    }

    @PutMapping("/token/page")
    public ResponseEntity<PageDto<RedditAccountProfile>> startGetTokens(
            @PageableDefault(page = 0, size = 10) Pageable pageable, HttpServletRequest request) throws RedditBotServiceException {
        var user = jwt.getUserFromRequest(request);

        var profiles = rest.findAll(user).stream()
                .filter(p -> p.getProxy() != null &&
                        p.getRedditAccount().getAccessToken() == null &&
                        p.getRedditAccount().getRefreshToken() == null)
                .collect(Collectors.toList());

        botService.sendToGetAccessTokens(profiles);

        var page = rest.findAllPageable(user, pageable);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @DeleteMapping("/all/filter")
    public ResponseEntity<String> deleteAllWithFilter(
            HttpServletRequest request, RedditAccountProfileFilterDto filter){
        var user = jwt.getUserFromRequest(request);

        if(nonNull(filter)){
            var profileIds = searchService.searchInProfiles(user.getProfiles(), filter)
                    .stream()
                    .map(AbstractEntity::getId)
                    .collect(Collectors.toList());

            rest.deleteAllByIds(profileIds, user);
        } else {
            rest.deleteAllByUser(user);
        }
        
        return new ResponseEntity<>("Successful deleted!", HttpStatus.OK);
    }
}
