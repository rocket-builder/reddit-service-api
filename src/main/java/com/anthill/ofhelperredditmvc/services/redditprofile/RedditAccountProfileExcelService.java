package com.anthill.ofhelperredditmvc.services.redditprofile;

import com.anthill.ofhelperredditmvc.domain.*;
import com.anthill.ofhelperredditmvc.domain.excel.RedditAccountProfileFields;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
@Service
public class RedditAccountProfileExcelService {

    public List<RedditAccountProfile> getFromFile(MultipartFile file) throws IOException {
        var worksheet = new XSSFWorkbook(file.getInputStream())
                .getSheetAt(0);

        var profiles = new ArrayList<RedditAccountProfile>();

        StreamSupport.stream(((Iterable<Row>) worksheet::rowIterator).spliterator(), false)
                .skip(1).forEach(
                        row -> parseAccount(row)
                                .ifPresent(account -> {
                                    var proxy = parseProxy(row);
                                    var access = parseAccess(row);

                                    var profile = RedditAccountProfile.builder()
                                            .redditAccount(account)
                                            .proxy(proxy)
                                            .access(access)
                                            .build();
                                    profiles.add(profile);
                                })
        );

        return profiles;
    }

    private Optional<RedditAccount> parseAccount(Row row){
        try {
            var cell = row.getCell(RedditAccountProfileFields.REDDIT_ACCOUNT.getIndex()).getStringCellValue();

            var loginPassword = cell.split(":");
            var account = new RedditAccount(loginPassword[0], loginPassword[1]);

            return Optional.of(account);
        } catch (Exception ex){
            log.error("Cannot parse account");
            ex.printStackTrace();

            return Optional.empty();
        }
    }

    private Proxy parseProxy(Row row){
        try {
            return new Proxy(row.getCell(RedditAccountProfileFields.PROXY.getIndex()).getStringCellValue());
        } catch (Exception ex) {
            return null;
        }
    }

    private AccountAccess parseAccess(Row row){
        try {
            var access = row.getCell(RedditAccountProfileFields.ACCESS_TYPE.getIndex()).getStringCellValue();
            return AccountAccess.valueOf(access);
        } catch (Exception ex) {
            return AccountAccess.ALL;
        }
    }
}
