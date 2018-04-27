import java.io.IOException;

import tijos.framework.devicecenter.TiADC;
import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.sensor.general.ITiGeneralSensorEventListener;
import tijos.framework.sensor.general.TiGeneralSensor;
import tijos.framework.util.Delay;


class SpeedSensorEventListener implements ITiGeneralSensorEventListener {

	private int counter = 0;

	public int getCounter() {
		return counter;
	}

	public void resetCounter() {
		this.counter = 0;
	}

	@Override
	public void onThresholdNotify(TiGeneralSensor sensor) {

		//System.out.println("GPIO Event");

		try {
			if (sensor.getDigitalOutput() == 1) {
				this.counter++;
			}
		} catch (IOException ie) {

		}

	}

}

public class SpeedSensorSample {

	public static void main(String[] args) {
		try {
			/*
			 * 定义使用的TiGPIO port 定义使用的TiADC port
			 */
			int adcPort0 = 0;
			int gpioPort0 = 0;
			/*
			 * 定义所使用的gpioPin
			 */
			int gpioPin0 = 0;
			/*
			 * 资源分配， 将gpioPort与gpioPin0分配给TiGPIO对象gpio0 将adcPort0分配给TiADC对象adc0
			 */
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin0);
			TiADC adc0 = TiADC.open(adcPort0);
			/**
			 * AD 通道0
			 */
			int adc_chn = 0;
			
			/*
			 * 资源绑定， 创建TiGeneralSensor对象并将gpioPort、gpioPortPin和adcPort与其绑定
			 * Pin0<---->D0 ADC <---->A0
			 */
			TiGeneralSensor speedSensor = new TiGeneralSensor(gpio0, gpioPin0, adc0, adc_chn);

			/*
			 * 资源使用， 创建事件监听对象并设置事件监听 在事件监听中处理事件逻辑
			 */
			SpeedSensorEventListener lc = new SpeedSensorEventListener();
			speedSensor.setEventListener(lc);

			System.out.println("Start ");
			lc.resetCounter();
			long startTime = System.currentTimeMillis();
			
			while (true) {
				Delay.msDelay(5000);
				
				long interval = (System.currentTimeMillis() - startTime) / 1000;
				double speed = lc.getCounter() / interval;
				
				lc.resetCounter();
				startTime = System.currentTimeMillis();
				
				System.out.println("speed(counter/secount) = " + speed );

			}
		} catch (IOException ie) {

			ie.printStackTrace();
		}

	}

}
