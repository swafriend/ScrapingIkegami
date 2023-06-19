package com.example.scraping1;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@SpringBootApplication
public class Scraping1Application {

    private static Myapp myapp;

    public Scraping1Application(Myapp myapp) {
        Scraping1Application.myapp = myapp;
    }

    public static void main(String[] args) {
        SpringApplication.run(Scraping1Application.class, args);

        String thePath = "C:\\Windows\\Media\\Windows Ringin.wav";
        try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(thePath).getAbsoluteFile())) {

            String LOGIN_URL = "https://www.e-license.jp/el31/mSg1DWxRvAI-brGQYS-1OA==";
            String mainUrl = "https://www.e-license.jp/el31/pc/login";
//            String reserveUrl = "https://www.e-license.jp/el31/pc/reserv/p03/p03a";
            // ユーザーエージェント
            final String UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36";
            // ユーザ名・パスワード
            final String SCHOOLID = "mSg1DWxRvAI-brGQYS-1OA==";
            final String USERNAME = myapp.getUser();
            final String PASS = myapp.getPassword();

//            for (int vvv = 1; vvv < myapp.getLoop_count(); vvv++) {

            Map<String, String> cookies = new HashMap<>();
            Connection.Response response1 = Jsoup.connect(LOGIN_URL)
                    .method(Connection.Method.GET)
                    .execute();
            Connection.Response response2 = Jsoup.connect(mainUrl)
                    .data("schoolCd", SCHOOLID)
                    .data("studentId", USERNAME)
                    .data("password", PASS)
                    .userAgent(UA)
                    .cookies(response1.cookies())
                    .followRedirects(false)
                    .method(Connection.Method.POST)
                    .execute();

            response1.cookies().forEach((s, s2) -> {
                        if (!s.equals("JSESSIONID")) {
                            cookies.put(s, s2);
                        }
                    }
            );
            response2.cookies().forEach((s, s2) -> {
                        if (s.equals("JSESSIONID")) {
                            cookies.put(s, s2);
                        }
                    }
            );

            // リダイレクト先のURLを取得
            String urlRedirectPath = "https://www.e-license.jp" + Objects.requireNonNull(response2.header("Location"));
            Document document = Jsoup.connect(urlRedirectPath)
                    .userAgent(UA)
                    .cookies(cookies)
                    .timeout(10000)
                    .referrer(LOGIN_URL)
                    .followRedirects(false)
                    .get();

            Elements elements = document.getAllElements();
            for (Element element : elements) {
                if (element.className().equals("status1")) {
                    // the reference to the clip
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    clip.loop(10);
                    log.info("空きがありました。音を鳴らしました。");
                    break;
                }
            }

            Thread.sleep(myapp.getScraping());
            log.info("終了しました。");
//            log.info("ループ回数：" + vvv);
//            Thread.sleep(myapp.getScraping());
//            }

        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException | InterruptedException e) {
            log.error("エラーが発生しました。", e);
        }

        log.info("プロセスを終了します。");
        System.exit(0);
    }


}
