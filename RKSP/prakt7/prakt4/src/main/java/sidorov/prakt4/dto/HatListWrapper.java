package sidorov.prakt4.dto;

import sidorov.prakt4.model.Hat;
import java.util.List;

public class HatListWrapper {
    private List<Hat> hats;
    public List<Hat> getHats() {
        return hats;
    }
    public void setHats(List<Hat> hats) {
        this.hats = hats;
    }
}
