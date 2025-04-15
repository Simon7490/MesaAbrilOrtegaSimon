package ar.edu.unju.fi.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @NotNull(message = "El evento es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;

    @NotNull(message = "La cantidad de tickets es obligatoria")
    @Min(value = 1, message = "Debe comprar al menos 1 ticket")
    @Column(nullable = false)
    private Integer cantidadTickets;

    @NotNull(message = "El total es obligatorio")
    @Positive(message = "El total debe ser mayor a 0")
    @Column(nullable = false)
    private Double total;

    @Column(nullable = false)
    private LocalDateTime fechaCompra = LocalDateTime.now();

    public Compra() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Integer getCantidadTickets() {
        return cantidadTickets;
    }

    public void setCantidadTickets(Integer cantidadTickets) {
        this.cantidadTickets = cantidadTickets;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public LocalDateTime getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDateTime fechaCompra) {
        this.fechaCompra = fechaCompra;
    }
}
