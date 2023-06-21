package es.ujaen.dae.hotel.repositorios;

import es.ujaen.dae.hotel.entidades.Reserva;
import es.ujaen.dae.hotel.entidades.ReservasPasadas;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RepositorioReservasPasadas {
    @PersistenceContext
    EntityManager em;

    public void guardarReservaCerrada(ReservasPasadas reservasPasadas) {
        em.persist(reservasPasadas);
    }

    public void eliminarReserva(Reserva reserva) {
        em.remove(reserva);
    }
}
