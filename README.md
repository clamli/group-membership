# Group Membership Project

## How to run the project
You'll need java and maven installed.

To build the project, run
```
mvn compile
```

To run the sample experiment defined in App.java, run
```
mvn exec:java -D"exec.mainClass"="dependable.App"
```

For detailed description of how to use the sample experiment, check out the App.java file. 
## Config parameters
```$java
BIG_DELTA: latency to broadcast message
SMALL_DELTA: latency to send datagram
PI: check interval
VALID_DIST: players can catch the ball if they are within this distance range of ball holder.
LENGTH: y-axis of the court
WIDTH: x-axis of the court
STEP_SIZE: the step size players move in each time unit
GOAL: the position of goal
```



## How to use the simulator
There are three simulator classes defined in PeriodicBroadcastSimulator.java, NeighborSurveillanceSimulator.java and AttendanceListSimulator.java.
To run the simulator, you only need to create a instance of the simulator class and call execute function:
```
/* 
   add ballholder (player0) and all player lists to the simulator
   player0 is a Player instance representing the ball holder and 
   players is a java list of all players involved in the experiment 
*/
Simulator simulator = new PeriodicBroadcastSimulator(player0, players);
 
// start simulating
simulator.execute();
```  


## Reference
- [Reaching agreement on processor-group membership in synchronous distributed systems](http://www.cs.utexas.edu/~mok/cs386C/papers/cristian91.pdf)
- [Processor group membership protocols: Specification, design and implementation](https://ieeexplore.ieee.org/document/393478)
- [A Group Membership Algorithm with a Practical Specification](http://circuit.ucsd.edu/~massimo/Journal/IEEE-TPDS-GrpMshp-J.pdf)
