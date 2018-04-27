import tijos.framework.devicecenter.TiI2CMaster;

import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.transducer.led.TiLED;
import tijos.framework.transducer.oled.TiOLED_UG2864;
import tijos.framework.transducer.relay.TiRelay1CH;
import tijos.framework.util.Delay;

import tijos.framework.transducer.buzzer.TiBuzzer;
import tijos.framework.sensor.dht.TiDHT;
import tijos.framework.sensor.mq.TiMQ;
import tijos.framework.sensor.mq.ITiMQEventListener;
import tijos.framework.sensor.button.TiButton;
import tijos.framework.sensor.button.ITiButtonEventListener;

/**
 * 实现按钮监听接口，当有按钮事件发生时，通过状态设置方法关闭报警铃<br>
 * <p>
 * @author Jason
 *
 */
class ButtonEventListener implements ITiButtonEventListener {
	boolean _stop = false;
	/**
	 * 按键按下事件处理
	 */
	public void onPressed(TiButton arg0) {
		synchronized(this) {
			this._stop = true;
		}
	}

	/**
	 * 按键释放事件处理
	 */
	public void onReleased(TiButton arg0) {
	}
	
	public boolean isStop() {
		boolean stop = false;
		synchronized(this) {
			stop = this._stop;
		}
		return stop;
	}	
	
	public void clearStop() {
		synchronized(this) {
			this._stop = false;
		}
	}	
}

/**
 * 实现MQ2监听接口<br> <p>
 * 
 * @author Jason
 *
 */
class MQ2EventListener implements ITiMQEventListener {

	TiMQ _mq2;
	boolean _notify = true;

	public void onThresholdNotify(TiMQ arg0) {
		this._mq2 = arg0;
		synchronized(this) {
			this._notify = true;
		}
	}

	public boolean isNotify() {
		boolean notify = false;
		synchronized(this) {
			notify = this._notify;
		}
		return notify;
	}
	
	public void clearNotify() {
		synchronized(this) {
			this._notify = false;
		}
	}	
}

/**
 * 温湿度监控线程
 * 
 * @author Andy
 *
 */
class HumitureMonitor extends Thread {
	TiDHT _dht11;
	TiOLED_UG2864 _oled;
	
	public HumitureMonitor(TiDHT dht11, TiOLED_UG2864 oled) {
		this._dht11 = dht11;
		this._oled = oled;
	}
	
	@Override
    public void run() {
		double tempLast = Double.NaN;
		double humiLast = Double.NaN;
		/*
		 * 测量温湿度，并显示
		 */
        while(true) {
        	try {
        		this._dht11.measure();
				double temp = this._dht11.getTemperature();
				double humi = this._dht11.getHumidity();
				
				if(tempLast != temp) {
					this._oled.print(0, 5, temp + "C ");
					tempLast = temp;
				}
				
				if(humiLast != humi) {				
					this._oled.print(1, 5, humi + "% ");
					humiLast = humi;
				}
				//延迟2秒再次采集温湿度
				Delay.msDelay(2000);
        	}
        	catch(IOException e) {
        		e.printStackTrace();
        	}
        }
    }
}

/**
 * 此类实现多功能超声波测距仪功能示例<br>
 * 功能说明：<br>
 * 1.实时监测环境温度和湿度，使用TiDHT实现；<br>
 * 2.实现实时监测环境可燃气体浓度，使用TiHCSR04实现；<br>
 * 3.当可燃气体浓度到达警戒值的时候，通过蜂鸣器报警，此时红灯闪烁，使用TiBuzzer和TIOLED实现；<br>
 * 4.当可燃气体浓度达到警戒值时，通过继电器控制自动切断电器电源（模拟场景）；<br>
 * 5.按下按钮后可立即解除报警，使用TiBuzzer实现；<br>
 * 6.无人操作时，报警一直持续到可燃气体浓度恢复到安全值后自动解除，同时恢复供电；<br>
 * 7.通过屏幕显示以上信息，通过TIOLED_UG2864实现；<br>
 * <p>
 * 
 * @author Jason
 *
 */
