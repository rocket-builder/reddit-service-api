package com.anthill.ofhelperredditmvc.utils;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PercentageCalculator {

    public BigDecimal calculatePercentage(double obtained, double total, int places) {
        return BigDecimal.valueOf(obtained * 100 / total).setScale(places, RoundingMode.HALF_UP);
    }
}