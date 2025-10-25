package com.alok;

import com.alok.app.App;
import com.alok.app.AppConfig;

public class Main {
    public static void main(String[] args) {
        AppConfig config = AppConfig.defaults();
        new App(config).run();
    }
}
