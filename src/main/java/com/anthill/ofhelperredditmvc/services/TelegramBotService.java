package com.anthill.ofhelperredditmvc.services;

import com.anthill.ofhelperredditmvc.domain.dto.NewsletterDto;
import com.anthill.ofhelperredditmvc.exceptions.TelegramBotServiceException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
@Service
public class TelegramBotService {

    @Value("${telegram.bot.endpoint}")
    private String sendMessageEndpoint;

    private final ObjectMapper mapper;
    private final HttpClient httpClient;

    public TelegramBotService(ObjectMapper mapper, HttpClient httpClient) {
        this.mapper = mapper;
        this.httpClient = httpClient;
    }

    public void sendNewsletter(NewsletterDto message) throws TelegramBotServiceException {
        try {
            var json = mapper.writeValueAsString(message);

            var request = HttpRequest.newBuilder()
                    .uri(new URI(sendMessageEndpoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != HttpStatus.OK.value()) {

                throw new IOException("status code: " + response.statusCode());
            }
        } catch (URISyntaxException | IOException | InterruptedException ex) {
            throw new TelegramBotServiceException(ex.getMessage());
        }
    }
}
