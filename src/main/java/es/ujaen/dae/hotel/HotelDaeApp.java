package es.ujaen.dae.hotel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages={
        "es.ujaen.dae.hotel.servicios",
        "es.ujaen.dae.hotel.repositorios"
})
@EntityScan(basePackages="es.ujaen.dae.hotel.entidades")
public class HotelDaeApp {
    public static void main(String[] args) throws Exception{
        SpringApplication.run(HotelDaeApp.class, args);
    }
}
