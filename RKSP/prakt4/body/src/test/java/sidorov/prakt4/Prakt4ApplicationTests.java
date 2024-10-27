package sidorov.prakt4;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.rsocket.frame.decoder.PayloadDecoder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import sidorov.prakt4.model.Hat;
import sidorov.prakt4.repository.HatRepository;

import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class Prak4ApplicationTests {

	@Autowired
	private HatRepository hatRepository;
	private RSocketRequester requester;

	@BeforeEach
	public void setup() {
		requester = RSocketRequester.builder()
				.rsocketStrategies(builder -> builder.decoder(new Jackson2JsonDecoder()))
				.rsocketStrategies(builder -> builder.encoder(new Jackson2JsonEncoder()))
				.rsocketConnector(connector -> connector
						.payloadDecoder(PayloadDecoder.ZERO_COPY)
						.reconnect(Retry.fixedDelay(2, Duration.ofSeconds(2))))
				.dataMimeType(MimeTypeUtils.APPLICATION_JSON)
				.tcp("localhost", 5200);
	}

	@AfterEach
	public void cleanup() {
		requester.dispose();
	}

	@Test
	public void testGetHat() {
		Hat hat = new Hat();
		hat.setType("Fedora");
		hat.setColor("Black");
		hat.setSize(57.0f);
		hat.setPrice(150.0f);
		Hat savedHat = hatRepository.save(hat);
		Mono<Hat> result = requester.route("getHat")
				.data(savedHat.getId())
				.retrieveMono(Hat.class);
		assertNotNull(result.block());
	}

	@Test
	public void testAddHat() {
		Hat hat = new Hat();
		hat.setType("Fedora");
		hat.setColor("Black");
		hat.setSize(57.0f);
		hat.setPrice(150.0f);
		Mono<Hat> result = requester.route("addHat")
				.data(hat)
				.retrieveMono(Hat.class);
		Hat savedHat = result.block();
		assertNotNull(savedHat);
		assertNotNull(savedHat.getId());
		assertTrue(savedHat.getId() > 0);
	}

	@Test
	public void testGetHats() {
		Flux<Hat> result = requester.route("getHats")
				.retrieveFlux(Hat.class);
		assertNotNull(result.blockFirst());
	}

	@Test
	public void testDeleteHat() {
		Hat hat = new Hat();
		hat.setType("Fedora");
		hat.setColor("Black");
		hat.setSize(57.0f);
		hat.setPrice(150.0f);
		Hat savedHat = hatRepository.save(hat);
		Mono<Void> result = requester.route("deleteHat")
				.data(savedHat.getId())
				.send();
		result.block();
		Hat deletedHat = hatRepository.findHatById(savedHat.getId());
		assertNull(deletedHat);
	}
}
