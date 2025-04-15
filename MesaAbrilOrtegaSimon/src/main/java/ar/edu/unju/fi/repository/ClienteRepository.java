package ar.edu.unju.fi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ar.edu.unju.fi.model.Cliente;
import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    /**
     * Busca un cliente por su email
     */
    Cliente findByEmail(String email);
    
    /**
     * Busca un cliente por su DNI
     */
    Cliente findByDni(String dni);
    
    /**
     * Obtiene todos los clientes que han realizado al menos una compra
     */
    @Query("SELECT DISTINCT c FROM Cliente c JOIN FETCH c.compras WHERE c.compras IS NOT EMPTY")
    List<Cliente> findClientesConCompras();
}
