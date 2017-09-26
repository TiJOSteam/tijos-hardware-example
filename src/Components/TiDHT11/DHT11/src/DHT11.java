import tijos.runtime.deviceaccess.TiGPIO;
import tijos.runtime.sensor.humiture.TiDHT;

/**
 * 此类实现DHT11数字温湿度传感器采集温度、湿度的功能演示<br>
 * TiDHT11采集温度数据分为三步：<br>
 * 1.“资源分配”：使用tijos.runtime.deviceaccess.TiGPIO包中TiGPIO类的<code>open</code>方法分配GPIO对象（注：全局只能分配一次）。<br>
 * 2.“资源绑定”：新创建TiDHT对象，将其与1.中分配的GPIO对象以及指定pin绑定。<br>
 * 3.“资源使用”：使用tijos.runtime.sensor.humiture.TiDHT类中的<code>setModel11<code>方法（11代表DHT11，22代表DHT22，默认为DHT11）设置模式，<br>
 * 然后再循环调用该类中的<code>measure<code>方法进行测量，并调用方法<code>getTemperature<code>和<code>getTemperature<code>获取测量到的温度值和湿度值<br>
 * <p>
 * @author Jason
 *
 */
public class DHT11 {
	/**
	 * 程序入口，由TiJOS调用
	 * @param args 入口参数， TiJOS中一直等于null
	 */
	public static void main(String[] args) {
		/*
		 * 定义使用的TiGPIO port
		 */
		int gpioPort0 = 0;
		/*
		 * 定义使用的TiGPIO portPin
		 */
		int gpioPin0 = 0;
		/*
		 * 定义使用的TiGPIO的pin 列表
		 */		
		int[] pinIDList = {gpioPin0};
		/*
		 * 资源分配，
		 * 将gpioPort0与PinIDList分配给TiGPIO的对象gpio0
		 */			
		TiGPIO gpio0 = TiGPIO.open(gpioPort0, pinIDList);
		/*
		 * 资源绑定，
		 * 创建TiDHT的对象dht并将gpio0和gpioPin0与其绑定
		 */	
		TiDHT dht = new TiDHT(gpio0, gpioPin0);
		/*
		 * 资源使用，
		 * 设置传感器模式，本例程默认为DHT11
		 * 启动测量并获取温度、湿度数据
		 */
		dht.setModel11();

		while(true) {
			int err = dht.measure();
			if(err < 0) {
				System.out.println("Error = "+ err);
			}
			else {
				double temperature = dht.getTemperature();
				double humidity = dht.getHumidity();
				System.out.println("Temperature = "+temperature+" C");
				System.out.println("Humidity = "+humidity+" %");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}
}
