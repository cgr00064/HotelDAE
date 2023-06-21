package es.ujaen.dae.hotel.entidades;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Data
@Entity
@NoArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank
    private String nombre;

    @Embedded
    @NotNull
    private Direccion direccion;

    @PositiveOrZero
    private int numSimp;

    @PositiveOrZero
    private int numDobl;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "hotel_id_reservas_actuales")
    private List<Reserva> reservasActuales;
    private int totalReservasActuales = 0;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "hotel_id_reservas_pasadas")
    private Set<ReservasPasadas> reservasPasadas;
    private int totalReservasPasadas = 0;


    public Hotel(String nombre, Direccion direccion, int numDobl, int numSimp) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.numSimp = numSimp;
        this.numDobl = numDobl;
        reservasActuales = new ArrayList<>();
        reservasPasadas = new HashSet<>();
    }

    public void addReserva(Reserva reserva){
        reserva.setId(totalReservasActuales);
        reservasActuales.add(reserva);
        totalReservasActuales++;
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


    public void moverReservasPasadasAHistorico() {
        LocalDate currentDate = LocalDate.now();
        List<Reserva> reservasActuales = this.getReservasActuales();
        Set<Reserva> reservasAMover = new HashSet<>();
        Set<ReservasPasadas> reservasPasadas = this.getReservasPasadas();

        reservasActuales.removeIf(reserva -> {
            if (reserva.getFechaFin().isBefore(currentDate.atStartOfDay())) {
                reservasAMover.add(reserva);
                return true;
            }
            return false;
        });

        for (Reserva reserva : reservasAMover) {
            ReservasPasadas reservaPasada = new ReservasPasadas(reserva);
            reservasPasadas.add(reservaPasada);
        }

        // Actualizar los contadores de habitaciones simples y dobles
        int numSimp = 0;
        int numDobl = 0;
        for (Reserva reserva : reservasActuales) {
            numSimp += reserva.getNumHabitacionesSimp();
            numDobl += reserva.getNumHabitacionesDobl();
        }
        this.setNumSimp(numSimp);
        this.setNumDobl(numDobl);

        // Actualizar las listas de reservas actuales y pasadas después de completar el bucle
        this.setReservasActuales(reservasActuales);
        this.setReservasPasadas(reservasPasadas);
    }




    public record NumHabitaciones(int dobles, int simples) { }

    private NumHabitaciones habitacionesOcupadasEnDia(LocalDateTime dia) {

        int doblesOcupadas = 0;
        int simplesOcupadas = 0;

        for (Reserva reserva : reservasActuales) {
            if(reserva.solapa(dia)) {
                doblesOcupadas += reserva.getNumHabitacionesDobl();
                simplesOcupadas += reserva.getNumHabitacionesSimp();
            }
        }
        //System.out.println("dobles: "+doblesOcupadas+" simples: "+simplesOcupadas);
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


