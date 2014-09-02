/**
 * Sean Normoyle
 * CS-411
 * Assignment 5
 * 5/7/2012
 * 
 *  DiningPhilosophers.java
 *
 *  This program demonstrates the second solution of the Dining
 *  Philosophers problem as shown in Philosopher2.java and
 *  Chopstick2.java where we use Java's synchronization mechanism
 *  on ReentrantLock Lock and conditional wait.
 *
 *  Note that this solution will not cause deadlock.  However
 *  starvation is still a problem.
 *
 */
package net.seannormoyle.diningphilosophers;

import java.awt.*;
import java.awt.geom.*;
import java.util.LinkedList;
import javax.swing.*;

public class DiningPhilosophers extends JFrame
{	
	private static Chopstick chopSticks;
	
	private final Image[] imgPhilosopher = { 
			Toolkit.getDefaultToolkit().getImage("philosopher0.gif"), 
			Toolkit.getDefaultToolkit().getImage("philosopher1.gif"),
			Toolkit.getDefaultToolkit().getImage("philosopher2.gif"),
			Toolkit.getDefaultToolkit().getImage("philosopher3.gif"),
			Toolkit.getDefaultToolkit().getImage("philosopher4.gif") 
		};
	
	// Coordinates for philosopher images
	private final int WINDOW_SIZE = 480;
	private final int IMAGE_SIZE = 100;
	private final int[] pX = { WINDOW_SIZE/2 - IMAGE_SIZE/2, 
							   WINDOW_SIZE/6 - IMAGE_SIZE/2, 
							   WINDOW_SIZE/3 - IMAGE_SIZE/2, 
							   2*(WINDOW_SIZE/3) - IMAGE_SIZE/2,
							   WINDOW_SIZE - WINDOW_SIZE/6 - IMAGE_SIZE/2 };
	
	private final int[] pY = { WINDOW_SIZE/15, 
							   WINDOW_SIZE/2 - 80, 
							   WINDOW_SIZE - WINDOW_SIZE/3,
							   WINDOW_SIZE - WINDOW_SIZE/3, 
							   WINDOW_SIZE/2 - 80 };
	
	public static LinkedList<State[]> event = new LinkedList<State[]>();
	private static boolean finishedRunning = false;
	private int visibleStateIndex = 0;
	
	/**
	 * main
	 */
	public DiningPhilosophers()
	{
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Dining Philosophers");
 		add(new MainPanel());
 		pack();
 		setVisible(true);	 
	}
	
	public static void main(String args[])
    {
		new DiningPhilosophers();
		
		int i;
		Philosopher[] philosophers = new Philosopher[5];
		Thread[] philosopherThreads = new Thread[5];
		State[] states = new State[5];
		chopSticks = new Chopstick(states);
		
		for (i = 0; i < 5; ++i)
		{
			states[i] = State.THINKING;
		}

        for (i = 0; i < 5; ++i)
        {
        	philosophers[i] = new Philosopher(i, chopSticks, event);
        }
     
        for (i = 0; i < 5; ++i)
        {
        	philosopherThreads[i] = new Thread(philosophers[i]);
        }

        for (i = 0; i < 5; ++i)
        {
        	philosopherThreads[i].start();
        }	
         
        try
        {
	       	for (i = 0; i < 5; ++i)
	       	{
	       		philosopherThreads[i].join();
	       	}	
        }
        catch(InterruptedException e)
        { }

        System.out.println("\nAll philosophers are done eating and thinking.");
        finishedRunning = true;
    }
	
	
	private class MainPanel extends JPanel
	{
		public MainPanel()
		{
			setPreferredSize(new Dimension(WINDOW_SIZE, WINDOW_SIZE));
		}
		
		public void paintComponent(Graphics g) 
		{
			super.paintComponent(g);
			State[] visibleState = new State[5];
		    Graphics2D p = (Graphics2D)g;
		    String[] stateStr = new String[5];
		    
		    // Draw the background
		    p.setColor(Color.WHITE);
		    p.fill(new Rectangle2D.Double(0, 0, WINDOW_SIZE-1, WINDOW_SIZE-1));
		    p.setColor(Color.BLACK);
		    p.draw(new Rectangle2D.Double(0, 0, WINDOW_SIZE-1, WINDOW_SIZE-1));
		    
		    // Draw images and labels
	    	for (int i = 0; i < imgPhilosopher.length; i++)
		    {
		    	p.drawImage(imgPhilosopher[i], pX[i], pY[i], this);
		    	p.setColor(Color.BLACK);
	    		p.fill(new Rectangle2D.Double(pX[i], pY[i] + IMAGE_SIZE, IMAGE_SIZE+10, 17));
	    		
	    		if (!finishedRunning)
	    		{
	    			visibleState = chopSticks.getStates();
	    		}
	    		else
	    		{
	    			visibleState = event.get(visibleStateIndex);
	    		}		
	    		
	    		switch (visibleState[i])
	    		{
	    			case HUNGRY:
	    			{
	    				p.setColor(Color.RED);
	    				stateStr[i] = "Waiting...";
	    				if (chopSticks.getPlatoIndex() == i)
	    				{
	    					stateStr[i] += " (PLATO)";
	    				}
	    				
	    				break;
	    			}
	    			case EATING:
	    			{
	    				p.setColor(Color.GREEN);
	    				stateStr[i] = "Eating";
	    				if (chopSticks.getPlatoIndex() == i)
	    				{
	    					stateStr[i] += " (PLATO)";
	    				}
	    					
	    				break;
	    			}
	    			default:
	    			{
	    				p.setColor(Color.CYAN);
	    				stateStr[i] = "Thinking";
	    				if (chopSticks.getPlatoIndex() == i)
	    				{
	    					stateStr[i] += " (PLATO)";
	    				}
	    					
	    				break;
	    			}
	    		}
	    		
	    		if (finishedRunning)
	    		{
	    			for (int j = 0; j < 5; j++)
	    			{
	    				stateStr[i] = "Finished";
	    			}	
	    		}
	    			
	    		
		    	p.drawString(stateStr[i], pX[i] + 3, pY[i] + IMAGE_SIZE + 12);
		    }
		    
	    	// Continuously repaint
	    	if (!finishedRunning)
	    	{
	    		repaint();
	    	}
		}
	}
}