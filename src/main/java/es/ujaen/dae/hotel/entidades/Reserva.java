package es.ujaen.dae.hotel.entidades;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Entity
public class Reserva implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    @ManyToOne
    @JoinColumn(name = "cliente_ID")
    private Cliente cliente; // referencia al cliente que hace la reserva

    @DateTimeFormat
    private LocalDateTime fechaInicio;

    @DateTimeFormat
    private LocalDateTime fechaFin;

    @PositiveOrZero
    private int numHabitacionesSimp;

    @PositiveOrZero
    private int numHabitacionesDobl;

    public Reserva(Cliente cliente, LocalDateTime fechaInicio, LocalDateTime fechaFin, int numHabitacionesSimp, int numHabitacionesDobl) {
        this.cliente = cliente;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.numHabitacionesSimp = numHabitacionesSimp;
        this.numHabitacionesDobl = numHabitacionesDobl;
    }

    public boolean solapa(LocalDateTime dia) {
        //System.out.println("Salida: "+ ((dia.equals(this.fechaInicio) || dia.isAfter(this.fechaInicio) ) &&
                //(dia.equals(this.fechaFin) || dia.isBefore(this.fechaFin))));
        return ((dia.equals(this.fechaInicio) || dia.isAfter(this.fechaInicio) ) &&
                (dia.equals(this.fechaFin) || dia.isBefore(this.fechaFin)));


    }

}
