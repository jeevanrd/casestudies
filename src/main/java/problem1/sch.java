package problem1;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by jeevan on 01/01/18.
 */
public class sch {
    public static void main(String args[]) throws InterruptedException {
        Timer time = new Timer(); // Instantiate Timer Object
        ScheduledTask st = new ScheduledTask(); // Instantiate SheduledTask class
        time.schedule(st, 0, 1000); // Create Repetitively task for every 1 secs
        ScheduledTask1 st1 = new ScheduledTask1(); // Instantiate SheduledTask class
        time.schedule(st1, 0, 2500); // Create Repetitively task for every 1 secs
    }
}

class ScheduledTask extends TimerTask {
    Date now;
    // to display current time
    // Add your task here
    public void run() {
        now = new Date(); // initialize date
        System.out.println("Time is :" + now); // Display current time
    }
}

class ScheduledTask1 extends TimerTask {
    // Add your task here
    public void run() {
        System.out.println("Time is : test");
    }
}