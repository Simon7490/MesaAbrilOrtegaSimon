package ar.edu.unju.fi.service;

import java.util.List;
import java.util.Map;

import ar.edu.unju.fi.model.Compra;
import ar.edu.unju.fi.model.Cliente;
import ar.edu.unju.fi.model.Evento;

public interface ICompraService {
    List<Compra> listarCompras();
    Compra guardar(Compra compra);
    void actualizarCompra(Compra compra);
    Compra obtenerCompraPorId(Long id);
    Map<String, Double> obtenerRecaudacionPorEvento();
    List<Compra> obtenerComprasPorCliente(Cliente cliente);
    List<Compra> obtenerComprasPorEvento(Evento evento);
    void eliminar(Long id);
    int obtenerTicketsDisponibles(Long eventoId);
}
