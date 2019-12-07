package dependable;

import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.util.*;

@Getter
@Setter
public abstract class Simulator {
  // List of all players
  protected List<Player> players;
  protected Player ballHolder;
  protected int msg = 0;
  protected int passballTimes = 0;
  protected int formNewGroupTimes = 1;

  protected TreeMap<Integer, Map<Player, List<Event>>> eventsByPlayerByTime;


  private class Data {
  }

  private Data data;

  public Simulator(Player player, List<Player> players) {
    this.players = players;
    this.ballHolder = player;

    this.eventsByPlayerByTime = new TreeMap<>();
    Event event = new Event(Event.Type.SEND, "NEW_GROUP", 0, player);
    addEvent(0, player, event);
  }

  protected void clearMap() {
    for (Map<Player, List<Event>> eventsByPlayer : eventsByPlayerByTime.values()) {
      for (List<Event> events : eventsByPlayer.values()) {
        for (Event event : events) {
          if (event.getType() == Event.Type.RECEIVE) {
            ++msg;
          }
        }
      }
    }
    for (Map<Player, List<Event>> eventsByPlayer : eventsByPlayerByTime.values()) {
      eventsByPlayer.clear();
    }
    eventsByPlayerByTime.clear();
  }

  protected List<Player> getNearbyPlayers(Player player) {
    List<Player> nearbyPlayers = new ArrayList<>();
    for (Player otherPlayer : players) {
        if (player.dist(otherPlayer) <= Config.VALID_DIST && otherPlayer.isValid()) {
          nearbyPlayers.add(otherPlayer);
        }
    }
    return nearbyPlayers;
  }

  protected void addEvent(int time, Player player, Event event) {
    eventsByPlayerByTime.putIfAbsent(time, new HashMap<>());
    eventsByPlayerByTime.get(time).putIfAbsent(player, new ArrayList<>());
    eventsByPlayerByTime.get(time).get(player).add(event);
    if (event.getType() == Event.Type.RECEIVE) {
      //++msg;
    }
  }

  protected boolean isEventsContainReceiveNewGroup(List<Event> events) {
    for (Event event : events) {
      if (event.getCommand().equals("NEW_GROUP") && event.getType() == Event.Type.RECEIVE) {
        return true;
      }
    }
    return false;
  }

  protected boolean isEnd(Player newBallHolder) {
    double newDist = newBallHolder.getPoint().dist(Config.GOAL);
    boolean isEnd = true;
    for (Player otherPlayer : players) {
      if (otherPlayer != newBallHolder) {
        double dist = otherPlayer.getPoint().dist(Config.GOAL);
        if (dist < newDist) {
          isEnd = false;
        }
      }
    }
    return isEnd;
  }

  protected Player next(Player player) {
    TreeSet<Player> players = player.getGroup().getPlayers();
    Player nextPlayer = players.higher(player);
    if (nextPlayer == null) {
      nextPlayer = players.first();
    }
    return nextPlayer;
  }

  protected void swap() {
    Player tmp = this.players.get(0);
    int index = this.players.indexOf(this.ballHolder);
    this.players.set(0, this.ballHolder);
    this.players.set(index, tmp);
  }

  public abstract void execute();

}
