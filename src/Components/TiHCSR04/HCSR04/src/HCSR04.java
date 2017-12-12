import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.sensor.distance.TiHCSR04;
import tijos.util.Delay;

/**
 * 此类实现TiHCSR04超声波测距传感器测量距离的功能演示<br>
 * TiHCSR04测量距离分为三步：<br>
 * 1.“资源分配”：使用tijos.framework.devicecenter.TiGPIO包中TiGPIO类的<code>open</code>方法分配GPIO对象（注：全局只能分配一次）。<br>
 * 2.“资源绑定”：新创建TiHCSR04对象，将其与1.中分配的GPIO对象以及指定pin绑定。<br>
 * 3.“资源使用”：使用tijos.framework.sensor.humiture.TiHCSR04类中的<code>setSpeed<code>方法（默认为340m/s）设置声波速度，<br>
 * 然后再循环调用该类中的<code>measure<code>方法进行测量，并调用方法<code>getDistance<code>获取测量到的距离值<br>
 * <p>
 * 
 * @author Jason
 *
 */
public class HCSR04 {
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
			 * 定义使用的TiGPIO portPin
			 */
			int gpioPin0 = 0;
			int gpioPin1 = 1;
			/*
			 * 资源分配， 将gpioPort0与gpioPin0/1分配给TiGPIO的对象gpio0
			 */
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin0, gpioPin1);
			/*
			 * 资源绑定， 创建TiHCSR04的对象hcsr04并将gpio0和gpioPin0和gpioPin1与其绑定 trigPinID
			 * <----> gpioPin0 echoPinID <----> gpioPin1
			 */
			TiHCSR04 hcsr04 = new TiHCSR04(gpio0, gpioPin0, gpioPin1);
			/*
			 * 资源使用， 设置声波的速度，默认340m/s 启动测量并获取温度、湿度数据
			 */
			hcsr04.setSpeed(340);

			while (true) {
				try {
					hcsr04.measure();
					Double distance = hcsr04.getDistance();
					if (distance.isNaN())
						System.out.println("Distance invalid.");
					else
						System.out.println("Distance = " + distance + " m");
				} catch (IOException ie) {
					ie.printStackTrace();
				}

				Delay.msDelay(1000);
			}
		} catch (IOException ie) {

			ie.printStackTrace();
		}
	}
}
