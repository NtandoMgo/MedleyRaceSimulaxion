//Class to represent a swim team - which has four swimmers
package medleySimulation;

import java.util.concurrent.CountDownLatch;

import medleySimulation.Swimmer.SwimStroke;

public class SwimTeam extends Thread {
	
	public static StadiumGrid stadium; //shared 
	private Swimmer [] swimmers;
	private int teamNo; //team number 

	
	public static final int sizeOfTeam=4;

	private CountDownLatch latch;
	
	SwimTeam( int ID, FinishCounter finish,PeopleLocation [] locArr, CountDownLatch lt ) {
		this.teamNo=ID;
		
		swimmers= new Swimmer[sizeOfTeam];
	    SwimStroke[] strokes = SwimStroke.values();  // Get all enum constants
		stadium.returnStartingBlock(ID);

		latch = lt;

		for(int i=teamNo*sizeOfTeam,s=0;i<((teamNo+1)*sizeOfTeam); i++,s++) { //initialise swimmers in team
			locArr[i]= new PeopleLocation(i,strokes[s].getColour());
	      	int speed=(int)(Math.random() * (3)+30); //range of speeds 
			swimmers[s] = new Swimmer(i,teamNo,locArr[i],finish,speed,strokes[s]); //hardcoded speed for now
		}
	}
	
	
	public void run() {
		try {	

			latch.await();	// thread wait for latch to be zero before entering the stadium

			for(int s=0;s<sizeOfTeam; s++) { //start swimmer threads
				swimmers[s].start();
				
			}
			
			for(int s=0;s<sizeOfTeam; s++) swimmers[s].join();			//don't really need to do this;
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
	

