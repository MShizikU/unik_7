package sidorov.prakt4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sidorov.prakt4.model.Hat;
import sidorov.prakt4.repository.HatRepository;

@Controller
public class MainSocketController {
    private final HatRepository hatRepository;

    @Autowired
    public MainSocketController(HatRepository hatRepository) {
        this.hatRepository = hatRepository;
    }

    @MessageMapping("getHat")
    public Mono<Hat> getHat(Long id) {
        return Mono.justOrEmpty(hatRepository.findHatById(id));
    }

    @MessageMapping("addHat")
    public Mono<Hat> addHat(Hat hat) {
        return Mono.justOrEmpty(hatRepository.save(hat));
    }

    @MessageMapping("getHats")
    public Flux<Hat> getHats() {
        return Flux.fromIterable(hatRepository.findAll());
    }

    @MessageMapping("deleteHat")
    public Mono<Void> deleteHat(Long id) {
        Hat hat = hatRepository.findHatById(id);
        hatRepository.delete(hat);
        return Mono.empty();
    }

    @MessageMapping("hatChannel")
    public Flux<Hat> hatChannel(Flux<Hat> hats) {
        return hats.flatMap(hat -> Mono.fromCallable(() ->
                        hatRepository.save(hat)))
                .collectList()
                .flatMapMany(savedHats -> Flux.fromIterable(savedHats));
    }
}
