/**
 * Sean Normoyle
 * CS-411
 * Assignment 5
 * 5/7/2012
 * 
 *  Chopstick.java
 *
 *  In this version we use Java's synchronization mechanism on
 *  ReentrantLock Lock and conditional wait.  A conditional wait
 *  (called await()) is used so we can have each philosopher wait
 *  for different condition (whether or not this philosopher's
 *  left and right neighbors are not eating).  In order to use
 *  conditional wait, we must use ReentrantLock.  Note that we do
 *  not use "synchronized" keyword in pickup and putdown methods.
 *  The mutual exclusion will be provided by the lock of a
 *  ReentrantLock object.
 *
 *  Note also we will use one single Chopstick object to control
 *  all chopsticks so we can synchronize neighboring philosophers.
 *
 */
package net.seannormoyle.diningphilosophers;
import java.util.concurrent.locks.*;


public class Chopstick
{
   private State states[];
   private Condition conditions[];
   private Lock key;
   private int plato;
   
   public Chopstick(State s[])
   {
      states = s;
      conditions = new Condition[5];
      key = new ReentrantLock();
      
      for (int i = 0; i < 5; ++i)
      {
    	  conditions[i] = key.newCondition();
      }
      
      // Initially philosopher 0 is the Plato
      plato = 0;
   }

   public void pickup(int i)
   {
      key.lock();              // lock will provide mutual exclusion

      try
      {
         states[i] = State.HUNGRY;
         check(i);
         
         if (states[i] != State.EATING)
         {
        	 conditions[i].await();  // note here it is await, not wait
         }  
      }
      catch(InterruptedException e)
      { 
    	  
      }
      finally  
      {
         key.unlock();
      }
   }


   public void putdown(int i)  // not synchronized
   {
      int left, right;

      key.lock();              // lock will provide mutual exclusion

      left = (i + 4) % 5;
      right = (i + 1) % 5;

      states[i] = State.THINKING;
      
      if (states[plato] != State.HUNGRY)
      {
    	  System.out.println("philosopher " + i + " is now PLATO");
    	  plato = (plato + 1) % 5;
      }

      check(left);
      check(right);
      
      key.unlock();
   }


   // check the status of left and right philosopher neighbors
   private void check(int i)
   {
      int left  = (i + 4) % 5;
      int right = (i + 1) % 5;
      
      if ((states[i] == State.HUNGRY) && (states[left] != State.EATING) && (states[right] != State.EATING))
      {
    	  if (((plato == left) || (plato == right)) && (states[plato] == State.HUNGRY))
    	  {
    		  states[plato] = State.EATING;
    		  conditions[plato].signal();
    	  }
    	  else
    	  {
    		  states[i] = State.EATING; 
              conditions[i].signal(); 
    	  }
      }
   }
   
   public State[] getStates()
   {
	   return states;
   }
   
   public int getPlatoIndex()
   {
	   return plato;
   }
}
