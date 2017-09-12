package com.SirBlobman.respawnx.utility;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Util {
    public static void print(String... msg) {
        for(String s : msg) {
            Logger log = LogManager.getLogger("RespawnX");
            log.info(s);
        }
    }
}