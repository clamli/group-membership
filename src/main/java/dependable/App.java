package dependable;

import sun.jvm.hotspot.oops.NamedFieldIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App {
  public static void main(String[] args) {

    int[] changes = new int[]{30,40,50,60,70,80,90,100};
    for (int change : changes) {
      // change the config for the experiment
      // you can change other configs if you want
      Config.VALID_DIST = change;
      Player.num = 0;
      // make the experiment reproducible
      Player.random = new Random(12346214);
      // create players
      Player player0 = new Player(30, 3);
      Player player1 = new Player(10, 25);
      Player player2 = new Player(25, 25);
      Player player3 = new Player(35, 30);
      Player player4 = new Player(50, 40);
      Player player5 = new Player(30, 50);
      Player player6 = new Player(15, 60);
      Player player7 = new Player(40, 55);
      Player player8 = new Player(30, 75);
      Player player9 = new Player(47, 72);
      Player player10 = new Player(10, 64);
      List<Player> players = new ArrayList<>();
      players.add(player0);
      players.add(player1);
      players.add(player2);
      players.add(player3);
      players.add(player4);
      players.add(player5);
      players.add(player6);
      players.add(player7);
      players.add(player8);
      players.add(player9);
      players.add(player10);

      // add ballholder (player0) and all player lists to the simulator
      Simulator simulator = new PeriodicBroadcastSimulator(player0, players);

      // you may also choose other simulator:
      // Simulator simulator = new AttendanceListSimulator(player0, players);
      // Simulator simulator = new NeighborSurveillanceSimulator(player0, players);

      // start simulating!
      simulator.execute();
    }
  }
}
