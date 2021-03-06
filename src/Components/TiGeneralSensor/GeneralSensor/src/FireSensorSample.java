
import tijos.framework.sensor.general.ITiGeneralSensorEventListener;
import tijos.framework.sensor.general.TiGeneralSensor;
import tijos.framework.util.Delay;


import java.io.IOException;

import tijos.framework.devicecenter.TiADC;
import tijos.framework.devicecenter.TiGPIO;

//火焰传感器
class FireSensorEventListener implements ITiGeneralSensorEventListener {

	@Override
	public void onThresholdNotify(TiGeneralSensor arg0) {
		System.out.println("GPIO Event");

	}

}

public class FireSensorSample {

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
						
			/**
			 * AD 通道0
			 */
			int adc_chn = 0;
			
			/*
			 * 资源分配， 将gpioPort与gpioPin0分配给TiGPIO对象gpio0 将adcPort0分配给TiADC对象adc0
			 */
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin0);
						
			TiADC adc0 = TiADC.open(adcPort0, adc_chn);

			/*
			 * 资源绑定， 创建TiGeneralSensor对象并将gpioPort、gpioPortPin和adcPort与其绑定
			 * Pin0<---->D0 ADC <---->A0
			 */
			TiGeneralSensor fireSensor = new TiGeneralSensor(gpio0, gpioPin0, adc0, adc_chn);

			/*
			 * 资源使用， 创建事件监听对象并设置事件监听 在事件监听中处理事件逻辑
			 */
			FireSensorEventListener lc = new FireSensorEventListener();
			fireSensor.setEventListener(lc); 

			while (true) {
				try {

					int ao = fireSensor.getAnalogOutput();
					System.out.println("AO: " + ao);

					if (fireSensor.getDigitalOutput() == 1) {
						System.out.println("high");
					} else {
						System.out.println("low");
					}

					Delay.msDelay(1000);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		} catch (IOException ie) {

			ie.printStackTrace();
		}
	}

}
