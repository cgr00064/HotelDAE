package es.ujaen.dae.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HotelDaeApp {
    public static void main(String[] args) throws Exception{
        SpringApplication.run(HotelDaeApp.class, args);
    }
}
