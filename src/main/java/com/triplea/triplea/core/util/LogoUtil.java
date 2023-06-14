package com.triplea.triplea.core.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogoUtil {

    public static String makeLogo(String symbol){

        if(symbol.isEmpty())
            log.error("makeLogo paramater is empty");

        String logobase = "https://storage.googleapis.com/iex/api/logos/<symbol>.png";
        return logobase.replace("<symbol>", symbol);
    }
}
