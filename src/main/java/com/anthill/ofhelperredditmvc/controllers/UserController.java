package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import com.anthill.ofhelperredditmvc.domain.User;
import com.anthill.ofhelperredditmvc.domain.dto.UserProfileDto;
import com.anthill.ofhelperredditmvc.domain.session.group.AbstractGroup;
import com.anthill.ofhelperredditmvc.exceptions.ResourceNotFoundedException;
import com.anthill.ofhelperredditmvc.interfaces.IRestService;
import com.anthill.ofhelperredditmvc.services.profile.ProfileService;
import com.anthill.ofhelperredditmvc.services.rest.UserService;
import com.anthill.ofhelperredditmvc.services.security.JwtUserRetriever;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Tag(name = "User")
@RequestMapping("/user")
@RestController
public class UserController extends AbstractRestController<User> {

    private final JwtUserRetriever jwt;
    private final ProfileService profileService;

    protected UserController(UserService rest, JwtUserRetriever jwt,
                             ProfileService profileService) {
        super(rest);
        this.jwt = jwt;
        this.profileService = profileService;
    }

    @GetMapping("/upVoteBalance")
    public ResponseEntity<Integer> getUpVoteBalance(HttpServletRequest request){
        var user = jwt.getUserFromRequest(request);

        return new ResponseEntity<>(user.getUpVoteBalance(), HttpStatus.OK);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(HttpServletRequest request){
        var user = jwt.getUserFromRequest(request);

        var profile = profileService.toProfile(user);

        return new ResponseEntity<>(profile, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<User> patchUser(@RequestBody User user) throws ResourceNotFoundedException {
        var old = rest.findById(user.getId());

        old.setUpVoteBalance(user.getUpVoteBalance());
        old.setRoles(user.getRoles());
        old.setBanned(user.isBanned());

        var updated = rest.update(old);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}
