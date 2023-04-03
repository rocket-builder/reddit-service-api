package com.anthill.ofhelperredditmvc.services.redditprofile;

import com.anthill.ofhelperredditmvc.domain.AccountAccess;
import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.domain.RedditAccount;
import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import com.anthill.ofhelperredditmvc.domain.excel.RedditAccountProfileFields;
import com.anthill.ofhelperredditmvc.domain.session.fields.AccountShared;
import com.anthill.ofhelperredditmvc.services.GoogleSheetsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RedditAccountProfileGoogleSheetsService {

    private final GoogleSheetsService sheetsService;

    public RedditAccountProfileGoogleSheetsService(GoogleSheetsService sheetsService) {
        this.sheetsService = sheetsService;
    }

    public List<RedditAccountProfile> getFromUrl(String url) throws IOException {
        var rows = sheetsService.getRows(url);

        var profiles = new ArrayList<RedditAccountProfile>();
        rows.stream().skip(1).forEach(row -> {
            parseAccount(row)
                    .ifPresent(account -> {
                        var proxy = parseProxy(row);
                        var access = parseAccess(row);
                        var isShared = parseIsShared(row);

                        var profile = RedditAccountProfile.builder()
                                .redditAccount(account)
                                .proxy(proxy)
                                .access(access)
                                .isShared(isShared)
                                .build();
                        profiles.add(profile);
                    });
        });

        return profiles;
    }

    private Optional<RedditAccount> parseAccount(List<String> row){
        try {
            var accountCell = row.get(RedditAccountProfileFields.REDDIT_ACCOUNT.getIndex());

            var loginPassword = accountCell.split(":");
            var account = new RedditAccount(loginPassword[0], loginPassword[1]);

            return Optional.of(account);
        } catch (Exception ex){
            return Optional.empty();
        }
    }

    private AccountAccess parseAccess(List<String> row){
        try {
            return AccountAccess.valueOf(row.get(RedditAccountProfileFields.ACCESS_TYPE.getIndex()));
        } catch (Exception ex){
            return AccountAccess.ALL;
        }
    }

    private boolean parseIsShared(List<String> row){
        try {
            return AccountShared.valueOf(row.get(RedditAccountProfileFields.IS_SHARED.getIndex())).equals(AccountShared.YES);
        } catch (Exception ex){
            return false;
        }
    }

    private Proxy parseProxy(List<String> row){
        try {
            return new Proxy(row.get(RedditAccountProfileFields.PROXY.getIndex()));
        } catch (Exception ex){
            return null;
        }
    }
}
