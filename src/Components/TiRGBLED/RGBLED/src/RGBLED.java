import tijos.framework.devicecenter.TiPWM;
import tijos.framework.transducer.led.TiRGBLED;
import tijos.framework.util.Delay;

import java.io.IOException;
import java.util.Random;

/**
 * 此类实现TiRGBLED灯控制功能演示<br>
 * TiRGBLED控制分为三步：<br>
 * 1.“资源分配”：使用tijos.framework.devicecenter.TiPWM包中TiPWM类的<code>open</code>方法分配PWM对象（注：全局只能分配一次）。<br>
 * 2.“资源绑定”：新创建TiRGBLED对象，将其与1.中分配的PWM对象以及指定通道（ch）绑定，同时设定灯的控制方式（默认为输出高电平点亮）。<br>
 * 3.“资源使用”：使用tijos.framework.transducer.led.TiRGBLED类中的<code>setPeriod<code>设置PWM周期，<br>
 * 然后通过调用方法<code>setRedBrightness<code>、<code>setGreenBrightness<code>、<code>setBlueBrightness<code><br>
 * 调整三个基色（红色、蓝色、绿色）的亮度，最后调用方法<code>updateBrightness<code>将参数统一设置后生效，以达到合成不同颜色光的目的。<br>
 * <p>
 * 
 * @author Jason
 *
 */
public class RGBLED {
	/**
	 * 程序入口，由TiJOS调用
	 * 
	 * @param args
	 *            入口参数， TiJOS中一直等于null
	 */
	public static void main(String[] args) {
		try {
			
			System.out.println("Start...");
			
			/*
			 * 定义使用的TiPWM port
			 */
			int pwmPort0 = 0;
			/*
			 * 定义使用的TiPWM ch（通道）
			 */
			int ch0 = 0;
			int ch1 = 1;
			int ch2 = 2;
			/*0
			 * 资源分配， 将gpioPort与ch0/1/2分配给TiGPIO对象pwm0
			 */
			TiPWM pwm0 = TiPWM.open(pwmPort0, ch0, ch1, ch2);
			
			/*
			 * 资源绑定， 创建TiRGBLED对象rgbled并将gpioPort和相应的(通道)ch与其对应的颜色绑定 red-----ch0
			 * green---ch1 blue----ch2
			 */
			TiRGBLED rgbled = new TiRGBLED(pwm0, ch0, ch1, ch2);
			/*
			 * 资源使用， 设置pwm输出的周期,默认为1000hz
			 * 控制三种基色的亮度(各个颜色的亮度值变化范围为0-255)，达到合成不同颜色光的效果
			 * 本例程采用取0-255随机数的方式随机设置各个基色的亮度，达到无序变色的效果
			 */
			Random random = new Random();
			
			rgbled.setFrequency(1000);
			while (true) {
				int r = 0, g = 0, b = 0;
				int max = 255;
				int min = 0;
				r = random.nextInt(max) % (max - min + 1) + min;
				g = random.nextInt(max) % (max - min + 1) + min;
				b = random.nextInt(max) % (max - min + 1) + min;

				rgbled.setRedBrightness(r);
				rgbled.setGreenBrightness(g);
				rgbled.setBlueBrightness(b);
				
				// 亮度更新
				rgbled.updateBrightness();

				System.out.println("redlevel=" + r);
				System.out.println("greenlevel=" + g);
				System.out.println("bluelevel=" + b);
				
				Delay.msDelay(100);
			}
		} catch (IOException ie) {

			ie.printStackTrace();
		}

	}
}
