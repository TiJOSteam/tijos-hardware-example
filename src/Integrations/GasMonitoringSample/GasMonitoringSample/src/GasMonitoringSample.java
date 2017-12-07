import tijos.framework.devicecenter.TiI2CMaster;
import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.transducer.led.TiLED;
import tijos.framework.transducer.led.TiOLED_UG2864;
import tijos.framework.transducer.relay.TiRelay1CH;
import tijos.framework.transducer.buzzer.TiBuzzer;
import tijos.framework.sensor.humiture.TiDHT;
import tijos.framework.sensor.gas.TiMQ;
import tijos.framework.sensor.gas.ITiMQEventListener;
import tijos.framework.sensor.button.TiButton;
import tijos.framework.sensor.button.ITiButtonEventListener;

/* 
 * 实现按钮监听接口，当有按钮事件发生时，通过状态设置方法关闭报警铃<br>
 * <p>
 * @author Jason
 *
 */
class ButtonEventListener implements ITiButtonEventListener {

	/**
	 * 按键按下事件处理
	 */
	public void onPressed(TiButton arg0) {
		GasMonitoringSample.stopAlarm();		
	}

	/**
	 * 按键释放事件处理
	 */
	public void onReleased(TiButton arg0) {			
	}	
}

/* 
 * 实现MQ2监听接口<br>
 * <p>
 * @author Jason
 *
 */
class MQ2EventListener implements ITiMQEventListener {
	
	private TiMQ _mq2 = null;
	private boolean _alarm = false;
	
	public void onThresholdNotify(TiMQ arg0) {
		this._mq2 = arg0;
		synchronized(this) {
			this._alarm = this._mq2.isGreaterThanThreshold();
		}
	}
	
	public synchronized boolean isAlarm() {
		return this._alarm;
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
	 * @param args
	 *            入口参数， TiJOS中一直等于null
	 */
	public static boolean isCanceled = false;
	
	public static void stopAlarm(){
		isCanceled = true;
	}
	public static void main(String[] args) {
		/*
		 * 定义使用的TiI2CMaster port和TiGPIO port
		 */
		int gpioPort0 = 0;
		int i2cPort0 = 0;
		/*
		 * 定义所使用的gpio pin 集合
		 * */
		int gpioPin2 = 2;
		int gpioPin3 = 3;
		int gpioPin4 = 4;
		int gpioPin5 = 5;
		int gpioPin6 = 6;
		int gpioPin7 = 7;
		/*
		 * 资源分配， 将i2cPort0分配给TiI2CMaster实例i2c0
		 * 将gpioPort0分配给TiGPIO实例gpio0
		 */
		TiI2CMaster i2c0 = TiI2CMaster.open(i2cPort0);
		TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin2, gpioPin3, gpioPin4, gpioPin5, gpioPin6, gpioPin7);

		/*
		 * 资源绑定 
		 * 创建TiOLED_UG2864实例oled并将i2c0与其绑定
		 * 创建TiRelay1CH实例relay并将gpioPinID2与其绑定
		 * 创建TiMQ实例led并将gpioPinID3与其绑定
		 * 创建TiLED实例mq2并将gpioPinID4与其绑定
		 * 创建TiDHT实例dht11并将gpioPinID5与其绑定
		 * 创建TiBuzzer实例buzzer并将gpioPinID6与其绑定
		 * 创建TiButton实例button并将gpioPinID7与其绑定
		 */
		TiOLED_UG2864 oled = new TiOLED_UG2864(i2c0, 0x78);
		TiRelay1CH relay = new TiRelay1CH(gpio0, gpioPin2, true);
		TiLED led = new TiLED(gpio0, gpioPin3);
		TiMQ mq2 = new TiMQ(gpio0, gpioPin4);
		TiDHT dht11 = new TiDHT(gpio0, gpioPin5);
		TiBuzzer buzzer = new TiBuzzer(gpio0, gpioPin6, false);
		TiButton button = new TiButton(gpio0, gpioPin7, false);
		
		/*
		 * 资源使用
		 * 关闭蜂鸣器
		 * 定义所需要的变量以及需要显示的菜单字符；
		 */
		
		buzzer.turnOff();
		
		String sWelcome = "Welcome!";
		String sModel = "Ti-GasMonitoring";
		String sTemptrue = "TEMP:";
		String sHumidity = "HUMI:";
		String sMode = "MODE:       ";
		String sPrepare = "WAITING...";

		/*
		 * TiOLED_UG2864屏幕初始操作：
		 * 1.开启屏幕
		 * 2.整屏清除
		 * 3.显示欢迎界面以及仪器型号
		 * */
		oled.turnOn();
		oled.clear();
		oled.print(1, 4, sWelcome);
		oled.print(2, 0, sModel);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
		}
		/*
		 * 清屏后显示菜单字符，包括温度，湿度，工作模式以及报警提示
		 * */
		oled.clear();
		oled.print(0, 0, sTemptrue);
		oled.print(1, 0, sHumidity);
		oled.print(2, 0, sMode);
		oled.print(3, 3, sPrepare);
		/*
		 * 延时5秒，等待MQ2传感器加热、初始化
		 * */
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
		/*
		 *  创建事件监听实例并设置事件监听
		 * 在事件监听中设置按键消息
		 * */
		ButtonEventListener lcButton = new ButtonEventListener();
		button.setEventListener(lcButton);
		
		MQ2EventListener lcMQ2 = new MQ2EventListener();
		mq2.setEventListener(lcMQ2);
		
		/*
		 * 最后打开继电器，模拟给电器供电
		 * */
		relay.turnOn();
		
		while (true) {
			/*
			 * 测量温湿度，并显示
			 * */
			dht11.measure();
			String temp = String.valueOf(dht11.getTemperature());
			String humi = String.valueOf(dht11.getHumidity());

			oled.print(0, 5, temp + "C ");
			oled.print(1, 5, humi + "% ");
			/*
			 * 发生可燃气体浓度高报警事件，并进行处理
			 * */
			if(lcMQ2.isAlarm()){
				if(isCanceled){
					/*
					 * 如果按钮被按下，则关闭蜂鸣器的报警，报警灯依旧闪烁，屏幕显示报警信息
					 * */
					buzzer.turnOff();
					led.turnOn();	
				}
				else{
					buzzer.turnOn();
					led.turnOn();
				}
				relay.turnOff();
				oled.print(2, 5, "Alarm");
				oled.print(3, 3, "WARNING!!!  ");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
				buzzer.turnOff();
				led.turnOff();
			}
			else{
				isCanceled = false;
				buzzer.turnOff();
				relay.turnOn();
				led.turnOff();
				oled.print(2, 5, "Safe ");
				oled.print(3, 3, "WORKING ^_^  ");
			}
		}
	}
}
