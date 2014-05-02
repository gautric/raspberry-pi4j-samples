package adafruiti2c.sensor.main;

import adafruiti2c.sensor.AdafruitTCS34725;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

public class SampleTCS34725Main
{
  private static boolean go = true;
  
  public static void main(String[] args) throws Exception
  {
    final AdafruitTCS34725 sensor = new AdafruitTCS34725(AdafruitTCS34725.TCS34725_INTEGRATIONTIME_50MS, AdafruitTCS34725.TCS34725_GAIN_4X);
    // Setup output pins here for the 3 color led
    final GpioController gpio = GpioFactory.getInstance();

    final GpioPinDigitalOutput greenPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "green", PinState.LOW);
    final GpioPinDigitalOutput bluePin  = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, "blue",  PinState.LOW);
    final GpioPinDigitalOutput redPin   = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "red",   PinState.LOW);
    
    Runtime.getRuntime().addShutdownHook(new Thread()
                                         {
                                           public void run()
                                           {
                                             go = false;
                                             System.out.println("\nBye");
                                           }
                                         });  
    // Main loop
    while (go)
    {
      sensor.setInterrupt(false); // turn led on
      try { Thread.sleep(60); } catch (InterruptedException ie) {} // Takes 50ms to read, see above
      AdafruitTCS34725.TCSColor color = sensor.getRawData();
      sensor.setInterrupt(true); // turn led off
      int r = color.getR(),
          g = color.getG(),
          b = color.getB();
      // Display the color on the 3-color led accordingly
      System.out.println("Read color R:" + r + 
                                   " G:" + g +
                                   " B:" + b);
      // TODO Send to 3-color led. The output is digital!! Not analog.
      // Use a DAC: https://learn.adafruit.com/mcp4725-12-bit-dac-with-raspberry-pi/overview
      // For now, take the biggest one
      int max = Math.max(r, g);
      max = Math.max(max, b);
      if (max == r) redPin.high();   else redPin.low();
      if (max == g) greenPin.high(); else greenPin.low();
      if (max == b) bluePin.high();  else bluePin.low();
    }
    redPin.low();
    greenPin.low();
    bluePin.low();
    gpio.shutdown();
    System.out.println("Exiting. Thanks.");
  }
}