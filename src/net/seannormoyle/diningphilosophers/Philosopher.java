/**
 * Sean Normoyle
 * CS-411
 * Assignment 5
 * 5/7/2012
 * 
 *  Philosopher.java
 *
 *  This class implements the second solution for the Dining
 *  Philosophers problem, for which each philosopher is allowed to
 *  pick up his/her chopsticks only if both of them are available.
 *
 *  Note that this solution will not cause deadlock.  However
 *  starvation is still a problem.
 */
package net.seannormoyle.diningphilosophers;

import java.util.LinkedList;


public class Philosopher implements Runnable
{
	private int         id;  // which philosopher
	private Chopstick  chopSticks;
	private LinkedList<State[]> event;
	private static final int MAX_EATING_ORTHINKING_TIME = 5000;

	public Philosopher(int n, Chopstick c, LinkedList<State[]> event)
	{
		id = n;
		chopSticks = c;
		this.event = event;
	}

	private void eating()
	{
		int eatingTime = (int) (MAX_EATING_ORTHINKING_TIME * Math.random() );
		System.out.print("philosopher " + id + " is eating ... \n");

		try
		{ 
			Thread.sleep(eatingTime); 
		}
		catch (InterruptedException e)
		{ }
	}

   private void thinking()
   {
	   int thinkingTime = (int) (MAX_EATING_ORTHINKING_TIME * Math.random());

	   System.out.print("philosopher " + id + " is thinking ... \n");
	   addEvent();

	   try
	   { 
		   Thread.sleep(thinkingTime); 
	   }
	   catch (InterruptedException e)
	   { }
   	}
   
	public void run()
	{
		for (int i = 0; i < 5; i++)
		{
			System.out.print("philosopher " + id + " wants to pick up both chopsticks\n");
			addEvent();
			chopSticks.pickup(id); // pick up both chopsticks
			System.out.print("philosopher " + id +  " picked up both chopsticks \n");
			addEvent();
		
			eating();
		
			System.out.print("philosopher " + id + " puts down both chopsticks\n");
			addEvent();
			chopSticks.putdown(id);    // put down both chopsticks
		
			thinking();
		}

		System.out.print("philosopher " + id + " done eating and thinking.\n");
	}
	
	public void addEvent()
	{
		State[] currentStates = new State[5];
		System.arraycopy(chopSticks.getStates(), 0, currentStates, 0, 5);
		event.add(currentStates);
	}
}