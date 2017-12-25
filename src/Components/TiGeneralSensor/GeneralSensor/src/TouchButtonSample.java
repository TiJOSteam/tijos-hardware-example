
import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.sensor.button.TiButton;
import tijos.framework.sensor.button.ITiButtonEventListener;

//触摸按键

class OneButtonEventListener implements ITiButtonEventListener {

	/**
	 * 按键按下事件处理
	 */
	public void onPressed(TiButton arg0) {
		System.out.println("onPressed" + "  time(us):" + arg0.getEventTime());
	}

	/**
	 * 按键释放事件处理
	 */
	public void onReleased(TiButton arg0) {
		System.out.println("onReleased" + "  time(us):" + arg0.getEventTime());
	}
}

public class TouchButtonSample {
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
			 * 资源绑定， 创建TiButton对象buttonS1并将gpio0和gpioPin0与其绑定
			 */
			TiButton buttonS1 = new TiButton(gpio0, gpioPin0);

			OneButtonEventListener lc = new OneButtonEventListener();
			buttonS1.setEventListener(lc);

			while (true) {
				try {

					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		} catch (IOException ie) {

			ie.printStackTrace();
		}
	}
}
