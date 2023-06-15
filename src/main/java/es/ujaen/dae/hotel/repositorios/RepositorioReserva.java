package es.ujaen.dae.hotel.repositorios;


import es.ujaen.dae.hotel.entidades.Reserva;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Repository
@Transactional(propagation = Propagation.REQUIRED)
public class RepositorioReserva {
    @PersistenceContext
    EntityManager em;

    public void guardarReserva(Reserva reserva) {
        em.persist(reserva);
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public Optional<Reserva> buscarReservaPorId(int id){
        return Optional.ofNullable(em.find(Reserva.class, id));
    }
    public void actualizarReserva(Reserva reserva){
        em.merge(reserva);
    }
}