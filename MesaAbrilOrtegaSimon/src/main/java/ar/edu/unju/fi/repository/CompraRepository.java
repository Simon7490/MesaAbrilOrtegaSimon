package ar.edu.unju.fi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ar.edu.unju.fi.model.Cliente;
import ar.edu.unju.fi.model.Compra;
import ar.edu.unju.fi.model.Evento;

@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {
    
    /**
     * Obtiene las compras de un cliente ordenadas por fecha
     */
    List<Compra> findByClienteOrderByFechaCompraDesc(Cliente cliente);
    
    /**
     * Obtiene las compras de un evento ordenadas por fecha
     */
    List<Compra> findByEventoOrderByFechaCompraDesc(Evento evento);
    
    /**
     * Cuenta la cantidad total de tickets vendidos para un evento
     */
    @Query("SELECT COALESCE(SUM(c.cantidadTickets), 0) FROM Compra c WHERE c.evento.id = :eventoId")
    int countTicketsVendidosPorEvento(@Param("eventoId") Long eventoId);
    
    /**
     * Obtiene el total recaudado por evento
     */
    @Query("SELECT c.evento.nombre, SUM(c.total) FROM Compra c GROUP BY c.evento.nombre")
    List<Object[]> findRecaudacionPorEvento();
}
