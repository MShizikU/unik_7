package mirea.sidorov.clients.controller;

import mirea.sidorov.clients.model.Client;
import mirea.sidorov.clients.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        return ResponseEntity.ok(clientService.createClient(client));
    }

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Long id, @RequestBody Client clientDetails) {
        return ResponseEntity.ok(clientService.updateClient(id, clientDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }
}

