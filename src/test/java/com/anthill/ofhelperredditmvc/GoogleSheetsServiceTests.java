package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.exceptions.GoogleSheetsAccessException;
import com.anthill.ofhelperredditmvc.services.parsers.GoogleSheetIdUrlParser;
import com.anthill.ofhelperredditmvc.services.GoogleSheetsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

@SpringBootTest
public class GoogleSheetsServiceTests {

    @Autowired
    GoogleSheetsService sheetsService;

    @Autowired
    GoogleSheetIdUrlParser urlParser;

    @Test
    void parseUrl_whenAllCorrect_shouldParse(){
        var url = "https://docs.google.com/spreadsheets/d/1AzEC9ik0PZFYafyzG3ANCHNv9DR_rteDEEhlgXzf9vo/edit?usp=sharing";

        var sheetId = urlParser.parse(url);

        assertFalse(sheetId.isEmpty());
    }

    @Test
    void getRows_whenAllCorrect_thenGet() throws IOException {
        var url = "https://docs.google.com/spreadsheets/d/1K_aLyT06-XKvfmCWtuZuqS4FYZvPRJglP_-7V9yfwPM/edit#gid=0";

        var result = sheetsService.getRows(url);

        assertFalse(result.isEmpty());
    }

    @Test
    void setRange_whenAllCorrect_thenSet() throws IOException, GoogleSheetsAccessException {
        var url = "https://docs.google.com/spreadsheets/d/1hODhQmjaP3y4UUFgKqch2cdr1WmP0IRnqp9ksGqyQE0/edit#gid=1923429495";
        var range = "I2";
        var message = "Error2";

        assertDoesNotThrow(() -> sheetsService.setRange(url, range, message));
    }

    @Test
    void setRange_whenNorAccess_thenSet(){
        var url = "https://docs.google.com/spreadsheets/d/1r5Vbuzsbsc1vA23CYvgdkWAyQi38EMijnlREQxMKZ0w/edit?usp=sharing";
        var range = "I2";
        var message = "Error";

        assertThrows(GoogleSheetsAccessException.class, () -> sheetsService.setRange(url, range, message));
    }
}
