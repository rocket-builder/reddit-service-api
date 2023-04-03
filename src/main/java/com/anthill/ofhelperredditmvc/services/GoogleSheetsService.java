package com.anthill.ofhelperredditmvc.services;

import com.anthill.ofhelperredditmvc.exceptions.GoogleSheetsAccessException;
import com.anthill.ofhelperredditmvc.exceptions.GoogleSheetsRowsNotFoundException;
import com.anthill.ofhelperredditmvc.services.parsers.GoogleSheetIdUrlParser;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class GoogleSheetsService {

    private final GoogleSheetIdUrlParser urlParser;
    private final Sheets sheets;

    private final Function<List<Object>, List<String>> mapToString = (row) -> row.stream()
            .map(String::valueOf)
            .collect(Collectors.toList());

    public GoogleSheetsService(GoogleSheetIdUrlParser urlParser, Sheets sheets) {
        this.urlParser = urlParser;
        this.sheets = sheets;
    }

    public List<List<String>> getRows(String url) throws IOException {
        var sheetId = urlParser.parse(url);

        var values = sheets.spreadsheets().values().get(sheetId, "A:Z")
                .execute()
                .getValues();
        if(values == null){
            throw new GoogleSheetsRowsNotFoundException();
        }

        return values.stream()
                .map(mapToString)
                .collect(Collectors.toList());
    }

    public void setRange(String url, String range, String value) throws GoogleSheetsAccessException {
        var sheetId = urlParser.parse(url);

        var valueRange = new ValueRange();
        valueRange.setValues(List.of(List.of(value)));

        try {
            sheets.spreadsheets().values().update(sheetId, range, valueRange)
                    .setValueInputOption("USER_ENTERED")
                    .execute();

        } catch (IOException ex){
            var message = ex.getMessage();

            if(message.contains(String.valueOf(HttpStatus.FORBIDDEN.value()))){
                throw new GoogleSheetsAccessException();
            } else {
                log.error("Cannot setRange because: "  + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
