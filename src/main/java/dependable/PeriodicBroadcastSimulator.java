package dependable;

import javax.sound.midi.SysexMessage;
import java.util.*;

public class PeriodicBroadcastSimulator extends Simulator {
  public PeriodicBroadcastSimulator(Player player, List<Player> players) {
    super(player, players);
  }

  @Override
  public void execute() {
    int t = 0;
    while (true) {

      Player newBallHolder = this.ballHolder;

      // Move
      for (Player player : this.players) {
        player.move();
        //player.stand();
      }
      for (Player player : this.players) {
        player.updateStatus(this.ballHolder);
      }
      eventsByPlayerByTime.putIfAbsent(t, new HashMap<>());
      Map<Player, List<Event>> eventsByPlayer = eventsByPlayerByTime.get(t);
      swap();
      for (Player player : this.players) {
        if (!player.isValid()) {
          continue;
        }
        eventsByPlayer.putIfAbsent(player, new ArrayList<>());
        List<Event> events = eventsByPlayer.get(player);
        // for receiving present
        Map<Integer, Set<Player>> membersByV = player.getMembersByV();
        int maxReceivedPresentV = -1;
        if (isEventsContainReceiveNewGroup(events)) {
          events.removeIf(x -> x.getCommand().equals("CHECK"));
        }
        Collections.sort(events, (o1, o2) -> {
          Map<String, Integer> priority = new HashMap<>();
          priority.put(Event.Type.RECEIVE.toString(), 0);
          priority.put(Event.Type.SEND.toString(), 1);
          return priority.get(o1.getType().toString()) - priority.get(o2.getType().toString());
        });
        for (Event event : events) {
          if (event.getType() == Event.Type.SEND) {
            if (event.getCommand().equals("NEW_GROUP")) {
              if (player == this.ballHolder) {
                this.setFormNewGroupTimes(this.getFormNewGroupTimes()-1);
              }
              Event receiveEvent = new Event(Event.Type.RECEIVE, "NEW_GROUP", t + Config.BIG_DELTA);
              for (Player otherPlayer : getNearbyPlayers(this.ballHolder)) {
                addEvent(t + Config.BIG_DELTA, otherPlayer, receiveEvent);
              }

            } else if (event.getCommand().equals("CHECK") && t <= event.getTime()) {
              Event presentEvent = new Event(Event.Type.RECEIVE, "PRESENT", event.getTime(), player);
              for (Player otherPlayer : getNearbyPlayers(this.ballHolder)) {
                addEvent(t + Config.BIG_DELTA, otherPlayer, presentEvent);
              }
              Event checkEvent = new Event(Event.Type.SEND, "CHECK", event.getTime() + Config.PI, player);
              addEvent(event.getTime() + Config.PI, player, checkEvent);
            }
          } else if (event.getType() == Event.Type.RECEIVE) {
            if (event.getCommand().equals("NEW_GROUP") && t <= event.getTime()) {
              // Cancel broadcast
              SortedMap<Integer, Map<Player, List<Event>>> subMap = eventsByPlayerByTime.tailMap(t, false);
              for (Map<Player, List<Event>> playerEventPair : subMap.values()) {
                List<Event> eventList = playerEventPair.get(player);
                if (eventList != null) {
                  eventList.removeIf(x -> x.getCommand().equals("CHECK"));
                }
              }
              Event presentEvent = new Event(Event.Type.RECEIVE, "PRESENT", event.getTime(), player);
              for (Player otherPlayer : getNearbyPlayers(this.ballHolder)) {
                addEvent(t + Config.BIG_DELTA, otherPlayer, presentEvent);
              }
              // Schedule check
              Event checkEvent = new Event(Event.Type.SEND, "CHECK", event.getTime() + Config.PI, player);
              addEvent(event.getTime() + Config.PI, player, checkEvent);
            } else if (event.getCommand().equals("PRESENT")) {
              membersByV.putIfAbsent(event.getTime(), new HashSet<>());
              membersByV.get(event.getTime()).add(event.getPlayer());
              maxReceivedPresentV = Math.max(maxReceivedPresentV, event.getTime());
            }

          }
        }
        if (maxReceivedPresentV != -1 && !player.isJoined() && membersByV.get(maxReceivedPresentV).contains(player)) {
          player.setJoined(true);
        }
        if (maxReceivedPresentV != -1 && !player.getGroup().getPlayers().equals(membersByV.get(maxReceivedPresentV))) {
          player.setGroup(new Group(membersByV.get(maxReceivedPresentV)));
          if (player == this.ballHolder) {
            this.setFormNewGroupTimes(this.getFormNewGroupTimes()+1);
          }
        } else if (player == this.ballHolder && maxReceivedPresentV != -1 && player.getGroup().getPlayers().equals(membersByV.get(maxReceivedPresentV))) {
          newBallHolder = player.passBall();
          this.setPassballTimes(this.getPassballTimes()+1);
          if (isEnd(newBallHolder)) {
            clearMap();
            System.out.print("Messages: " + msg);
            System.out.print(" Error rate: " + (this.getPassballTimes()*1.0)/(this.getFormNewGroupTimes()));
            System.out.println(" Time: " + t);
            return;
          }
//          SortedMap<Integer, Map<Player, List<Event>>> subMap = eventsByPlayerByTime.tailMap(t, true);
//          for (Map<Player, List<Event>> playerEventPair: subMap.values()) {
//            List<Event> eventList = playerEventPair.get(player);
//            if (eventList != null) {
//              eventList.removeIf(x -> x.getCommand().equals("CHECK"));
//            }
//          }
          clearMap();
          Event event = new Event(Event.Type.SEND, "NEW_GROUP", 0, player);
          addEvent(t + 1, newBallHolder, event);
          this.setFormNewGroupTimes(this.getFormNewGroupTimes()+1);
          break;
        }
      }

      this.ballHolder = newBallHolder;
      t += 1;
    }
  }
}
