import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.transducer.led.TiLED;
import tijos.framework.util.Delay;


/**
 * 此类实现TiLED灯控制功能演示<br>
 * TiLED控制分为三步：<br>
 * 1.“资源分配”：使用tijos.framework.devicecenter.TiGPIO包中TiGPIO类的<code>open</code>方法分配GPIO对象（注：全局只能分配一次）。<br>
 * 2.“资源绑定”：新创建TiLED对象，将其与1.中分配的GPIO对象以及指定pin绑定。<br>
 * 3.“资源使用”：使用tijos.framework.transducer.transducer.led.TiLED类中的<code>turnOn<code>以及<code>turnOff<code>控制各个灯的开关<br>
 * <p>
 * 
 * @author Jason
 *
 */
public class FourLED {
	/**
	 * 程序入口，由TiJOS调用
	 * 
	 * @param args
	 *            入口参数， TiJOS中一直等于null
	 */
	public static void main(String[] args) {
		try {
			/*
			 * 定义使用的TiGPIO port
			 */
			int gpioPort0 = 0;
			/*
			 * 定义使用的TiGPIO Pin
			 */
			int gpioPin0 = 0;
			int gpioPin1 = 1;
			int gpioPin3 = 3;
			int gpioPin4 = 4;
			/*
			 * 资源分配， 将gpioPort与gpioPin0/1/3/4分配给TiGPIO对象gpio0
			 */
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin0, gpioPin1, gpioPin3, gpioPin4);
			/*
			 * 资源绑定， 创建TiLED对象red/yellow/blue/green并将gpio?和gpioPin?与其绑定
			 */
			TiLED red = new TiLED(gpio0, gpioPin0);
			TiLED yellow = new TiLED(gpio0, gpioPin1);
			TiLED blue = new TiLED(gpio0, gpioPin3);
			TiLED green = new TiLED(gpio0, gpioPin4);
			/*
			 * 资源使用， 控制各个灯的亮与灭
			 */
			while (true) {
				red.turnOn();
				System.out.println("redled is turned on");
				Delay.msDelay(100);

				yellow.turnOn();
				System.out.println("yellowled is turned on");
				Delay.msDelay(100);

				blue.turnOn();
				System.out.println("blueled is turned on");
				Delay.msDelay(100);
				
				green.turnOn();
				System.out.println("greenled is turned on");
				Delay.msDelay(100);

				green.turnOff();
				System.out.println("greenled is turned off");
				Delay.msDelay(100);

				blue.turnOff();
				System.out.println("blueled is turned off");
				Delay.msDelay(100);

				yellow.turnOff();
				System.out.println("yellowled is turned off");
				Delay.msDelay(100);

				red.turnOff();
				System.out.println("redled is turned off");
				Delay.msDelay(100);
			}
			
		} catch (IOException ie) {
			
			ie.printStackTrace();
		}
	}
}
