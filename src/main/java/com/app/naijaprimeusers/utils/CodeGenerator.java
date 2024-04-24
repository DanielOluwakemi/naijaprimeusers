package com.app.naijaprimeusers.utils;

import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

@Component
public class CodeGenerator {

    public int generateRandomSixDigitCode() {
        return RandomUtils.nextInt(100000, 1000000);
    }
}
