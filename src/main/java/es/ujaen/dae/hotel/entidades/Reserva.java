package es.ujaen.dae.hotel.entidades;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class Reserva {

    int id;
    @NotNull
    private final Cliente cliente; // referencia al cliente que hace la reserva

    @NotNull
    private final Direccion direccion;

    @DateTimeFormat
    private final LocalDateTime fechaInicio;

    @DateTimeFormat
    private final LocalDateTime fechaFin;

    @PositiveOrZero
    private final int numHabitacionesSimp;

    @PositiveOrZero
    private final int numHabitacionesDobl;

    /*
     verifica si la reserva actual se solapa con otra reserva recibida como parámetro.
     Si se solapa, retorna true, de lo contrario, retorna false.
     */
    public boolean solapa(Reserva otraReserva) {
        return (fechaInicio.isBefore(otraReserva.getFechaFin())
                && otraReserva.getFechaInicio().isBefore(fechaFin));
    }





}