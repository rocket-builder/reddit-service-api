package com.anthill.ofhelperredditmvc.services;

import com.anthill.ofhelperredditmvc.domain.RedditAccountProfile;
import com.anthill.ofhelperredditmvc.domain.session.bot.AbstractBotSession;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.AbstractBotGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.PostingBotGroup;
import com.anthill.ofhelperredditmvc.domain.session.group.bot.UpVoteBotGroup;
import com.anthill.ofhelperredditmvc.exceptions.RedditBotServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

@Slf4j
@Service
public class RedditBotService {

    @Value("${reddit.bot.host}")
    private String botBaseHost;

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public RedditBotService(HttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
    }

    public <G extends AbstractBotGroup<S>, S extends AbstractBotSession> void sendGroup(G group) throws RedditBotServiceException {
        var url = getStartRequestUrl(group);

        sendRequest(url, group, HttpMethod.POST);
    }

    public <G extends AbstractBotGroup<S>, S extends AbstractBotSession>void sendStop(G group) throws RedditBotServiceException {
        var url = botBaseHost + "/group/stop";

        sendRequest(url, group, HttpMethod.POST);
    }

    private <G extends AbstractBotGroup<S>, S extends AbstractBotSession> String getStartRequestUrl(G group){

        var requestUrl = botBaseHost + "/group/start-";
        if(group.getClass().equals(PostingBotGroup.class)){
            requestUrl += "posting";

        } else if(group.getClass().equals(UpVoteBotGroup.class)){
            requestUrl += "upvotes";

        } else {
            throw new IllegalArgumentException("Unsupported session type");
        }

        return requestUrl;
    }

    public void sendToGetAccessTokens(List<RedditAccountProfile> accounts) throws RedditBotServiceException {
        var url = botBaseHost + "/token/create";

        sendRequest(url, accounts, HttpMethod.POST);
    }

    private void sendRequest(String url, Object body, HttpMethod method) throws RedditBotServiceException {
        try {
            var json = mapper.writeValueAsString(body);
            log.info("JSON: " + json);

            var request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .method(method.name(), HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != HttpStatus.OK.value()) {

                throw new RedditBotServiceException("status code: " + response.statusCode());
            }
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            log.error("Cannot send because: ");
            ex.printStackTrace();

            throw new RedditBotServiceException(ex.getMessage());
        }
    }
}
