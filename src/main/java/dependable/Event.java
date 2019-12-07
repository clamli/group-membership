package dependable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Event {

  public enum Type {
    SEND, RECEIVE
  }

  private Type type;
  private String command;
  private int time;
  private Player player;

  public Event(Type type, String command, int time) {
    this.type = type;
    this.command = command;
    this.time = time;
  }

  public Event(Type type, String command, int time, Player player) {
    this.type = type;
    this.command = command;
    this.time = time;
    this.player = player;
  }
}
