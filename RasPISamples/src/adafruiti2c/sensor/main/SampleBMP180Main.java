package adafruiti2c.sensor.main;

import adafruiti2c.sensor.listener.AdafruitBMP180Listener;
import adafruiti2c.sensor.nmea.AdafruitBMP180Reader;
import adafruiti2c.sensor.listener.SensorNMEAContext;

import ocss.nmea.api.NMEAEvent;

/*
 * Uses its own listeners, defined in this project.
 * @see AdafruitBMP180Listener
 * @see SensorNMEAContext
 */
public class SampleBMP180Main
{
  private final AdafruitBMP180Reader sensorReader = new AdafruitBMP180Reader();
  
  public SampleBMP180Main()
  {
    SensorNMEAContext.getInstance().addReaderListener(new AdafruitBMP180Listener()
      {
        public void dataDetected(NMEAEvent e) 
        {
          System.out.println(e.getContent());
        }
      });
  }
  
  public void start()
  {
    System.out.println("Starting reader.");
    sensorReader.startReading();
  }
  
  public void stop()
  {
    sensorReader.stopReading();
    synchronized (Thread.currentThread())
    {
      System.out.println("... notifying main.");
      Thread.currentThread().notify();
    }
  }

  public static void main(String[] args)
  {
    final SampleBMP180Main reader = new SampleBMP180Main();
    Thread worker = new Thread("Reader")
      {
        public void run()
        {
          reader.start();
        }
      };
    Runtime.getRuntime().addShutdownHook(new Thread("Hook")
      {
        public void run()
        {
          System.out.println();            
          reader.stop();
          // Wait for everything to shutdown, for the example...
          try { Thread.sleep(2000L); } catch (InterruptedException ie) {}
        }
      });
    
    worker.start();
    synchronized (Thread.currentThread())
    {
      try 
      { 
        Thread.currentThread().wait(); 
      }
      catch (InterruptedException ie)
      {
        ie.printStackTrace();
      }
    }
  }
}
