package com.anthill.ofhelperredditmvc.services;

import com.anthill.ofhelperredditmvc.domain.scrapper.RedditAccountScrapped;
import com.anthill.ofhelperredditmvc.domain.scrapper.RedditAccountScrappedDto;
import com.anthill.ofhelperredditmvc.exceptions.RedditAccountScrapperException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.rholder.retry.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class RedditAccountScrapperService {

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public RedditAccountScrapperService(HttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    public RedditAccountScrapped scrapWithRetry(String username, int tryCount) throws RedditAccountScrapperException {
        var retry = RetryerBuilder.<RedditAccountScrapped>newBuilder()
                .retryIfExceptionOfType(IOException.class)
                .retryIfExceptionOfType(InterruptedException.class)
                .withWaitStrategy(WaitStrategies.fixedWait(5, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(tryCount))
                .build();

        try {
            return retry.call(() -> scrap(username));
        } catch (RetryException | ExecutionException ex) {
            ex.printStackTrace();
            throw new RedditAccountScrapperException(username, ex.getMessage());
        }
    }
    public RedditAccountScrapped scrap(String username)
            throws RedditAccountScrapperException, IOException, InterruptedException {
        try {
            var request = HttpRequest.newBuilder()
                    .uri(new URI("https://old.reddit.com/user/" + username + "/about.json"))
                    .GET()
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            RedditAccountScrapped account;
            if(response.statusCode() == HttpStatus.NOT_FOUND.value()) {

                account = RedditAccountScrapped.builder()
                        .username(username)
                        .isBanned(true)
                        .build();

            } else if(response.statusCode() == HttpStatus.OK.value()) {

                account = mapper.readValue(response.body(), RedditAccountScrappedDto.class)
                        .getRedditAccount();
            } else {
                throw new RedditAccountScrapperException(username, "status code: " + response.statusCode());
            }

            return account;
        } catch (URISyntaxException ex) {

            throw new RedditAccountScrapperException(username, "incorrect username, " + ex.getMessage());
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            throw new RedditAccountScrapperException(username, "cannot parse reddit account: " + ex.getMessage());
        }
    }
}