public class GasMonitoringSample {
	/*
	 * 程序入口，由TiJOS调用
	 * 
	 * @param args 入口参数， TiJOS中一直等于null
	 */
	public static void main(String[] args) {
		try {
			/*
			 * 定义使用的TiI2CMaster port和TiGPIO port
			 */
			int gpioPort0 = 0;
			int i2cPort0 = 0;
			/*
			 * 定义所使用的gpio pin 集合
			 */
			int gpioPin2 = 2;
			int gpioPin3 = 3;
			int gpioPin4 = 4;
			int gpioPin5 = 5;
			int gpioPin6 = 6;
			int gpioPin7 = 7;
			/*
			 * 资源分配， 将i2cPort0分配给TiI2CMaster实例i2c0 将gpioPort0分配给TiGPIO实例gpio0
			 */
			TiI2CMaster i2c0 = TiI2CMaster.open(i2cPort0);
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin2, gpioPin3, gpioPin4, gpioPin5, gpioPin6, gpioPin7);

			/*
			 * 资源绑定 创建TiOLED_UG2864实例oled并将i2c0与其绑定
			 * 创建TiRelay1CH实例relay并将gpioPinID2与其绑定 创建TiMQ实例led并将gpioPinID3与其绑定
			 * 创建TiLED实例mq2并将gpioPinID4与其绑定 创建TiDHT实例dht11并将gpioPinID5与其绑定
			 * 创建TiBuzzer实例buzzer并将gpioPinID6与其绑定
			 * 创建TiButton实例button并将gpioPinID7与其绑定
			 */
			TiOLED_UG2864 oled = new TiOLED_UG2864(i2c0, 0x3C);
			TiRelay1CH relay = new TiRelay1CH(gpio0, gpioPin2);
			TiLED led = new TiLED(gpio0, gpioPin3);
			TiMQ mq2 = new TiMQ(gpio0, gpioPin4);
			TiDHT dht11 = new TiDHT(gpio0, gpioPin5);
			TiBuzzer buzzer = new TiBuzzer(gpio0, gpioPin6);
			TiButton button = new TiButton(gpio0, gpioPin7);
			
			String sWelcome = "Welcome!";
			String sModel = "Ti-GasMonitoring";
			
			String sTemptrue = "TEMP:--";
			String sHumidity = "HUMI:--";
			String sMode = "MODE:-- ";
			String sPrepare = "WAITING...";

			/*
			 * TiOLED_UG2864屏幕初始操作： 1.开启屏幕 2.整屏清除 3.显示欢迎界面以及仪器型号
			 */
			oled.turnOn();
			oled.clear();
			oled.print(1, 4, sWelcome);
			oled.print(2, 0, sModel);
			
			Delay.msDelay(1500);
			
			/*
			 * 清屏后显示菜单字符，包括温度，湿度，工作模式以及报警提示
			 */
			oled.clear();
			oled.print(0, 0, sTemptrue);
			oled.print(1, 0, sHumidity);
			oled.print(2, 0, sMode);
			oled.print(3, 3, sPrepare);
			/*
			 * 延时5秒，等待MQ2传感器加热、初始化
			 */
			Delay.msDelay(5000);
			
			/*
			 * 创建事件监听实例并设置事件监听 在事件监听中设置按键消息
			 */
			ButtonEventListener lcButton = new ButtonEventListener();
			button.setEventListener(lcButton);
			
			MQ2EventListener lcMQ2 = new MQ2EventListener();
			mq2.setEventListener(lcMQ2);
			
			/*
			 * 创建并启动温湿度监控显示线程
			 */
			HumitureMonitor humMonitor = new HumitureMonitor(dht11, oled);
			humMonitor.start();

			/*
			 * 最后打开继电器，模拟给电器供电
			 */
			relay.turnOn();
			
			/*
			 * 主线程报警监控
			 */	
			boolean alarmReady = false;

	        while(true) {
	        	//气体检测通知
	        	if(lcMQ2.isNotify()) {
	        		alarmReady = mq2.isGreaterThanThreshold();
	        		if(alarmReady) {
	        			/*
	        			 * 报警提示，继电器关闭，模拟切断电源
	        			 */
	        			relay.turnOff();
						oled.print(2, 5, "Alarm");
						oled.print(3, 3, "WARNING!!!  ");
	        		}
	        		else {
	        			/*
	        			 * 报警解除，继电器打开，模拟接通电源
	        			 */
	        			relay.turnOn();
						oled.print(2, 5, "Safe ");
						oled.print(3, 3, "WORKING ^_^  ");	        			
	        		}
	        		lcMQ2.clearNotify();
	        	}
	        	//报警检测
	        	if(alarmReady) {
	        		led.turnOver();
	        		if(lcButton.isStop()) {
	        			buzzer.turnOff();
	        		}
	        		else {
		        		if(buzzer.isTurnedOn()) {
		        			buzzer.turnOff();
		        		}
		        		else {
		        			buzzer.turnOn();
		        		}
	        		}
	        	}
	        	else {
	        		led.turnOff();
	        		buzzer.turnOff();
	        		lcButton.clearStop();
	        	}
	        	Delay.msDelay(500);       	
	        }

		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
}
