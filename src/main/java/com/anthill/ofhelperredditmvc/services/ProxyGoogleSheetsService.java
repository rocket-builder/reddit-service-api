package com.anthill.ofhelperredditmvc.services;

import com.anthill.ofhelperredditmvc.domain.Proxy;
import com.anthill.ofhelperredditmvc.exceptions.GoogleSheetsAccessException;
import com.anthill.ofhelperredditmvc.exceptions.GoogleSheetsReadException;
import com.anthill.ofhelperredditmvc.exceptions.IncorrectInputDataException;
import com.anthill.ofhelperredditmvc.services.parsers.ProxyParser;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProxyGoogleSheetsService {

    private final ProxyParser proxyParser;
    private final GoogleSheetsService sheetsService;

    public ProxyGoogleSheetsService(ProxyParser proxyParser,
                                    GoogleSheetsService sheetsService) {
        this.proxyParser = proxyParser;
        this.sheetsService = sheetsService;
    }

    public List<Proxy> getFromUrl(String url)
            throws GoogleSheetsReadException, GoogleSheetsAccessException {
        try {
            var rows = sheetsService.getRows(url);

            var proxies = new ArrayList<Proxy>();
            rows.stream().skip(1).forEach(row -> {
                parseProxy(row)
                        .ifPresent(proxies::add);
            });

            return proxies;
        } catch (GoogleJsonResponseException ex){
          if(ex.getDetails().getCode() == 403){
              throw new GoogleSheetsAccessException();
          } else {
              throw new GoogleSheetsReadException(ex.getDetails().getMessage());
          }
        } catch (IOException ex){
            throw new GoogleSheetsReadException(ex.getMessage());
        }
    }

    public Optional<Proxy> parseProxy(List<String> row){
        try {
            var formattedValue = row.get(0);

            if(!proxyParser.isCorrect(formattedValue)){
                throw new IncorrectInputDataException("Incorrect proxy");
            }

            var proxy = Proxy.builder()
                    .formattedValue(formattedValue)
                    .build();

            return Optional.of(proxy);
        } catch (IndexOutOfBoundsException | IncorrectInputDataException ex){
            return Optional.empty();
        }
    }
}
