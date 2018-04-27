import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.sensor.button.TiButton;
import tijos.framework.util.Delay;
import tijos.framework.sensor.button.ITiButtonEventListener;

/**
 * 1.此类实现了ITiButtonEventListener四按键事件监听接口<br>
 * 在<code>onPressed</code>与<code>onReleased</code>方法中不要阻塞处理事件太久，<br>
 * 因为TiJOS系统所有事件分发在同一个事件监听线程中，若阻塞<br>
 * 太久会影响其他事件的分发，导致事件响应不及时甚至丢失。<br>
 * <p>
 * 2.如果实际应用中，需要较长时间来处理某事件，建议在新的线程中<br>
 * 处理。<br>
 * <p>
 * 
 * @author Andy
 *
 */
class FourButtonEventListener implements ITiButtonEventListener {

	/**
	 * 按键按下事件处理
	 */
	public void onPressed(TiButton arg0) {
		System.out
				.println("onPressed" + "  time(us):" + arg0.getEventTime() + "  buttonPinID:" + arg0.getSignalPinID());
	}

	/**
	 * 按键释放事件处理
	 */
	public void onReleased(TiButton arg0) {
		System.out
				.println("onReleased" + "  time(us):" + arg0.getEventTime() + "  buttonPinID:" + arg0.getSignalPinID());
	}
}

/**
 * 此类实现四个TiButton的事件功能演示，TiJOS中建议使用事件方式处理按键动作<br>
 * TiButton事件的使用分为三步：<br>
 * 1.“资源分配”，使用tijos.framework.devicecenter包中TiGPIO类<code>open</code>方法分配GPIO对象（注：全局只能分配一次）。<br>
 * 2.“资源绑定”，新创建TiButton对象，将其与1.中分配的GPIO对象以及指定pin绑定。<br>
 * 3.“资源使用”，向TiButton对象中设置事件监听对象，事件监听类需要继承TiButtonEventListener接口，根据发生的事件类型处理事件逻辑。<br>
 * <p>
 * 
 * @author Andy
 *
 */
public class FourButton {
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
			int gpioPin1 = 1;
			int gpioPin3 = 3;
			int gpioPin4 = 4;
			/*
			 * 资源分配， 将gpioPort0与gpioPin0/1/3/4分配给TiGPIO对象gpio0
			 */
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin0, gpioPin1, gpioPin3, gpioPin4);
			/*
			 * 资源绑定， 创建TiButton对象buttonS1/S2/S3/S4并将gpio0和gpioPin0/1/3/4与其绑定
			 */
			TiButton buttonS1 = new TiButton(gpio0, gpioPin0);
			TiButton buttonS2 = new TiButton(gpio0, gpioPin1);
			TiButton buttonS3 = new TiButton(gpio0, gpioPin3);
			TiButton buttonS4 = new TiButton(gpio0, gpioPin4);
			/*
			 * 资源使用， 创建事件监听对象并设置事件监听 在事件监听中处理按键事件逻辑
			 */
			FourButtonEventListener lc = new FourButtonEventListener();
			buttonS1.setEventListener(lc);
			buttonS2.setEventListener(lc);
			buttonS3.setEventListener(lc);
			buttonS4.setEventListener(lc);

			while (true) {
				Delay.msDelay(1000);
			}
		} catch (IOException ie) {

			ie.printStackTrace();
		}
	}
}
