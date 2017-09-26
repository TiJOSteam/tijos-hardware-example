import tijos.runtime.deviceaccess.TiI2CMaster;
import java.text.DecimalFormat;
import tijos.runtime.deviceaccess.TiGPIO;
import tijos.runtime.transducer.led.TiOLED_UG2864;
import tijos.runtime.sensor.humiture.TiDHT;
import tijos.runtime.sensor.distance.TiHCSR04;
import tijos.runtime.transducer.buzzer.TiBuzzer;
import tijos.runtime.sensor.button.TiButton;
import tijos.runtime.sensor.button.TiButtonEventListener;

/* 
 * 实现按钮监听接口<br>
 * <p>
 * @author Jason
 *
 */
class ButtonEventListener implements TiButtonEventListener {
	private boolean pressed = false;
	private int pinID;
	
	public ButtonEventListener(int buttonPinID) {
		pinID = buttonPinID;
	}
	/**
	 * 按键按下事件处理
	 */
	public synchronized void onPressed(TiButton arg0) {
		if(arg0.getSignalPinID() == pinID)
			pressed = true;
	}

	/**
	 * 按键释放事件处理
	 */
	public synchronized void onReleased(TiButton arg0) {
		if(arg0.getSignalPinID() == pinID)
			pressed = false;		
	}
	
	public synchronized boolean isPressed() {
		return pressed;
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
		/*
		 * 定义使用的TiI2CMaster port和TiGPIO port
		 */
		int gpioPort0 = 0;
		int i2cPort0 = 0;
		/*
		 * 定义所使用的gpio pin列表
		 * */
		int gpioPin3 = 3;
		int gpioPin4 = 4;
		int gpioPin5 = 5;
		int gpioPin6 = 6;
		int gpioPin7 = 7;
		
		int[] gpioPinList = {gpioPin3, gpioPin4, gpioPin5, gpioPin6, gpioPin7};
		/*
		 * 资源分配， 将i2cPort0分配给TiI2CMaster实例i2c0
		 * 将gpioPort0分配给TiGPIO实例gpio0
		 */
		TiI2CMaster i2c0 = TiI2CMaster.open(i2cPort0);
		TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPinList);

		/*
		 * 资源绑定 
		 * 创建TiOLED_UG2864实例oled并将i2c0与其绑定
		 * 创建TiHCSR04实例distance并将gpioPin3和gpioPin4与其绑定
		 * 创建TiDHT实例dht11并将gpioPinID5与其绑定
		 * 创建TiBuzzer实例buzzer并将gpioPin6与其绑定
		 * 创建TiButton实例button并将gpioPin7与其绑定
		 */
		TiOLED_UG2864 oled = new TiOLED_UG2864(i2c0, 0x78);
		TiHCSR04 distance = new TiHCSR04(gpio0, gpioPin3, gpioPin4);
		TiDHT dht11 = new TiDHT(gpio0, gpioPin5);
		TiBuzzer buzzer = new TiBuzzer(gpio0, gpioPin6, false);
		TiButton button = new TiButton(gpio0, gpioPin7, false);
		
		/*
		 *  创建事件监听实例并设置事件监听
		 * 在事件监听中设置按键消息
		 * */
		ButtonEventListener lcButton = new ButtonEventListener(gpioPin7);
		button.setEventListener(lcButton);
		
		/*
		 * 资源使用
		 * 定义所需要的变量以及需要显示的菜单字符；
		 */
		boolean rstlock = false;
		String distancelockvalue = "0";
		
		buzzer.turnOff();

		String sWelcome = "Welcome!";
		String sModel = "TiOS-Rangefinder";
		String sTemptrue = "TEMP:";
		String sHumidity = "HUMI:";
		String sDistance = "DISTANCE:    m";
		String sTips = "press to measure";
		/*
		 * TiOLED_UG2864屏幕初始操作：
		 * 1.开启屏幕
		 * 2.整屏清除
		 * 3.显示欢迎界面以及仪器型号
		 * */
		oled.turnOn();
		oled.clear();
		oled.print(1, 3, sWelcome);
		oled.print(2, 0, sModel);
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
		}
		/*
		 * 清屏后显示菜单字符，包括温度，湿度，距离以及操作提示
		 * */
		oled.clear();
		oled.print(0, 0, sTemptrue);
		oled.print(1, 0, sHumidity);
		oled.print(2, 0, sDistance);
		oled.print(3, 0, sTips);

		/*
		 *DecimalFormat用于数据截断
		 * */
		DecimalFormat df = new DecimalFormat("#.##");
		while (true) {
			/*
			 * 测量温湿度数据，转换为字符后显示在屏幕对应位置
			 * */
			dht11.measure();
			String temp = String.valueOf(dht11.getTemperature());
			String humi = String.valueOf(dht11.getHumidity());

			oled.print(0, 5, temp + "C ");
			oled.print(1, 5, humi + "% ");
			/*
			 * 测量距离值并截断到小数点后两位，保存到临时变量中
			 * */
			distance.measure();
			String distancevalue = df.format(distance.getDistance());
			/*
			 * 检测按键是否被按下并根据当前状态切换下一次的状态
			 * */
			if (lcButton.isPressed()) {
				
				buzzer.turnOn();
				if (rstlock) {
					rstlock = false;
					distancelockvalue = "0";
				} else {
					rstlock = true;
					distance.measure();
					distancelockvalue = String.valueOf(df.format(distance.getDistance()));
				}
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {

				}
				buzzer.turnOff();
			}
			/*
			 * 根据按钮是否已经被按下选择动态显示所测距离还是已测的保存值
			 * */
			if (rstlock) {
				oled.print(2, 9, distancelockvalue);
				oled.print(3, 9, "unlock ");
			} else {
				oled.print(2, 9, distancevalue);
				oled.print(3, 9, "measure");
			}
		}
	}
}
