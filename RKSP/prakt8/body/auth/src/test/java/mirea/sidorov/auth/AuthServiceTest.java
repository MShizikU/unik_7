package mirea.sidorov.auth;

import mirea.sidorov.auth.model.AuthToken;
import mirea.sidorov.auth.repository.AuthTokenRepository;
import mirea.sidorov.auth.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

	@Mock
	private AuthTokenRepository authTokenRepository;

	@InjectMocks
	private AuthService authService;

	private OAuth2User mockUser;
	private AuthToken savedAuthToken;

	@BeforeEach
	public void setUp() {
		// Создаём мок-объект OAuth2User
		mockUser = mock(OAuth2User.class);

		// Инициализируем AuthToken, который будет возвращаться репозиторием при сохранении
		savedAuthToken = new AuthToken();
		savedAuthToken.setId(1L);
		savedAuthToken.setUsername("testuser");
		savedAuthToken.setToken(UUID.randomUUID().toString());
	}

	@Test
	public void testGenerateToken_Success_Corrected() {
		// Arrange
		when(mockUser.getAttribute("login")).thenReturn("testuser");

		// Настраиваем репозиторий, чтобы он возвращал переданный AuthToken
		when(authTokenRepository.save(any(AuthToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		String token = authService.generateToken(mockUser);

		// Assert
		assertNotNull(token);

		// Проверяем, что метод save был вызван с правильными параметрами
		ArgumentCaptor<AuthToken> authTokenCaptor = ArgumentCaptor.forClass(AuthToken.class);
		verify(authTokenRepository, times(1)).save(authTokenCaptor.capture());

		AuthToken capturedAuthToken = authTokenCaptor.getValue();
		assertEquals("testuser", capturedAuthToken.getUsername());
		assertNotNull(capturedAuthToken.getToken());
		assertEquals(token, capturedAuthToken.getToken());
	}

	@Test
	public void testValidateToken_Found() {
		// Arrange
		String token = savedAuthToken.getToken();
		when(authTokenRepository.findByToken(token)).thenReturn(Optional.of(savedAuthToken));

		// Act
		Optional<AuthToken> result = authService.validateToken(token);

		// Assert
		assertTrue(result.isPresent());
		assertEquals(savedAuthToken, result.get());

		verify(authTokenRepository, times(1)).findByToken(token);
	}

	@Test
	public void testValidateToken_NotFound() {
		// Arrange
		String token = UUID.randomUUID().toString();
		when(authTokenRepository.findByToken(token)).thenReturn(Optional.empty());

		// Act
		Optional<AuthToken> result = authService.validateToken(token);

		// Assert
		assertFalse(result.isPresent());

		verify(authTokenRepository, times(1)).findByToken(token);
	}

	@Test
	public void testGenerateToken_UniqueToken() {
		// Arrange
		when(mockUser.getAttribute("login")).thenReturn("testuser");
		when(authTokenRepository.save(any(AuthToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

		// Act
		String token1 = authService.generateToken(mockUser);
		String token2 = authService.generateToken(mockUser);

		// Assert
		assertNotNull(token1);
		assertNotNull(token2);
		assertNotEquals(token1, token2);

		// Проверяем, что метод save был вызван дважды
		verify(authTokenRepository, times(2)).save(any(AuthToken.class));
	}
}