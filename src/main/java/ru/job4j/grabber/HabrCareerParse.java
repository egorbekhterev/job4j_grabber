package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import java.io.IOException;
import java.util.Objects;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";

    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        Connection connection = Jsoup.connect(PAGE_LINK);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-card__inner");
        rows.forEach(row -> {
            Element titleElement = row.select(".vacancy-card__title").first();
            Element linkElement = Objects.requireNonNull(titleElement).child(0);
            String vacancyName = titleElement.text();
            Element dateElement = Objects.requireNonNull(row.select(".vacancy-card__date").first()).child(0);
            String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
            String[] date = dateElement.attr("datetime").split("T");
            System.out.printf("%s %s %s %s%n", vacancyName, link, date[0], date[1].substring(0, 8));
        });
    }
}