import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.sensor.dht.TiDHT;
import tijos.util.Delay;

/**
 * 此类实现DHT11数字温湿度传感器采集温度、湿度的功能演示<br>
 * TiDHT11采集温度数据分为三步：<br>
 * 1.“资源分配”：使用tijos.framework.devicecenter.TiGPIO包中TiGPIO类的<code>open</code>方法分配GPIO对象（注：全局只能分配一次）。<br>
 * 2.“资源绑定”：新创建TiDHT对象，将其与1.中分配的GPIO对象以及指定pin绑定。<br>
 * 3.“资源使用”：循环调用该类中的<code>measure<code>方法进行测量，并调用方法<code>getTemperature<code>和<code>getTemperature<code>获取测量到的温度值和湿度值<br>
 * <p>
 * 
 * @author Jason
 *
 */
public class DHT11 {
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
			/*
			 * 资源分配， 将gpioPort0与gpioPin0分配给TiGPIO的对象gpio0
			 */
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin0);
			/*
			 * 资源绑定， 创建TiDHT的对象dht并将gpio0和gpioPin0与其绑定
			 */
			TiDHT dht = new TiDHT(gpio0, gpioPin0);
			/*
			 * 资源使用， 设置传感器模式，本例程默认为DHT11 启动测量并获取温度、湿度数据
			 */

			while (true) {
				try
				{
					dht.measure();
					
					double temperature = dht.getTemperature();
					double humidity = dht.getHumidity();
					System.out.println("Temperature = " + temperature + " C");
					System.out.println("Humidity = " + humidity + " %");
					
				}
				catch(IOException ie)
				{
					System.out.println("Error occurred.");
					ie.printStackTrace();  
				}
	
				Delay.msDelay(2000);// DHT11官方资料要求采集间隔至少2秒
				
			}
		} catch (IOException ie) {

			ie.printStackTrace();
		}
	}
}
