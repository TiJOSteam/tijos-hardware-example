import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.transducer.relay.TiRelay1CH;
import tijos.framework.util.Delay;

/**
 * 此类实现TiRelay1CH继电器控制功能演示<br>
 * TiRelay1CH控制分为三步：<br>
 * 1.“资源分配”：使用tijos.framework.devicecenter.TiGPIO包中TiGPIO类的<code>open</code>方法分配GPIO对象。（注：全局只能分配一次）。<br>
 * 2.“资源绑定”：新创建TiRelay1CH对象，将其与1.中分配的GPIO对象以及指定pin绑定,同时选择继电器的驱动方式。<br>
 * 3.“资源使用”：使用tijos.framework.transducer.transducer.relay.TiRelay1CH类中的<code>turnOn<code>以及<code>turnOff<code>控制继电器的通断。<br>
 * <p>
 * 
 * @author Jason
 *
 */
public class Relay1CH {
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
			/*
			 * 资源分配， 将gpioPort与gpioPin0分配给TiGPIO对象gpio0
			 */
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin0);
			/*
			 * 资源绑定， 创建TiRelay1CH对象relay并将gpioPort和gpioPortPin与其绑定
			 */
			TiRelay1CH relay = new TiRelay1CH(gpio0, gpioPin0);
			/*
			 * 资源使用， 控制继电器的开和关
			 */
			while (true) {
				relay.turnOn();
				System.out.println("relay is turned on");
				
				Delay.msDelay(500);
				
				relay.turnOff();
				System.out.println("relay is turned off");
				
				Delay.msDelay(500);
			}
		} catch (IOException ie) {

			ie.printStackTrace();
		}
	}
}
