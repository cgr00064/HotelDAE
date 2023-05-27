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

    private NumHabitaciones habitacionesOcupadasEnDia(LocalDateTime dia) {

        System.out.println("Reservas actuales: "+ reservasActuales.size());

        //if(reservasActuales.isEmpty())
        //    return new NumHabitaciones(0, 0);

        int doblesOcupadas = 0;
        int simplesOcupadas = 0;

        for (Reserva reserva : reservasActuales) {
            if(reserva.solapa(dia)) {
                doblesOcupadas += reserva.getNumHabitacionesDobl();
                simplesOcupadas += reserva.getNumHabitacionesSimp();
            }
        }
        System.out.println("dobles: "+doblesOcupadas+" simples: "+simplesOcupadas);
        return new NumHabitaciones(doblesOcupadas, simplesOcupadas);
    }
    public boolean hayDisponibilidad(
            LocalDateTime fechaInicio, LocalDateTime fechaFin,
            int numHabitacionesDobl, int numHabitacionesSimp) {

        LocalDateTime dia = fechaInicio;

        while (!dia.isAfter(fechaFin)) {

            NumHabitaciones habitacionesOcupadas = habitacionesOcupadasEnDia(dia);

            int doblesDisponibles = getNumDobl() - habitacionesOcupadas.dobles();
            int simplesDisponibles = getNumSimp() - habitacionesOcupadas.simples();

            if (doblesDisponibles < numHabitacionesDobl || simplesDisponibles < numHabitacionesSimp) {
                return false;
            }

            dia = dia.plusDays(1);
        }

        return true;
    }
}



