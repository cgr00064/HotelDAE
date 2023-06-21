package es.ujaen.dae.hotel.entidades;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ReservasPasadas implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "cliente_ID")
    private Cliente cliente;

    @DateTimeFormat
    private LocalDateTime fechaInicio;

    @DateTimeFormat
    private LocalDateTime fechaFin;

    @PositiveOrZero
    private int numHabitacionesSimp;

    @PositiveOrZero
    private int numHabitacionesDobl;

    public ReservasPasadas(Reserva reserva) {
        this.cliente = reserva.getCliente();
        this.fechaInicio = reserva.getFechaInicio();
        this.fechaFin = reserva.getFechaFin();
        this.numHabitacionesSimp = reserva.getNumHabitacionesSimp();
        this.numHabitacionesDobl = reserva.getNumHabitacionesDobl();
    }
}
