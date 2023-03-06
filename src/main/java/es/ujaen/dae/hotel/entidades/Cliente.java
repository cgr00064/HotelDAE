package es.ujaen.dae.hotel.entidades;

import es.ujaen.dae.hotel.utils.ExprReg;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@RequiredArgsConstructor
public class Cliente {

    private int id;

    @NotBlank
    @Pattern(regexp = ExprReg.DNI)
    private final String dni;

    @NotBlank
    private final String nombre;

    @NotBlank
    private final String userName;

    @NotBlank
    private final String contraseña;

    @NotNull
    private final Direccion direccion;

    @Pattern(regexp = ExprReg.TLF)
    private final String tlf;

    @Email
    private final String email;

    private List<Reserva> reservas = new ArrayList<>();
    private int totalReservas = 0;

//    public Cliente(int id, String dni, String nombre, String userName, String contraseña, Direccion direccion, String tlf, String email){
//        this.id = id;
//        this.dni = dni;
//        this.nombre = nombre;
//        this.userName = userName;
//        this.contraseña = contraseña;
//        this.direccion = direccion;
//        this.tlf = tlf;
//        this.email = email;
//        reservas = new ArrayList<>();
//    }

    public List<Reserva> verReservas() {
        return Collections.unmodifiableList(reservas);
    }

    public Reserva verReserva(int idReserva) {
        return reservas.get(idReserva);
    }

    public boolean claveValida(String clave) {
        return contraseña.equals(clave);
    }

    public void addReserva(Reserva reserva) {
        reserva.setId(totalReservas++);
        reservas.add(reserva);
    }
}