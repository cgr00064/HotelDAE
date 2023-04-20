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

    public void addReserva(Reserva reserva){
        reserva.setId(totalReservasActuales++);
        reservasActuales.add(reserva);
    }

    public int getNumSimp() {
        return numSimp;
    }

    public int getNumDobl() {
        return numDobl;
    }

    public void setNumDobl(int numDobl) {
        this.numDobl -= numDobl;
    }

    public void setNumSimp(int numSimp) {
        this.numSimp -= numSimp;
    }

    public List<Reserva> getReservasHistoricas() {
        return reservasPasadas;
    }

    /*
    numHabitacionesSimp() calcula el número de habitaciones simples disponibles restando el
    número total de habitaciones simples del hotel al número de habitaciones simples ya
    reservadas en las reservas actuales del hotel.
     */
    public int numHabitacionesSimp() {
        int numReservasSimples = reservasActuales.stream()
                .mapToInt(Reserva::getNumHabitacionesSimp)
                .sum();
        return numSimp - numReservasSimples;
    }

    /*
    Similar a numHabitacionesSimp()
     */
    public int numDoblDisponibles() {
        int numReservasDobles = reservasActuales.stream()
                .mapToInt(Reserva::getNumHabitacionesDobl)
                .sum();
        return numDobl - numReservasDobles;
    }

}