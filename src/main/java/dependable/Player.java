package dependable;


import lombok.Getter;
import lombok.Setter;

import java.util.*;

@Getter
@Setter
public class Player {

  static int num = 0;
  private int id;
  private int L = -Integer.MAX_VALUE;
  private boolean joined = false;
  private boolean isValid = false;
  private Group group;
  // save the position of a player
  private Point point;
  private Map<Integer, Set<Player>> membersByV = new HashMap<>();
  static Random random = new Random(12346214);

  private double nextDouble(double startInclusive, double endInclusive) {
    return startInclusive == endInclusive ? startInclusive : startInclusive + (endInclusive - startInclusive) * random.nextDouble();
  }

  public Player(double x, double y) {
    point = new Point(x, y);
    id = num++;
    group = new Group(this);
  }

  public String toString() {
    return "player" + id;
  }

  public Player passBall() {
    Player resultPlayer = this;
    double minDist= resultPlayer.point.dist(Config.GOAL);
    for (Player player : group.players) {
      double dist = player.point.dist(Config.GOAL);
      if (minDist > dist) {
        minDist = dist;
        resultPlayer = player;
      }
    }
    return resultPlayer;
  }

  public double dist(Player otherPlayer){
    return point.dist(otherPlayer.point);
  }

  public void move() {
    this.point.setX(this.point.getX() + nextDouble(-Config.STEP_SIZE, Config.STEP_SIZE));
    this.point.setY(this.point.getY() + nextDouble(-Config.STEP_SIZE, Config.STEP_SIZE));
    if (this.point.getX() > Config.WIDTH) this.point.setX(Config.WIDTH);
    if (this.point.getX() < 0) this.point.setX(0);
    if (this.point.getY() > Config.LENGTH) this.point.setY(Config.LENGTH);
    if (this.point.getY() < 0) this.point.setY(0);
  }

  public void stand() {}

  public void updateStatus(Player ballHolder){
    isValid = ballHolder.dist(this) <= Config.VALID_DIST;
  }

}
