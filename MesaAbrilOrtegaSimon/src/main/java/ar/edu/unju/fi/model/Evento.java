package ar.edu.unju.fi.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "eventos")
public class Evento {

    public Evento(Long id, String nombre, String descripcion, LocalDateTime fecha, String lugar, Double precio, Integer capacidad, Integer capacidadInicial, TipoEvento tipoEvento, List<Compra> compras) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.lugar = lugar;
        this.precio = precio;
        this.capacidad = capacidad;
        this.tipoEvento = tipoEvento;
        this.capacidadInicial = capacidadInicial != null ? capacidadInicial : capacidad;
        this.compras = compras != null ? compras : new ArrayList<>();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @NotNull(message = "La fecha es obligatoria")
    @Future(message = "La fecha debe ser futura")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(nullable = false)
    private LocalDateTime fecha;

    @NotBlank(message = "El lugar es obligatorio")
    @Column(nullable = false)
    private String lugar;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 0, message = "La capacidad no puede ser negativa")
    @Column(nullable = false)
    private Integer capacidad;

    @NotNull(message = "El tipo de evento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipoEvento;

    @Column(name = "capacidad_inicial", nullable = false)
    private Integer capacidadInicial;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Compra> compras = new ArrayList<>();

    public Evento() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public void setTipoEvento(TipoEvento tipoEvento) {
        this.tipoEvento = tipoEvento;
    }

    public Integer getCapacidadInicial() {
        return capacidadInicial;
    }

    public void setCapacidadInicial(Integer capacidadInicial) {
        this.capacidadInicial = capacidadInicial;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }
}
