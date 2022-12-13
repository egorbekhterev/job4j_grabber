package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;
import ru.job4j.grabber.utils.HabrCareerDateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HabrCareerParse implements Parse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);
    private static final int PAGE_COUNTER = 1;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> list = new ArrayList<>();
        try {
            for (int i = 1; i <= PAGE_COUNTER; i++) {
                Connection connection = Jsoup.connect(String.format("%s%d", link, i));
                Document document = connection.get();
                Elements rows = document.select(".vacancy-card__inner");
                rows.forEach(element -> list.add(parsing(element)));
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Error while adding elements to list.");
        }
        return list;
    }

    private Post parsing(Element element) {
        Element titleElement = element.select(".vacancy-card__title").first();
        Element linkElement = Objects.requireNonNull(titleElement).child(0);
        String vacancyName = titleElement.text();
        Element dateElement = Objects.requireNonNull(element.select(".vacancy-card__date").first()).child(0);
        String fullLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
        String date = dateElement.attr("datetime");
        LocalDateTime dateAfterParsing = dateTimeParser.parse(date);
        String description = retrieveDescription(fullLink);
        return new Post(vacancyName, fullLink, description, dateAfterParsing);
    }

    private String retrieveDescription(String link) {
        String  retrieved;
        try {
            Connection connection = Jsoup.connect(link);
            Document document = connection.get();
            Elements rows = Objects.requireNonNull(document).select(".style-ugc");
            retrieved = rows.text();
        } catch (IOException e) {
            throw new IllegalArgumentException("Error while retrieving description of the vacancy.");
        }
        return retrieved;
    }
}
