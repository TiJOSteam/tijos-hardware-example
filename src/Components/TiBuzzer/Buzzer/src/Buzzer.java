import tijos.runtime.deviceaccess.TiGPIO;
import tijos.runtime.transducer.buzzer.TiBuzzer;

/**
 * 此类实现TiBuzzer蜂鸣器控制功能演示<br>
 * TiBuzzer控制分为三步：<br>
 * 1.“资源分配”：使用tijos.runtime.deviceaccess.TiGPIO包中TiGPIO类的<code>open</code>方法分配GPIO对象（注：全局只能分配一次）。<br>
 * 2.“资源绑定”：新创建TiBuzzer对象，将其与1.中分配的GPIO对象以及指定pin绑定，同时选择蜂鸣器的驱动方式。<br>
 * 3.“资源使用”：使用tijos.runtime.transducer.buzzer.TiBuzzer类中的<code>turnOn<code>以及<code>turnOff<code>控制蜂鸣器的开关<br>
 * <p>
 * @author Jason
 *
 */
public class Buzzer {
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
		 * 定义使用的TiGPIO Pin
		 */
		int gpioPin0 = 0;
		/*
		 * 定义使用的TiGPIO的pin 列表
		 */		
		int[] pinIDList = {gpioPin0};
		/*
		 * 资源分配，
		 * 将gpioPort0与pinIDList分配给TiGPIO的对象gpio0
		 */			
		TiGPIO gpio0 = TiGPIO.open(gpioPort0, pinIDList);
		/*
		 * 资源绑定，
		 * 创建TiBuzzer的对象buzzer并将gpio0和gpioPin0与其绑定
		 * 默认使用低电平控制蜂鸣器开启
		 */	
		TiBuzzer buzzer = new TiBuzzer(gpio0, gpioPin0, false);

		/*
		 * 资源使用，
		 * 控制蜂鸣器开启和关闭
		 */
		while(true){
			buzzer.turnOn();
			System.out.println("buzzer is turned on");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
			
			buzzer.turnOff();
			System.out.println("buzzer is turned off");
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}	
	}
}
