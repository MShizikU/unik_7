package sidorov.prakt4.controller;

import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sidorov.prakt4.model.Hat;

@RestController
@RequestMapping("/api/hats")
public class RequestStreamController {
    private final RSocketRequester rSocketRequester;

    @Autowired
    public RequestStreamController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @GetMapping
    public Publisher<Hat> getHats() {
        return rSocketRequester
                .route("getHats")
                .data(new Hat())
                .retrieveFlux(Hat.class);
    }
}
