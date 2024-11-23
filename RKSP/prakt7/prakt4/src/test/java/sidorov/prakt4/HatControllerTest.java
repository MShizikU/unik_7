package sidorov.prakt4;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sidorov.prakt4.controller.HatController;
import sidorov.prakt4.model.Hat;
import sidorov.prakt4.repository.HatRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class HatControllerTest {

    @Test
    public void testGetHatById() {
        // Создайте фиктивную шляпу
        Hat hat = new Hat();
        hat.setId(1L);
        hat.setType("Fedora");

        // Создайте мок репозитория
        HatRepository hatRepository = Mockito.mock(HatRepository.class);
        when(hatRepository.findById(1L)).thenReturn(Mono.just(hat));

        // Создайте экземпляр контроллера
        HatController hatController = new HatController(hatRepository);

        // Вызовите метод контроллера и проверьте результат
        ResponseEntity<Hat> response = hatController.getHatById(1L).block();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(hat, response.getBody());
    }

    @Test
    public void testGetAllHats() {
        // Создайте список фиктивных шляп
        Hat hat1 = new Hat();
        hat1.setId(1L);
        hat1.setType("Fedora");
        hat1.setSize(58.0f);

        Hat hat2 = new Hat();
        hat2.setId(2L);
        hat2.setType("Beanie");
        hat2.setSize(56.0f);

        // Создайте мок репозитория
        HatRepository hatRepository = Mockito.mock(HatRepository.class);
        when(hatRepository.findAll()).thenReturn(Flux.just(hat1, hat2));

        // Создайте экземпляр контроллера
        HatController hatController = new HatController(hatRepository);

        // Вызовите метод контроллера и проверьте результат
        Flux<Hat> response = hatController.getAllHats(null);
        assertEquals(2, response.collectList().block().size());
    }

    @Test
    public void testCreateHat() {
        // Создайте фиктивную шляпу
        Hat hat = new Hat();
        hat.setId(1L);
        hat.setType("Fedora");

        // Создайте мок репозитория
        HatRepository hatRepository = Mockito.mock(HatRepository.class);
        when(hatRepository.save(hat)).thenReturn(Mono.just(hat));

        // Создайте экземпляр контроллера
        HatController hatController = new HatController(hatRepository);

        // Вызовите метод контроллера и проверьте результат
        Mono<Hat> response = hatController.createHat(hat);
        assertEquals(hat, response.block());
    }

    @Test
    public void testUpdateHat() {
        // Создайте фиктивную шляпу
        Hat existingHat = new Hat();
        existingHat.setId(1L);
        existingHat.setType("Fedora");
        // Создайте фиктивную обновленную шляпу
        Hat updatedHat = new Hat();
        updatedHat.setId(1L);
        updatedHat.setType("Beret");
        // Создайте мок репозитория
        HatRepository hatRepository = Mockito.mock(HatRepository.class);
        when(hatRepository.findById(1L)).thenReturn(Mono.just(existingHat));
        when(hatRepository.save(existingHat)).thenReturn(Mono.just(updatedHat));

        // Создайте экземпляр контроллера
        HatController hatController = new HatController(hatRepository);

        // Вызовите метод контроллера и проверьте результат
        ResponseEntity<Hat> response = hatController.updateHat(1L, updatedHat).block();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedHat, response.getBody());
    }

    @Test
    public void testDeleteHat() {
        // Создайте фиктивную шляпу
        Hat hat = new Hat();
        hat.setId(1L);
        hat.setType("Fedora");

        // Создайте мок репозитория
        HatRepository hatRepository = Mockito.mock(HatRepository.class);
        when(hatRepository.findById(1L)).thenReturn(Mono.just(hat));
        when(hatRepository.delete(hat)).thenReturn(Mono.empty());

        // Создайте экземпляр контроллера
        HatController hatController = new HatController(hatRepository);

        // Вызовите метод контроллера и проверьте результат
        ResponseEntity<Void> response = hatController.deleteHat(1L).block();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}

