//Class to represent a swimmer swimming a race
//Swimmers have one of four possible swim strokes: backstroke, breaststroke, butterfly and freestyle
package medleySimulation;

// import java.util.concurrent.CountDownLatch;
import java.awt.Color;

import java.util.Random;

import java.util.concurrent.atomic.AtomicInteger;

public class Swimmer extends Thread {

	public static StadiumGrid stadium; // shared
	private FinishCounter finish; // shared

	GridBlock currentBlock;
	private Random rand;
	private int movingSpeed;

	private PeopleLocation myLocation;
	private int ID; // thread ID
	private int team; // team ID
	private GridBlock start;

	private AtomicInteger order;

	public enum SwimStroke {
		Backstroke(1, 2.5, Color.black),
		Breaststroke(2, 2.1, new Color(255, 102, 0)),
		Butterfly(3, 2.55, Color.magenta),
		Freestyle(4, 2.8, Color.red);

		private final double strokeTime;
		private final int order; // in minutes
		private final Color colour;

		SwimStroke(int order, double sT, Color c) {
			this.strokeTime = sT;
			this.order = order;
			this.colour = c;
		}

		public int getOrder() {
			return order;
		}

		public Color getColour() {
			return colour;
		}
	}

	private final SwimStroke swimStroke;

	// Constructor
	Swimmer(int ID, int t, PeopleLocation loc, FinishCounter f, int speed, SwimStroke s, AtomicInteger ord) {
		this.swimStroke = s;
		this.ID = ID;
		movingSpeed = speed; // range of speeds for swimmers
		this.myLocation = loc;
		this.team = t;
		start = stadium.returnStartingBlock(team);
		finish = f;
		rand = new Random();

		// oLatch = l;
		order = ord;
	}

	// getter
	public int getX() {
		return currentBlock.getX();
	}

	// getter
	public int getY() {
		return currentBlock.getY();
	}

	// getter
	public int getSpeed() {
		return movingSpeed;
	}

	public SwimStroke getSwimStroke() {
		return swimStroke;
	}

	// !!!You do not need to change the method below!!!
	// swimmer enters stadium area
	public void enterStadium() throws InterruptedException {
		currentBlock = stadium.enterStadium(myLocation); //
		sleep(200); // wait a bit at door, look around
	}

	// !!!You do not need to change the method below!!!
	// go to the starting blocks
	// printlns are left here for help in debugging
	public void goToStartingBlocks() throws InterruptedException {
		int x_st = start.getX();
		int y_st = start.getY();
		// System.out.println("Thread "+this.ID + " has start position: " + x_st + " "
		// +y_st );
		// System.out.println("Thread "+this.ID + " at " + currentBlock.getX() + " "
		// +currentBlock.getY() );
		while (currentBlock != start) {
			// System.out.println("Thread "+this.ID + " has starting position: " + x_st + "
			// " +y_st );
			// System.out.println("Thread "+this.ID + " at position: " + currentBlock.getX()
			// + " " +currentBlock.getY() );
			sleep(movingSpeed * 3); // not rushing
			currentBlock = stadium.moveTowards(currentBlock, x_st, y_st, myLocation); // head toward starting block
			// System.out.println("Thread "+this.ID + " moved toward start to position: " +
			// currentBlock.getX() + " " +currentBlock.getY() );
		}
		System.out.println(
				"-----------Thread " + this.ID + " at start " + currentBlock.getX() + " " + currentBlock.getY());
	}

	// !!!You do not need to change the method below!!!
	// dive in to the pool
	private void dive() throws InterruptedException {
		int x = currentBlock.getX();
		int y = currentBlock.getY();
		currentBlock = stadium.jumpTo(currentBlock, x, y - 2, myLocation);
	}

	// !!!You do not need to change the method below!!!
	// swim there and back
	private void swimRace() throws InterruptedException {
		int x = currentBlock.getX();
		while ((boolean) ((currentBlock.getY()) != 0)) {
			currentBlock = stadium.moveTowards(currentBlock, x, 0, myLocation);
			// System.out.println("Thread "+this.ID + " swimming " + currentBlock.getX() + "
			// " +currentBlock.getY() );
			sleep((int) (movingSpeed * swimStroke.strokeTime)); // swim
			System.out.println("Thread " + this.ID + " swimming  at speed" + movingSpeed);
		}

		while ((boolean) ((currentBlock.getY()) != (StadiumGrid.start_y - 1))) {
			currentBlock = stadium.moveTowards(currentBlock, x, StadiumGrid.start_y, myLocation);
			// System.out.println("Thread "+this.ID + " swimming " + currentBlock.getX() + "
			// " +currentBlock.getY() );
			sleep((int) (movingSpeed * swimStroke.strokeTime)); // swim
		}

	}

	// !!!You do not need to change the method below!!!
	// after finished the race
	public void exitPool() throws InterruptedException {
		int bench = stadium.getMaxY() - swimStroke.getOrder(); // they line up
		int lane = currentBlock.getX() + 1;// slightly offset
		currentBlock = stadium.moveTowards(currentBlock, lane, currentBlock.getY(), myLocation);
		while (currentBlock.getY() != bench) {
			currentBlock = stadium.moveTowards(currentBlock, lane, bench, myLocation);
			sleep(movingSpeed * 3); // not rushing
		}
	}

	public void run() {
		try {

			// Swimmer arrives
			sleep(movingSpeed + (rand.nextInt(10))); // arriving takes a while
			myLocation.setArrived();

			synchronized (swimStroke) {
				// Keep checking if it's the current swimmer's turn
				while (true) {
					if (this.getSwimStroke() == SwimStroke.Backstroke && order.get() == 0) {
						enterStadium();
						order.set(1); // allowing Breaststroke to enter the stadium next
						break; // exit the loop once the swimmer enters the stadium
					} else if (this.getSwimStroke() == SwimStroke.Breaststroke && order.get() == 1) {
						enterStadium();
						order.set(2); // allowing Butterfly to enter the stadium next
						break; // exit the loop
					} else if (this.getSwimStroke() == SwimStroke.Butterfly && order.get() == 2) {
						enterStadium();
						order.set(3); // allowing Freestyle to enter the stadium next
						break; // exit the loop
					} else if (this.getSwimStroke() == SwimStroke.Freestyle && order.get() == 3) {
						enterStadium();
						break; // exit the loop
					}

					// If it's not their turn, wait a bit and check again
					swimStroke.wait(50); // small delay to avoid busy-waiting (could do notify and wait but not worth
											// it)
				}
			}

			// enterStadium();

			goToStartingBlocks();

			synchronized (stadium) {
				// Wait for the previous swimmer to finish before starting
				while (stadium.getStatus(team)) {
					stadium.wait(); // The swimmer waits here until notified
				}

				// Once allowed to swim, set the status to true
				stadium.setStatus(team, true);
			}

			stadium.startLatch.countDown(); // each swimmer decrements the count
			stadium.startLatch.await(); // swimmer waits for the latch to reach zero

			dive();

			swimRace();

			synchronized (stadium) {
				// After finishing, update the status and notify all waiting swimmers
				stadium.setStatus(team, false);
				stadium.notifyAll();
			}

			if (swimStroke.order == 4) {
				finish.finishRace(ID, team); // fnishline
			} else {
				// System.out.println("Thread "+this.ID + " done " + currentBlock.getX() + " "
				// +currentBlock.getY() );
				exitPool();// if not last swimmer leave pool
			}

		} catch (InterruptedException e1) { // do nothing
		}
	}
}
