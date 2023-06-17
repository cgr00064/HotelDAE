package es.ujaen.dae.hotel.entidades;

import es.ujaen.dae.hotel.utils.ExprReg;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
public class Cliente implements Serializable {

    @NotBlank
    @Id
    @Size(min=9, max=9)
    @Pattern(regexp = ExprReg.DNI)
    private String dni;

    @NotBlank
    private String nombre;

    @NotBlank
    private String userName;

    @NotBlank
    private String contraseña;

    @NotNull
    private Direccion direccion;

    @Pattern(regexp = ExprReg.TLF)
    private String tlf;

    @Email
    private String email;
/*
    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "cliente_id_reservas")
    private List<Reserva> reservas;
    private int totalReservas = 0;
*/
    public Cliente(String dni, String nombre, String userName, String contraseña, Direccion direccion, String tlf, String email){
        this.dni = dni;
        this.nombre = nombre;
        this.userName = userName;
        this.contraseña = contraseña;
        this.direccion = direccion;
        this.tlf = tlf;
        this.email = email;
        //reservas = new ArrayList<>();
    }
/*
    public List<Reserva> verReservas() {
        return Collections.unmodifiableList(reservas);
    }

    public Reserva verReserva(int idReserva) {
        return reservas.get(idReserva);
    }
*/
    public boolean claveValida(String clave) {
        return contraseña.equals(clave);
    }
}