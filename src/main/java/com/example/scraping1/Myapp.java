package com.example.scraping1;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "myapp")
public class Myapp {

    // @Dataやらないとデータを取得できない。
    private int scraping;

    private int loop_count;

    private String user;

    private String password;

}
