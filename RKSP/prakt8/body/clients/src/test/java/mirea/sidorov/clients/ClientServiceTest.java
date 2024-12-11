package mirea.sidorov.clients;

import mirea.sidorov.clients.model.Client;
import mirea.sidorov.clients.repository.ClientRepository;
import mirea.sidorov.clients.service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

	@Mock
	private ClientRepository clientRepository;

	@InjectMocks
	private ClientService clientService;

	private Client client1;
	private Client client2;

	@BeforeEach
	public void setUp() {
		client1 = new Client();
		client1.setId(1L);
		client1.setName("Иван Иванов");
		client1.setEmail("ivan@example.com");

		client2 = new Client();
		client2.setId(2L);
		client2.setName("Мария Петрова");
		client2.setEmail("maria@example.com");
	}

	@Test
	public void testCreateClient() {
		when(clientRepository.save(any(Client.class))).thenReturn(client1);

		Client created = clientService.createClient(client1);

		assertNotNull(created);
		assertEquals(client1.getId(), created.getId());
		assertEquals(client1.getName(), created.getName());
		assertEquals(client1.getEmail(), created.getEmail());

		verify(clientRepository, times(1)).save(client1);
	}

	@Test
	public void testGetAllClients() {
		List<Client> clients = Arrays.asList(client1, client2);
		when(clientRepository.findAll()).thenReturn(clients);

		List<Client> result = clientService.getAllClients();

		assertEquals(2, result.size());
		assertTrue(result.contains(client1));
		assertTrue(result.contains(client2));

		verify(clientRepository, times(1)).findAll();
	}

	@Test
	public void testGetClientById_Found() {
		when(clientRepository.findById(1L)).thenReturn(Optional.of(client1));

		Client found = clientService.getClientById(1L);

		assertNotNull(found);
		assertEquals(client1.getId(), found.getId());
		assertEquals(client1.getName(), found.getName());

		verify(clientRepository, times(1)).findById(1L);
	}

	@Test
	public void testGetClientById_NotFound() {
		when(clientRepository.findById(3L)).thenReturn(Optional.empty());

		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			clientService.getClientById(3L);
		});

		assertEquals("Client not found", exception.getMessage());

		verify(clientRepository, times(1)).findById(3L);
	}

	@Test
	public void testUpdateClient() {
		Client updatedDetails = new Client();
		updatedDetails.setName("Иван Смирнов");
		updatedDetails.setEmail("ivan.smirnov@example.com");

		when(clientRepository.findById(1L)).thenReturn(Optional.of(client1));
		when(clientRepository.save(any(Client.class))).thenReturn(client1);

		Client updated = clientService.updateClient(1L, updatedDetails);

		assertNotNull(updated);
		assertEquals(client1.getId(), updated.getId());
		assertEquals("Иван Смирнов", updated.getName());
		assertEquals("ivan.smirnov@example.com", updated.getEmail());

		verify(clientRepository, times(1)).findById(1L);
		verify(clientRepository, times(1)).save(client1);
	}

	@Test
	public void testDeleteClient() {
		doNothing().when(clientRepository).deleteById(1L);

		clientService.deleteClient(1L);

		verify(clientRepository, times(1)).deleteById(1L);
	}
}

