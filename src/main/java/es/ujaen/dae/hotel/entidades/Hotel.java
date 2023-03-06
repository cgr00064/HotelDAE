package es.ujaen.dae.hotel.entidades;

import lombok.Data;
import org.springframework.scheduling.annotation.Scheduled;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class Hotel {

    private int id;

    @NotBlank
    private String nombre;

    @NotNull
    private Direccion direccion;

    @PositiveOrZero
    private int numSimp;

    @PositiveOrZero
    private int numDobl;

    private List<Reserva> reservasActuales;
    private int totalReservasActuales = 0;

    private List<Reserva> reservasPasadas;
    private int totalReservasPasadas = 0;

    public Hotel(int id, String nombre, Direccion direccion, int numDobl, int numSimp) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.numSimp = numSimp;
        this.numDobl = numDobl;
        reservasActuales = new ArrayList<>();
        reservasPasadas = new ArrayList<>();
    }


    public List<Reserva> verReservas(LocalDateTime fechaIni, LocalDateTime fechaFin) {
        List<Reserva> reservas = new ArrayList<>();
        for (Reserva reservasActuales : reservasActuales) {
            if (reservasActuales.getFechaInicio() == fechaIni && reservasActuales.getFechaFin() == fechaFin)
                reservas.add(reservasActuales);
        }
        return reservas;
    }
    public void addReserva(Reserva reserva){
        reserva.setId(totalReservasActuales++);
        reservasActuales.add(reserva);
    }

    public void setNumDobl(int numDobl) {
        this.numDobl -= numDobl;
    }

    public void setNumSimp(int numSimp) {
        this.numSimp -= numSimp;
    }


    //Trabajo Voluntario
    @Scheduled(cron="0 0 3 * * * ?")
    public void cambioReservar(){
        for (Reserva reservasActuale : reservasActuales) {
            if (reservasActuale.getFechaFin().isBefore(LocalDateTime.now()))
                reservasPasadas.add(reservasActuale);
        }
    }
}