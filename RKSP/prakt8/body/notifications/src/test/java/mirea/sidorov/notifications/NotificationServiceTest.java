package mirea.sidorov.notifications;

import mirea.sidorov.notifications.service.NotificationService;
import org.junit.jupiter.api.Test;
import mirea.sidorov.notifications.dto.Client;
import mirea.sidorov.notifications.feign.ClientServiceFeignClient;
import mirea.sidorov.notifications.model.Notification;
import mirea.sidorov.notifications.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {
	//Sidorov
	@Mock
	private NotificationRepository notificationRepository;

	@Mock
	private ClientServiceFeignClient clientServiceFeignClient;

	@InjectMocks
	private NotificationService notificationService;

	private Notification notification1;
	private Notification notification2;

	@BeforeEach
	public void setUp() {
		notification1 = new Notification();
		notification1.setId(1L);
		notification1.setClientId(100L);
		notification1.setMessage("Payment of $250.0 was successfully processed.");

		notification2 = new Notification();
		notification2.setId(2L);
		notification2.setClientId(101L);
		notification2.setMessage("Your subscription has been renewed.");
	}
	//Sidorov
	@Test
	public void testCreateNotification_Success() {
		// Arrange
		Client client = new Client(100L, "Иван Иванов", "ivan@example.com");
		when(clientServiceFeignClient.getClientById(notification1.getClientId()))
				.thenReturn(new ResponseEntity<>(client, HttpStatus.OK));
		when(notificationRepository.save(any(Notification.class))).thenReturn(notification1);

		// Act
		Notification createdNotification = notificationService.createNotification(notification1);

		// Assert
		assertNotNull(createdNotification);
		assertEquals(notification1.getId(), createdNotification.getId());
		assertEquals(notification1.getClientId(), createdNotification.getClientId());
		assertEquals(notification1.getMessage(), createdNotification.getMessage());

		verify(clientServiceFeignClient, times(1)).getClientById(notification1.getClientId());
		verify(notificationRepository, times(1)).save(notification1);
	}

	@Test
	public void testCreateNotification_ClientNotFound() {
		// Arrange
		when(clientServiceFeignClient.getClientById(notification1.getClientId()))
				.thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

		// Act & Assert
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			notificationService.createNotification(notification1);
		});

		assertEquals("Client does not exist", exception.getMessage());

		verify(clientServiceFeignClient, times(1)).getClientById(notification1.getClientId());
		verify(notificationRepository, never()).save(any(Notification.class));
	}

	@Test
	public void testGetAllNotifications() {
		// Arrange
		List<Notification> notifications = Arrays.asList(notification1, notification2);
		when(notificationRepository.findAll()).thenReturn(notifications);

		// Act
		List<Notification> result = notificationService.getAllNotifications();

		// Assert
		assertEquals(2, result.size());
		assertTrue(result.contains(notification1));
		assertTrue(result.contains(notification2));

		verify(notificationRepository, times(1)).findAll();
	}

	@Test
	public void testGetNotificationById_Found() {
		// Arrange
		when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification1));

		// Act
		Notification foundNotification = notificationService.getNotificationById(1L);

		// Assert
		assertNotNull(foundNotification);
		assertEquals(notification1.getId(), foundNotification.getId());
		assertEquals(notification1.getClientId(), foundNotification.getClientId());
		assertEquals(notification1.getMessage(), foundNotification.getMessage());

		verify(notificationRepository, times(1)).findById(1L);
	}

	@Test
	public void testGetNotificationById_NotFound() {
		// Arrange
		when(notificationRepository.findById(3L)).thenReturn(Optional.empty());

		// Act & Assert
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			notificationService.getNotificationById(3L);
		});

		assertEquals("Notification not found", exception.getMessage());

		verify(notificationRepository, times(1)).findById(3L);
	}
}

