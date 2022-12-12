package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);
    private static final int PAGE_COUNTER = 5;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> list = new ArrayList<>();
        for (int i = 1; i <= PAGE_COUNTER; i++) {
            Connection connection = Jsoup.connect(String.format("%s%d", link, i));
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = Objects.requireNonNull(titleElement).child(0);
                String vacancyName = titleElement.text();
                Element dateElement = Objects.requireNonNull(row.select(".vacancy-card__date").first()).child(0);
                String fullLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                String date = dateElement.attr("datetime");
                LocalDateTime dateAfterParsing = dateTimeParser.parse(date);
                String description = retrieveDescription(fullLink);
                list.add(new Post(vacancyName, fullLink, description, dateAfterParsing));
            });
        }
        return list;
    }

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
}
