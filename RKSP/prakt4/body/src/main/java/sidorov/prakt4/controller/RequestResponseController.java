package sidorov.prakt4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import sidorov.prakt4.model.Hat;

@RestController
@RequestMapping("/api/hats")
public class RequestResponseController {
    private final RSocketRequester rSocketRequester;

    @Autowired
    public RequestResponseController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @GetMapping("/{id}")
    public Mono<Hat> getHat(@PathVariable Long id) {
        return rSocketRequester
                .route("getHat")
                .data(id)
                .retrieveMono(Hat.class);
    }

    @PostMapping
    public Mono<Hat> addHat(@RequestBody Hat hat) {
        return rSocketRequester
                .route("addHat")
                .data(hat)
                .retrieveMono(Hat.class);
    }
}
