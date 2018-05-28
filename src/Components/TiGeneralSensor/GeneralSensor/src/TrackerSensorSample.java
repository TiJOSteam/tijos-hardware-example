import java.io.IOException;

import tijos.framework.devicecenter.TiADC;
import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.sensor.general.ITiGeneralSensorEventListener;
import tijos.framework.sensor.general.TiGeneralSensor;
import tijos.framework.util.Delay;


class TrackerSensorEventListener implements ITiGeneralSensorEventListener {

	@Override
	public void onThresholdNotify(TiGeneralSensor sensor) {

		try {
			if (sensor.getDigitalOutput() == 1) {
				System.out.println("GPIO Event - high ");				
			}
			else {
				System.out.println("GPIO Event - low ");	
			}
		} catch (IOException ie) {

		}
	}

}

public class TrackerSensorSample {

	public static void main(String[] args) {
		 
		try {
			/*
			 * 定义使用的TiGPIO port 
			 */
			int gpioPort0 = 0;
			/*
			 * 定义所使用的gpioPin
			 */
			int gpioPin0 = 0;

			/*
			 * 资源分配， 将gpioPort与gpioPin0分配给TiGPIO对象gpio0 
			 */
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin0);
			
			/*
			 * 资源绑定， 创建TiGeneralSensor对象generalSensor并将gpioPort、
			 * gpioPortPin和adcPort与其绑定 Pin0<---->D0 
			 */
			TiGeneralSensor trackerSensor = new TiGeneralSensor(gpio0, gpioPin0);

			/*
			 * 资源使用， 创建事件监听对象并设置事件监听 在事件监听中处理事件逻辑
			 */
			TrackerSensorEventListener lc = new TrackerSensorEventListener();
			trackerSensor.setEventListener(lc);

			while (true) {
				if (trackerSensor.getDigitalOutput() == 1) {
					System.out.println("DO high ");				
				}
				else {
					System.out.println("DO low ");	
				}
				Delay.msDelay(1000);
			}
		} catch (IOException ie) {

			ie.printStackTrace();
		}

	}

}
