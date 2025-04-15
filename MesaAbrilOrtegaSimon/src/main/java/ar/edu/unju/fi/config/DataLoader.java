package ar.edu.unju.fi.config;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import ar.edu.unju.fi.model.Evento;
import ar.edu.unju.fi.model.TipoEvento;
import ar.edu.unju.fi.repository.EventoRepository;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private EventoRepository eventoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Solo cargar datos si no hay eventos en la base de datos
        if (eventoRepository.count() == 0) {
            cargarEventos();
        }
    }

    private void cargarEventos() {
        List<Evento> eventos = Arrays.asList(
            // Recitales
            new Evento(null, "Coldplay en Argentina", 
                "Coldplay regresa a Argentina con su gira Music of the Spheres World Tour", 
                LocalDateTime.now().plusMonths(2), 
                "Estadio River Plate", 
                15000.0, 
                50000,
                50000,
                TipoEvento.RECITAL,
                null),
            
            new Evento(null, "Metallica en Buenos Aires", 
                "Metallica presenta su nuevo álbum 72 Seasons", 
                LocalDateTime.now().plusMonths(3), 
                "Estadio Vélez Sarsfield", 
                12000.0, 
                40000,
                40000,
                TipoEvento.RECITAL,
                null),

            // Obras de Teatro
            new Evento(null, "El Fantasma de la Ópera", 
                "El clásico musical de Andrew Lloyd Webber llega a Argentina", 
                LocalDateTime.now().plusWeeks(3), 
                "Teatro Colón", 
                8000.0, 
                2000,
                2000,
                TipoEvento.OBRA_DE_TEATRO,
                null),
            
            new Evento(null, "Hamlet", 
                "La obra maestra de Shakespeare en una nueva producción", 
                LocalDateTime.now().plusWeeks(4), 
                "Teatro San Martín", 
                5000.0, 
                1500,
                1500,
                TipoEvento.OBRA_DE_TEATRO,
                null),

            // Stand Up
            new Evento(null, "Seinfeld en Argentina", 
                "Jerry Seinfeld presenta su nuevo show de stand up", 
                LocalDateTime.now().plusWeeks(6), 
                "Teatro Gran Rex", 
                10000.0, 
                3000,
                3000,
                TipoEvento.STAND_UP,
                null),
            
            new Evento(null, "Noche de Comedia", 
                "Los mejores comediantes argentinos juntos en un show único", 
                LocalDateTime.now().plusWeeks(2), 
                "Teatro Opera", 
                4000.0, 
                2500,
                2500,
                TipoEvento.STAND_UP,
                null)
        );

        eventoRepository.saveAll(eventos);
    }
}
