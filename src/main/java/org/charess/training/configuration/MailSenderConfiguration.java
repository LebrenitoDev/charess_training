package org.charess.training.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@PropertySource("file:/home/lebrenito/.charess.mail.properties")
public class MailSenderConfiguration {

    private final Logger log = LoggerFactory.getLogger(MailSenderConfiguration.class);

    @Autowired
    private Environment environment;

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(){
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Bean
    public JavaMailSenderImpl javaMailSenderImpl(){
        final JavaMailSenderImpl sender = new JavaMailSenderImpl();
        try {
            sender.setHost("smtp.office365.com");
            sender.setPort(587);
            sender.setProtocol("smtp");
            sender.setUsername("charess@charess.org");
            sender.setPassword("Jaime@pprendre2022");
        } catch (IllegalStateException e){
            log.error("Could not resolve .charess.mail.properties. Check the file .charess.mail.properties");
            throw e;
        }

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtp.starttls.enable", true);
        sender.setJavaMailProperties(properties);
        return sender;
    }
}
