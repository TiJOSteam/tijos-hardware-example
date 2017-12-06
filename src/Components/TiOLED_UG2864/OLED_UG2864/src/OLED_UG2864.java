import tijos.framework.devicecenter.TiI2CMaster;
import tijos.framework.transducer.led.TiOLED_UG2864;

/**
 * 此类实现TiOLED_UG2864屏幕功能演示<br>
 * TiOLED_UG2864的使用分为三步：<br>
 * 1.“资源分配”：使用tijos.framework.devicecenter.TiI2CMaster包中TiI2CMaster类的<code>open</code>方法分配I2C对象（注：全局只能分配一次）。<br>
 * 2.“资源绑定”：新创建TiOLED_UG2864对象，将其与1.中分配的I2C对象绑定,并传入设备（屏幕）的地址。<br>
 * 3.“资源使用”：使用tijos.framework.transducer.transducer.led.TiOLED_UG2864类中的相应方法操作屏幕，详情见例程中的操作步骤。<br>
 * <p>
 * @author Jason
 *
 */
public class OLED_UG2864 {
	/**
	 * 程序入口，由TiJOS调用
	 * @param args 入口参数， TiJOS中一直等于null
	 */
	public static void main(String[] args) {
		/*
		 * 定义使用的TiI2CMaster port
		 */
		int i2cPort0 = 0;
		/*
		 * 定义从机地址
		 */
		int i2cAssress = 0x78;
		/*
		 * 资源分配，
		 * 将i2cPort0分配给TiI2CMaster对象i2c0
		 */			
		TiI2CMaster i2c0 = TiI2CMaster.open(i2cPort0);
		/*
		 * 资源绑定，
		 * 创建TiOLED_UG2864对象oled12864并将i2c0与其绑定
		 */	
		TiOLED_UG2864 oled12864 = new TiOLED_UG2864(i2c0, i2cAssress);
		/*
		 * 资源使用，
		 * 操作屏幕并显示字符串
		 * TiOLED_UG2864使用16*8点阵字符，共4行16列， 行范围：0-3, 列范围：0-15
		 * 点阵字库只支持英文字符，注意输入字符串时的格式，尤其是标点符号！
		 */	
		String s0 = "Welcome to the TiKit world !";
		String s1 = "Hello world!";
		String s2 = "TiKit";
		while(true) {
			/*给屏幕上电*/
			boolean err = oled12864.turnOn();
			if(!err) {
				System.out.println("oled turnOn fail");
				break;
			}
			System.out.println("oled is turned on");
			/*清屏*/
			err = oled12864.clear();
			if(!err) 
				System.out.println("oled clear fail");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			/*从第0行0列开始打印字符串*/
			err = oled12864.print(0, 0, s0);
			if(!err) 
				System.out.println("oled print fail");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}	
			err = oled12864.clear();
			if(!err) 
				System.out.println("oled clear fail");
			/*设置行起始坐标和列起始坐标*/
			oled12864.setPosition(1, 2);
			/*在指定位置输出字符串*/
			err = oled12864.output(s1);
			if(!err) 
				System.out.println("oled output fail");			
			oled12864.setPosition(3, 11);
			err = oled12864.output(s2);
			if(!err) 
				System.out.println("oled output fail");	
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {			
			}
			err = oled12864.clear();
			if(!err) 
				System.out.println("oled clear fail");
			/*关闭屏幕*/
			err = oled12864.turnOff();
			if(!err) 
				System.out.println("oled turnOff fail");
			else
				System.out.println("oled is turned off");	
		}
	}
}
