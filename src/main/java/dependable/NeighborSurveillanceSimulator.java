package dependable;

import java.util.*;

public class NeighborSurveillanceSimulator extends Simulator {
  public NeighborSurveillanceSimulator(Player player, List<Player> players) {
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
        // set GAMMA
        int GAMMA = Config.SMALL_DELTA;
        eventsByPlayer.putIfAbsent(player, new ArrayList<>());
        List<Event> events = eventsByPlayer.get(player);
        // remove check and confirmation if receive new group
        Map<Integer, Set<Player>> membersByV = player.getMembersByV();
        int maxReceivedPresentV = -1;
        if (isEventsContainReceiveNewGroup(events)) {
          events.removeIf(x -> x.getCommand().equals("CHECK"));
          events.removeIf(x -> x.getCommand().equals("CONFIRMATION"));
        }
        Collections.sort(events, (o1, o2) -> {
          Map<String, Integer> priority = new HashMap<>();
          priority.put(Event.Type.RECEIVE.toString(), 0);
          priority.put(Event.Type.SEND.toString(), 1);
          return priority.get(o1.getType().toString()) - priority.get(o2.getType().toString());
        });
        for (Event event : events) {
          if (event.getType() == Event.Type.SEND) { // event type is send
            if (event.getCommand().equals("NEW_GROUP")) {
              Event receiveEvent = new Event(Event.Type.RECEIVE, "NEW_GROUP", t + Config.BIG_DELTA);
              for (Player otherPlayer : getNearbyPlayers(this.ballHolder)) {
                addEvent(t + Config.BIG_DELTA, otherPlayer, receiveEvent);
              }
            } else if (event.getCommand().equals("CHECK") && t <= event.getTime()) {
              // If current player is the maximum player, send list to next player
              if (player.getGroup().size() > 0) {
                Event receiveListEvent = new Event(Event.Type.RECEIVE, "NEIGHBOR", event.getTime());
                addEvent(t + GAMMA, next(player), receiveListEvent);
              }
              // Schedule confirmation
              // TODO: remove player?
              Event confirmationEvent = new Event(Event.Type.SEND, "CONFIRMATION", event.getTime() + GAMMA, player);
              addEvent(event.getTime() + GAMMA, player, confirmationEvent);
              // Schedule check
              Event checkEvent = new Event(Event.Type.SEND, "CHECK", event.getTime() + Config.PI, player);
              addEvent(event.getTime() + Config.PI, player, checkEvent);
            } else if (event.getCommand().equals("CONFIRMATION")) {
              if (t <= event.getTime()) {
                //Pass the ball if current group is valid
                if (player.getL() + GAMMA >= event.getTime()) {
                  //player.getGroup().print();
                  if (player == this.ballHolder) {
                    newBallHolder = player.passBall();
                    this.setPassballTimes(this.getPassballTimes()+1);
                    if (isEnd(newBallHolder)) {
                      clearMap();
                      System.out.print("Messages: " + msg);
                      System.out.print(" Error rate: " + (this.getPassballTimes()*1.0)/this.getFormNewGroupTimes());
                      System.out.println(" Time: " + t);
                      return;
                    }
                    clearMap();
                    Event sendNewGroupEvent = new Event(Event.Type.SEND, "NEW_GROUP", 0, player);
                    addEvent(t + 1, newBallHolder, sendNewGroupEvent);
                    this.setFormNewGroupTimes(this.getFormNewGroupTimes()+1);
                    break;
                  }
                }
                Event sendNewGroupEvent = new Event(Event.Type.SEND, "NEW_GROUP", 0, player);
                addEvent(t + 1, newBallHolder, sendNewGroupEvent);
                this.setFormNewGroupTimes(this.getFormNewGroupTimes()+1);
              }
            }
          } else if (event.getType() == Event.Type.RECEIVE) { // event type is receive
            if (event.getCommand().equals("NEW_GROUP") && t <= event.getTime()) {
              // Cancel broadcast check and confirmation
              SortedMap<Integer, Map<Player, List<Event>>> subMap = eventsByPlayerByTime.tailMap(t, false);
              for (Map<Player, List<Event>> playerEventPair : subMap.values()) {
                List<Event> eventList = playerEventPair.get(player);
                if (eventList != null) {
                  eventList.removeIf(x -> x.getCommand().equals("CHECK"));
                  eventList.removeIf(x -> x.getCommand().equals("CONFIRMATION"));
                }
              }
              // Broadcast present
              Event presentEvent = new Event(Event.Type.RECEIVE, "PRESENT", event.getTime(), player);
              for (Player otherPlayer : getNearbyPlayers(this.ballHolder)) {
                addEvent(t + Config.BIG_DELTA, otherPlayer, presentEvent);
              }
              // Schedule check
              // TODO: remove player?
              Event checkEvent = new Event(Event.Type.SEND, "CHECK", event.getTime() + Config.PI, player);
              addEvent(event.getTime() + Config.PI, player, checkEvent);
            } else if (event.getCommand().equals("PRESENT")) {
              membersByV.putIfAbsent(event.getTime(), new HashSet<>());
              membersByV.get(event.getTime()).add(event.getPlayer());
              maxReceivedPresentV = Math.max(maxReceivedPresentV, event.getTime());
            } else if (event.getCommand().equals("NEIGHBOR")) {
              if (t <= event.getTime() + GAMMA) {
                player.setL(event.getTime());
              }
            }
          }
        }
        if (maxReceivedPresentV != -1 && !player.isJoined() && membersByV.get(maxReceivedPresentV).contains(player)) {
          player.setJoined(true);
        }
        if (maxReceivedPresentV != -1 && !player.getGroup().getPlayers().equals(membersByV.get(maxReceivedPresentV))) {
          player.setGroup(new Group(membersByV.get(maxReceivedPresentV)));
        }
      }

      this.ballHolder = newBallHolder;
      t += 1;
    }
  }
}