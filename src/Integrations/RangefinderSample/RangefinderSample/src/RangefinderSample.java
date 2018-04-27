import tijos.framework.devicecenter.TiI2CMaster;
import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.transducer.oled.TiOLED_UG2864;
import tijos.framework.util.Delay;
import tijos.framework.sensor.dht.TiDHT;
import tijos.framework.sensor.hcsr.TiHCSR04;
import tijos.framework.transducer.buzzer.TiBuzzer;
import tijos.framework.sensor.button.TiButton;
import tijos.framework.sensor.button.ITiButtonEventListener;

import java.io.IOException;
import java.text.DecimalFormat;

/* 
 * 实现按钮监听接口<br>
 * <p>
 * @author Jason
 *
 */
class ButtonEventListener implements ITiButtonEventListener {
	boolean _lock = false;
	boolean _notify = false;
	
	/**
	 * 按键按下事件处理
	 */
	public void onPressed(TiButton arg0) {
		synchronized(this) {
			this._lock = !this._lock;
			this._notify = true;
		}
	}

	/**
	 * 按键释放事件处理
	 */
	public void onReleased(TiButton arg0) {
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

	public boolean checkLock() {
		boolean lock = false;
		synchronized(this) {
			lock = this._lock;
		}
		return lock;
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
 * 2.实现实时超声波测量距离，使用TiHCSR04实现；<br>
 * 3.实现按下按钮测距并发出提示声，使用TiButton以及TiBuzzer实现；<br>
 * 4.确认测量方向和位置后，按下按钮锁定测量值，并保存该值方便读取；<br>
 * 5.通过屏幕显示以上信息，通过TIOLED_UG2864实现；<br>
 * 6.再次按下按钮，解锁为自由测量模式，实时显示测量值<br>
 * <p>
 * 
 * @author Jason
 *
 */
public class RangefinderSample {
	/**
	 * 程序入口，由TiJOS调用
	 * 
	 * @param args
	 *            入口参数， TiJOS中一直等于null
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
			int gpioPin3 = 3;
			int gpioPin4 = 4;
			int gpioPin5 = 5;
			int gpioPin6 = 6;
			int gpioPin7 = 7;
			/*
			 * 资源分配， 将i2cPort0分配给TiI2CMaster实例i2c0 将gpioPort0分配给TiGPIO实例gpio0
			 */
			TiI2CMaster i2c0 = TiI2CMaster.open(i2cPort0);
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin3, gpioPin4, gpioPin5, gpioPin6, gpioPin7);

			/*
			 * 资源绑定 创建TiOLED_UG2864实例oled并将i2c0与其绑定
			 * 创建TiHCSR04实例distance并将gpioPin3和gpioPin4与其绑定
			 * 创建TiDHT实例dht11并将gpioPinID5与其绑定 创建TiBuzzer实例buzzer并将gpioPin6与其绑定
			 * 创建TiButton实例button并将gpioPin7与其绑定
			 */
			TiOLED_UG2864 oled = new TiOLED_UG2864(i2c0, 0x3C);
			TiHCSR04 distance = new TiHCSR04(gpio0, gpioPin3, gpioPin4);
			TiDHT dht11 = new TiDHT(gpio0, gpioPin5);
			TiBuzzer buzzer = new TiBuzzer(gpio0, gpioPin6);
			TiButton button = new TiButton(gpio0, gpioPin7);

			/*
			 * 资源使用 定义所需要的变量以及需要显示的菜单字符；
			 */
			buzzer.turnOff();

			String sWelcome = "Welcome!";
			String sModel = "TiOS-Rangefinder";
			
			String sTemptrue = "TEMP:--";
			String sHumidity = "HUMI:--";
			String sDistance = "DISTANCE:--  m";
			String sTips = "press to measure";
			/*
			 * TiOLED_UG2864屏幕初始操作： 1.开启屏幕 2.整屏清除 3.显示欢迎界面以及仪器型号
			 */
			oled.turnOn();
			oled.clear();
			oled.print(1, 3, sWelcome);
			oled.print(2, 0, sModel);
			
			Delay.msDelay(2000);

			/*
			 * 清屏后显示菜单字符，包括温度，湿度，距离以及操作提示
			 */
			oled.clear();
			oled.print(0, 0, sTemptrue);
			oled.print(1, 0, sHumidity);
			oled.print(2, 0, sDistance);
			oled.print(3, 0, sTips);
			
			/*
			 * 创建事件监听实例并设置事件监听 在事件监听中设置按键消息
			 */
			ButtonEventListener lcButton = new ButtonEventListener();
			button.setEventListener(lcButton);
			
			/*
			 * 创建并启动温湿度监控显示线程
			 */
			HumitureMonitor humMonitor = new HumitureMonitor(dht11, oled);
			humMonitor.start();

			/*
			 * DecimalFormat用于数据截断
			 */
			DecimalFormat df = new DecimalFormat("#.##");
			
			/*
			 * 测距开始
			 */
			while (true) {
				/*
				 * 测量距离值并截断到小数点后两位，保存到临时变量中
				 */
				distance.measure();	
				double dis = distance.getDistance();
				
				String distancevalue = null;
				if(Double.isNaN(dis)) {
					distancevalue = " NaN";
				}
				else {
					distancevalue = df.format(dis);
				}
				
				/*
				 * 检测测距使能按键是否按了
				 */
				if (lcButton.isNotify()) {
					//蜂鸣提示
					buzzer.turnOn();
					Delay.msDelay(100);
					buzzer.turnOff();
					
					if(lcButton.checkLock()) {
						oled.print(2, 9, distancevalue);
						oled.print(3, 9, "unlock ");
					}
					else {
						oled.print(3, 9, "measure");
					}

					lcButton.clearNotify();
				}
				
				if(!lcButton.checkLock()) {
					oled.print(2, 9, distancevalue);
				}
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}
	}
}
