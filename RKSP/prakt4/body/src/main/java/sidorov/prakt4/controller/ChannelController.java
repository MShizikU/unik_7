package sidorov.prakt4.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import sidorov.prakt4.dto.HatListWrapper;
import sidorov.prakt4.model.Hat;

import java.util.List;

@RestController
@RequestMapping("/api/hats")
public class ChannelController {
    private final RSocketRequester rSocketRequester;

    @Autowired
    public ChannelController(RSocketRequester rSocketRequester) {
        this.rSocketRequester = rSocketRequester;
    }

    @PostMapping("/exp")
    public Flux<Hat> addHatsMultiple(@RequestBody HatListWrapper hatListWrapper){
        List<Hat> hatList = hatListWrapper.getHats();
        Flux<Hat> hats = Flux.fromIterable(hatList);
        return rSocketRequester
                .route("hatChannel")
                .data(hats)
                .retrieveFlux(Hat.class);
    }
}