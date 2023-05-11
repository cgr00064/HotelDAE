package es.ujaen.dae.hotel.entidades;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
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

    public void moverReservasPasadasAHistorico() {
        List<Reserva> reservasActuales = this.getReservasActuales();
        List<Reserva> reservasPasadas = this.getReservasPasadas();

        // Mover las reservas pasadas a la lista de reservas históricas
        Iterator<Reserva> iterator = reservasActuales.iterator();
        while (iterator.hasNext()) {
            Reserva reserva = iterator.next();
            if (reserva.getFechaFin().isBefore(LocalDate.now().atStartOfDay())) {
                iterator.remove();
                this.setNumSimp(reserva.getNumHabitacionesSimp());
                this.setNumDobl(reserva.getNumHabitacionesDobl());
                reservasPasadas.add(reserva);
            }
        }
    }

    public record NumHabitaciones(int dobles, int simples) { }

    public boolean hayDisponibilidad(LocalDate fechaInicio, LocalDate fechaFin, int numHabitacionesDobles, int numHabitacionesSimples) {
        for (LocalDate fecha = fechaInicio; fecha.compareTo(fechaFin) <= 0; fecha = fecha.plusDays(1)) {
            NumHabitaciones numHabitaciones = getNumHabitacionesOcupadas(fecha);
            if (numHabitacionesDobles > numHabitaciones.dobles() || numHabitacionesSimples > numHabitaciones.simples()) {
                return false;
            }
            for (Reserva reserva : reservasActuales) {
                if (reserva.solapa(new Reserva(null, null, fecha.atStartOfDay(), fecha.atStartOfDay().plusDays(1), numHabitacionesDobles, numHabitacionesSimples))) {
                    return false;
                }
            }
        }
        return true;
    }

    private NumHabitaciones getNumHabitacionesOcupadas(LocalDate fecha) {
        int numDoblesOcupadas = 0;
        int numSimplesOcupadas = 0;
        for (Reserva reserva : reservasActuales) {
            if (reserva.solapa(new Reserva(null, null, fecha.atStartOfDay(), fecha.atStartOfDay(), 0, 0))) {
                numDoblesOcupadas += reserva.getNumHabitacionesDobl();
                numSimplesOcupadas += reserva.getNumHabitacionesSimp();
            }
        }
        return new NumHabitaciones(numDoblesOcupadas, numSimplesOcupadas);
    }


}



