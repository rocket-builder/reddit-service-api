package com.anthill.ofhelperredditmvc.utils;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParseHelper {

    public String parseIfPresent(List<String> row, int index){
        try {
            return row.get(index);
        } catch (IndexOutOfBoundsException ex){
            return null;
        }
    }

    public int parseIntIfPresent(List<String> row, int index){
        try {
            return Integer.parseInt(row.get(index));
        } catch (IndexOutOfBoundsException | NumberFormatException ex) {
            return 0;
        }
    }
}
