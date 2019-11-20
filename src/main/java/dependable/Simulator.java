package dependable;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Simulator {

    // List of all players
    private List<Player> players;

    // Current group
    private Group group;

    private class Data {

    }

    private Data data;

    private boolean shouldFormGroup;

    public Simulator(List<Player> players) {
        this.players = players;
        this.shouldFormGroup = true;
        this.group = new Group();
        this.data = new Data();
    }

    public void execute() {

    }

    
}
