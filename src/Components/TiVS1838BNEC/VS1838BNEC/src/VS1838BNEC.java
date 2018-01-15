import java.io.IOException;

import tijos.framework.devicecenter.TiGPIO;
import tijos.framework.sensor.vs1838b.TiVS1838BNEC;
import tijos.util.Delay;
import tijos.framework.sensor.vs1838b.ITiVS1838BNECEventListener;

/**
 * 1.此类实现了ITiVS1838BNECEventListener接收事件监听接口<br>
 * 在<code>cmdReceived</code>与<code>cmdRepead</code>方法中不要阻塞处理事件太久，<br>
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
class VS1838BNECEventListener implements ITiVS1838BNECEventListener {
	/**
	 * 接收事件处理
	 */
	public void cmdReceived(TiVS1838BNEC arg0) {
		System.out.println("Received:" + arg0.getAddress() + "," + arg0.getCommand());
	}

	/**
	 * 接收重复事件处理
	 */
	public void cmdRepeat(TiVS1838BNEC arg0) {
		System.out.println("Repeat:" + arg0.getAddress() + "," + arg0.getCommand());
	}
}

/**
 * 此类实现单个TiVS1838BNEC的事件功能演示，TiJOS中必须使用事件方式处理接收动作<br>
 * TiVS1838BNEC事件的使用分为三步：<br>
 * 1.“资源分配”，使用tijos.framework.devicecenter包中TiGPIO类<code>open</code>方法分配GPIO对象（注：全局只能分配一次）。<br>
 * 2.“资源绑定”，新创建TiVS1838BNEC对象，将其与1.中分配的GPIO对象以及指定pin绑定。<br>
 * 3.“资源使用”，向TiVS1838BNEC对象中设置事件监听对象，事件监听类需要继承TiVS1838BNECEventListener接口，根据发生的事件类型处理事件逻辑。<br>
 * <p>
 * 
 * @author Andy
 *
 */
public class VS1838BNEC {
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
			 * 定义使用的TiGPIO portPin
			 */
			int gpioPin0 = 0;
			/*
			 * 资源分配， 将gpioPort0与gpioPin0分配给TiGPIO的对象gpio0
			 */
			TiGPIO gpio0 = TiGPIO.open(gpioPort0, gpioPin0);
			/*
			 * 资源绑定， 创建TiVS1838BNEC的对象vs1838b并将gpio0和gpioPin0与其绑定
			 */
			TiVS1838BNEC vs1838bNec = new TiVS1838BNEC(gpio0, gpioPin0);
			/*
			 * 资源使用， 创建事件监听对象并设置事件监听 在事件监听中处理接收事件逻辑
			 */
			VS1838BNECEventListener lc = new VS1838BNECEventListener();
			vs1838bNec.setEventListener(lc);

			while (true) {
				Delay.msDelay(1000);
			}
			
		} catch (IOException ie) {

			ie.printStackTrace();
		}
	}
}
