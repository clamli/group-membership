package dependable;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Group {
  TreeSet<Player> players = new TreeSet<>((Player p1, Player p2) -> {return p1.getId() - p2.getId();});;
  public Group(Player player) {
    this.players.add(player);
  }

  public Group() {
  }
  public Group(Set<Player> players) {
    this.players.addAll(players);
  }

  public int size() {
    return this.players.size();
  }

  public void print() {
    for (Player player : players) {
      System.out.print(player + ",");
    }
    System.out.println();
  }

}
