package ru.job4j.quartz;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Properties;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;

public class AlertRabbit {
    private Properties init() {
        Properties config = new Properties();
        try (InputStream inputStream = AlertRabbit.class.getClassLoader()
                .getResourceAsStream("rabbit.properties")) {
            config.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return config;
    }

    private static Connection getConnection(Properties properties) throws ClassNotFoundException, SQLException {
        Class.forName(properties.getProperty("driver-class-name"));
        String url = properties.getProperty("url");
        String login = properties.getProperty("username");
        String password = properties.getProperty("password");
        return DriverManager.getConnection(url, login, password);
    }

    public static void main(String[] args) {
        AlertRabbit alertRabbit = new AlertRabbit();
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            JobDataMap data = new JobDataMap();
            data.put("store", getConnection(alertRabbit.init()));
            JobDetail job = newJob(Rabbit.class)
                    .usingJobData(data)
                    .build();
            SimpleScheduleBuilder times = simpleSchedule()
                    .withIntervalInSeconds(Integer.parseInt(
                            alertRabbit.init().getProperty("rabbit.interval")))
                    .repeatForever();
            Trigger trigger = newTrigger()
                    .startNow()
                    .withSchedule(times)
                    .build();
            scheduler.scheduleJob(job, trigger);
            Thread.sleep(10000);
            scheduler.shutdown();
        } catch (SchedulerException | ClassNotFoundException | SQLException | InterruptedException se) {
            se.printStackTrace();
        }
    }

    public static class Rabbit implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            System.out.println("Rabbit runs here ...");
            Connection store = (Connection) context.getJobDetail().getJobDataMap().get("store");
            try (PreparedStatement statement = store.prepareStatement(
                    "insert into rabbit(created_date) values (?)")) {
                statement.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
