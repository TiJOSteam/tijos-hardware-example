
import tijos.framework.sensor.general.ITiGeneralSensorEventListener;
import tijos.framework.sensor.general.TiGeneralSensor;

import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;

//HCSR505人体感应开关 - Human body detector module
class HumanSensorEventListener implements ITiGeneralSensorEventListener {

	@Override
	public void onThresholdNotify(TiGeneralSensor arg0) {
		System.out.println("GPIO Event");

	}
}

public class HumanSensorSample {
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
			 * 定义使用的TiGPIO pin
			 */
			int gpioPin0 = 0;

			/*
			 * 资源分配， 将gpioPort0与gpioPin0分配给TiGPIO对象gpio0
			 */
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin0);
			/*
			 * 资源绑定， 创建TiGeneralSensor对象HCSR505并将gpio0和gpioPin0与其绑定
			 */
			TiGeneralSensor hcsr505 = new TiGeneralSensor(gpio0, gpioPin0);

			HumanSensorEventListener lc = new HumanSensorEventListener();
			hcsr505.setEventListener(lc);

			while (true) {
				try {

					if (hcsr505.getDigitalOutput() == 1) {
						System.out.println("Human is detected");
					} else {
						System.out.println("No human");
					}

					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
			
		}catch (IOException ie) {

			ie.printStackTrace();
		} 
		
	}
}