# job4j_grabber

## Проект - Агрегатор вакансий.

Система запускается по расписанию - раз в минуту.
Работает с разделом: https://career.habr.com/vacancies/java_developer.
Программа считывает все вакансии, относящиеся к Java, c первых пяти страниц и записывает их в базу данных.
Список вакансий выводится в браузер.
Подключение к БД, установка таймера и вывод в браузер реализуются посредством чтения файла конфигурации.

### Инструменты
* Java 17
* PostgreSQL
* JDBC
* Jsoup
* Liquibase
* quartz, log4j, slf4j 
* Socket
* IO
* JUnit 5
* Maven
* IDEA
* Git
### Запуск через терминал

<p>1. Собрать jar через Maven</p>

```bash
mvn package
```
<p>2. Запустить jar файл</p>

```bash
java -jar target/job4j_grabber-1.0-SNAPSHOT.jar
```

### Запуск через IDE

1. Перейти к папке ``src/main/java`` и файлу ``ru.job4j/pooh/grabber/Grabber.java``
2. Нажать на кнопку запуска метода ``main`` в IDE
