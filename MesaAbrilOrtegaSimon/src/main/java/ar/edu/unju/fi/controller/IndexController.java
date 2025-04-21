package ar.edu.unju.fi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import ar.edu.unju.fi.service.ClienteService;
import ar.edu.unju.fi.service.CompraService;
import ar.edu.unju.fi.service.EventoService;

@Controller
public class IndexController {

    @Autowired
    private EventoService eventoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private CompraService compraService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("eventos", eventoService.listarEventos());
        model.addAttribute("clientes", clienteService.listarClientes());
        model.addAttribute("compras", compraService.listarCompras());
        return "index"; // Este es el nombre del archivo index.html en /templates
    }
}
