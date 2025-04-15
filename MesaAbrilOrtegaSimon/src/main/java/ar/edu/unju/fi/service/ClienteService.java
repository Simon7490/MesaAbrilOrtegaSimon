package ar.edu.unju.fi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.unju.fi.model.Cliente;
import ar.edu.unju.fi.repository.ClienteRepository;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Lista todos los clientes activos en el sistema.
     */
    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    /**
     * Guarda un nuevo cliente o actualiza uno existente.
     * Valida que el email y DNI no estén duplicados.
     */
    @Transactional
    public Cliente guardarCliente(Cliente cliente) {
        // Validar email único
        Cliente clienteExistenteEmail = clienteRepository.findByEmail(cliente.getEmail());
        if (clienteExistenteEmail != null && !clienteExistenteEmail.getId().equals(cliente.getId())) {
            throw new RuntimeException("Ya existe un cliente con ese email");
        }

        // Validar DNI único
        Cliente clienteExistenteDni = clienteRepository.findByDni(cliente.getDni());
        if (clienteExistenteDni != null && !clienteExistenteDni.getId().equals(cliente.getId())) {
            throw new RuntimeException("Ya existe un cliente con ese DNI");
        }

        return clienteRepository.save(cliente);
    }

    /**
     * Obtiene un cliente por su ID.
     * Lanza excepción si no existe.
     */
    public Cliente obtenerClientePorId(Long id) {
        return clienteRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + id));
    }

    /**
     * Elimina un cliente verificando que exista.
     */
    @Transactional
    public void eliminarCliente(Long id) {
        Cliente cliente = obtenerClientePorId(id);
        if (!cliente.getCompras().isEmpty()) {
            throw new RuntimeException("No se puede eliminar un cliente con compras realizadas");
        }
        clienteRepository.delete(cliente);
    }

    /**
     * Obtiene la lista de clientes que han realizado compras.
     */
    public List<Cliente> listarClientesConCompras() {
        return clienteRepository.findClientesConCompras();
    }

    /**
     * Actualiza los datos de un cliente existente.
     */
    @Transactional
    public Cliente actualizarCliente(Long id, Cliente clienteActualizado) {
        Cliente clienteExistente = obtenerClientePorId(id);
        
        clienteExistente.setNombre(clienteActualizado.getNombre());
        clienteExistente.setApellido(clienteActualizado.getApellido());
        clienteExistente.setEmail(clienteActualizado.getEmail());
        clienteExistente.setDni(clienteActualizado.getDni());

        return guardarCliente(clienteExistente);
    }
}
