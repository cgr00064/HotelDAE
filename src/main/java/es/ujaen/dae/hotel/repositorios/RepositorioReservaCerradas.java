package es.ujaen.dae.hotel.repositorios;

import es.ujaen.dae.hotel.entidades.Reserva;
import es.ujaen.dae.hotel.entidades.ReservaCerrada;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RepositorioReservaCerradas {
    @PersistenceContext
    EntityManager em;

    public void guardarReservaCerrada(ReservaCerrada reservaCerrada) {
        em.persist(reservaCerrada);
    }

    public void eliminarReserva(Reserva reserva) {
        em.remove(reserva);
    }
}
