package ru.sber.sberlunch.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class TextImporter {
    @Getter
    private List<String> lines = new ArrayList<>();

    @PostConstruct
    private void initialize() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("huina.txt");
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
