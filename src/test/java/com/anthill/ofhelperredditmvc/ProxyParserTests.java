package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.services.parsers.ProxyParser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ProxyParserTests {

    @Autowired
    ProxyParser parser;

    @Test
    void parseProxy_whenAllCorrect_thenParse(){
        //arrange
        var proxy = "https://bskyqlkn:rmwoer8r9w3a@104.144.3.230:6309";

        var result = parser.isCorrect(proxy);

        assert result;
    }

    @Test
    void parseProxy_whenError_thenParse(){
        //arrange
        var proxy = "https://bskyqlkn:rmwoer8r9w3a@104.144.3.2";

        var result = parser.isCorrect(proxy);

        assert !result;
    }

    @Test
    void parseProxy_whenEmpty_thenParse(){
        //arrange
        var proxy = "";

        var result = parser.isCorrect(proxy);

        assert !result;
    }
}
