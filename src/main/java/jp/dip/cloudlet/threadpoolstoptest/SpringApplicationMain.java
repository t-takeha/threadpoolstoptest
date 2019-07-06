package jp.dip.cloudlet.threadpoolstoptest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
public class SpringApplicationMain {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringApplicationMain.class, args);
    }
}
