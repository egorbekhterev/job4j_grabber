package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.util.Objects;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);
    private static final int PAGE_COUNTER = 5;

    private String retrieveDescription(String link) {
        Connection connection = Jsoup.connect(link);
        Document document = null;
        try {
            document = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements rows = Objects.requireNonNull(document).select(".style-ugc");
        return rows.text();
    }

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= PAGE_COUNTER; i++) {
            Connection connection = Jsoup.connect(String.format("%s%d", PAGE_LINK, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = Objects.requireNonNull(titleElement).child(0);
                String vacancyName = titleElement.text();
                Element dateElement = Objects.requireNonNull(row.select(".vacancy-card__date").first()).child(0);
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String date = dateElement.attr("datetime");
                String dateAfterParsing = new HabrCareerDateTimeParser().parse(date).toString();
                String description = new HabrCareerParse().retrieveDescription(link);
                System.out.printf("%s %s %s%n %s%n", vacancyName, link, dateAfterParsing, description);
            });
        }
    }
}
