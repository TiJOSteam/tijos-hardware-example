import tijos.framework.devicecenter.TiOWMaster;
import tijos.framework.sensor.humiture.TiDS18B20;

/**
 * 此类实现单个DS18B20数字温度传感器采集温度的功能演示<br>
 * TiDS18B20采集温度数据分为三步：<br>
 * 1.“资源分配”：使用tijos.framework.devicecenter.TiOWMaster包中TiOWMaster类的<code>open</code>方法分配OW（单总线onewire）对象。<br>
 * 2.“资源绑定”：新创建TiDS18B20对象，将其与1.中分配的OW对象以及指定io绑定。<br>
 * 3.“资源使用”：当同一根总线上只有一个DS18B20传感器时，调用tijos.framework.sensor.humiture.TiDS18B20类中的<code>selectSingle</code>方法选择单个传感器,
 * 然后循环调用该类中的<code>measure</code>方法开始测量，并调用<code>getTemperature</code>方法获取温度；<br>
 * 当同一根总线上上挂有多个DIS18B20传感器时，需要先调用tijos.framework.sensor.humiture.TiDS18B20类中的<code>enumeration</code>方法枚举总线上的设备,<br>
 * 然后再调用该类中的<code>select</code>方法选择要操作的传感器，最后再循环调用<code>measure</code>方法并调用<code>getTemperature</code>方法获取对应温度传感器的温度。
 * <p>
 * @author Jason
 *
 */
public class DS18B20 {
	/**
	 * 程序入口，由TiJOS调用
	 * @param args 入口参数， TiJOS中一直等于null
	 */
	public static void main(String[] args) {
		/*
		 * 定义使用的TiOWMaster port
		 */
		int owPort0 = 0;
		/*
		 * 定义使用的TiOWMaster io
		 */
		int owIo0 = 0;
		/*
		 * 资源分配，
		 * 将owPort0与owIo0分配给TiOWMaster的对象ow0
		 */			
		TiOWMaster ow0 = TiOWMaster.open(owPort0, owIo0);
		/*
		 * 资源绑定，
		 * 创建TiDS18B20对象ds18b20并将ow0和owIo0与其绑定
		 */	
		TiDS18B20 ds18b20 = new TiDS18B20(ow0, owIo0);
		/*
		 * 资源使用，
		 * 选择只有单传感器模式
		 * 启动测量并获取温度数据
		 */
		ds18b20.selectSingle();

		while(true){
			int err = ds18b20.measure();
			if(err < 0) {
				System.out.println("Error = "+ err);
			}
			else {
				double temperature = ds18b20.getTemperature();
				System.out.println("Temperature = "+temperature+" C");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}	
	}
}
