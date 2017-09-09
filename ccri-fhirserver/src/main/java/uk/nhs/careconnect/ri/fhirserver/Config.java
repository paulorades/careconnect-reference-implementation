package uk.nhs.careconnect.ri.fhirserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by kevinmayfield on 21/07/2017.
 */

//@PropertySource("classpath:logging.properties")

@Configuration
@EnableTransactionManagement()
@PropertySource("classpath:logging.properties")
@ComponentScan(basePackages = "uk.nhs.careconnect.ri")
public class Config {

    @Value("${datasource.cleardown.cron:0 19 21 * * *}")
    private String cron;
}
