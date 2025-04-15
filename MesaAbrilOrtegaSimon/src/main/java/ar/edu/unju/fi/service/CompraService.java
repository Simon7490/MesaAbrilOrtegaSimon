package ar.edu.unju.fi.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.model.Cliente;
import ar.edu.unju.fi.model.Compra;
import ar.edu.unju.fi.model.Evento;
import ar.edu.unju.fi.repository.CompraRepository;
import ar.edu.unju.fi.repository.EventoRepository;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private EventoRepository eventoRepository;

    /**
     * Lista todas las compras realizadas
     */
    public List<Compra> listarCompras() {
        return compraRepository.findAll();
    }

    /**
     * Realiza una nueva compra validando disponibilidad y calculando el total
     */
    @Transactional
    public Compra realizarCompra(Compra compra) {
        // Validar que el evento exista
        Evento evento = eventoRepository.findById(compra.getEvento().getId())
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));

        // Validar que el evento no haya pasado
        if (evento.getFecha().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("El evento ya ha pasado");
        }

        // Validar disponibilidad de tickets
        int ticketsVendidos = compraRepository.countTicketsVendidosPorEvento(evento.getId());
        int ticketsDisponibles = evento.getCapacidad() - ticketsVendidos;

        if (compra.getCantidadTickets() > ticketsDisponibles) {
            throw new RuntimeException("No hay suficientes tickets disponibles. Quedan " + ticketsDisponibles + " tickets.");
        }

        // Calcular total
        compra.setTotal(evento.getPrecio() * compra.getCantidadTickets());
        compra.setFechaCompra(LocalDateTime.now());

        return compraRepository.save(compra);
    }

    /**
     * Obtiene una compra por su ID
     */
    public Compra obtenerCompraPorId(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compra no encontrada"));
    }

    /**
     * Calcula la recaudación total por evento
     */
    public Map<String, Double> obtenerRecaudacionPorEvento() {
        List<Object[]> resultados = compraRepository.findRecaudacionPorEvento();
        Map<String, Double> recaudacion = new HashMap<>();
        
        for (Object[] resultado : resultados) {
            String nombreEvento = (String) resultado[0];
            Double total = (Double) resultado[1];
            recaudacion.put(nombreEvento, total);
        }
        
        return recaudacion;
    }

    /**
     * Obtiene las compras de un cliente específico
     */
    public List<Compra> obtenerComprasPorCliente(Cliente cliente) {
        return compraRepository.findByClienteOrderByFechaCompraDesc(cliente);
    }

    /**
     * Obtiene las compras de un evento específico
     */
    public List<Compra> obtenerComprasPorEvento(Evento evento) {
        return compraRepository.findByEventoOrderByFechaCompraDesc(evento);
    }

    /**
     * Verifica la disponibilidad de tickets para un evento
     */
    public int obtenerTicketsDisponibles(Long eventoId) {
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado"));
        
        int ticketsVendidos = compraRepository.countTicketsVendidosPorEvento(eventoId);
        return evento.getCapacidad() - ticketsVendidos;
    }
}
