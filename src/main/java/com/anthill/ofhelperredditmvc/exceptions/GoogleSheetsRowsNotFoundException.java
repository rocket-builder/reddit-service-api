package com.anthill.ofhelperredditmvc.exceptions;

public class GoogleSheetsRowsNotFoundException extends RuntimeException {
    public GoogleSheetsRowsNotFoundException(){
        super("Rows are not found at list, please sure that you provide filled spreadsheet");
    }
}
