package sidorov.prakt4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import sidorov.prakt4.exception.CustomException;
import sidorov.prakt4.model.Hat;
import sidorov.prakt4.repository.HatRepository;

@RestController
@RequestMapping("/hats")
public class HatController {
    private final HatRepository hatRepository;

    @Autowired
    public HatController(HatRepository hatRepository) {
        this.hatRepository = hatRepository;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Hat>> getHatById(@PathVariable Long id) {
        return hatRepository.findById(id)
                .map(hat -> ResponseEntity.ok(hat))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Hat> getAllHats(@RequestParam(name = "minsize", required = false) Float minSize) {
        Flux<Hat> hats = hatRepository.findAll();
        if (minSize != null && minSize > 0) {
            hats = hats.filter(hat -> hat.getSize() >= minSize);
        }
        return hats
                .map(this::transformHat)
                .onErrorResume(e -> Flux.error(new CustomException("Failed to fetch hats", e)))
                .onBackpressureBuffer();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Hat> createHat(@RequestBody Hat hat) {
        return hatRepository.save(hat);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Hat>> updateHat(@PathVariable Long id, @RequestBody Hat updatedHat) {
        return hatRepository.findById(id)
                .flatMap(existingHat -> {
                    existingHat.setType(updatedHat.getType());
                    existingHat.setColor(updatedHat.getColor());
                    existingHat.setSize(updatedHat.getSize());
                    existingHat.setPrice(updatedHat.getPrice());
                    return hatRepository.save(existingHat);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteHat(@PathVariable Long id) {
        return hatRepository.findById(id)
                .flatMap(existingHat ->
                        hatRepository.delete(existingHat)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    private Hat transformHat(Hat hat) {
        hat.setType(hat.getType().toUpperCase());
        return hat;
    }
}
