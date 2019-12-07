package dependable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Point {

  private double x;
  private double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double dist(Point otherPoint) {
    double dx = x - otherPoint.x;
    double dy = y - otherPoint.y;
    return Math.sqrt(dx * dx + dy * dy);
  }


}
