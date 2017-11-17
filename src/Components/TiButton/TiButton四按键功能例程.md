# TiButton四按键功能例程

## 简介

本例程为四个TiButton的控制例程，对应目录如下：

- FourButton

  ​

## 适用TiKit开发板 

1.TiKit-T600-ESP8266A

​

## TiKit-T600-ESP8266A与TiButton连接说明

### 电气连接

- TiKit ---------TiButton

- GND<------>GND

- PIN0<------>B1(S1)

- PIN1<------>B2(S2)

- PIN3<------>B3(S3)

- PIN4<------>B4(S4)

### 示意图

![TiButton四按键功能例程](.\Picture\TiButton四按键功能例程.png)

### 注意事项

1. TiJOS按键处理使用事件方式，性能较高，可用在复杂业务逻辑中，详细使用方法请参考例程源代码。
2. 在按键例程中不能使用TiKit的GPIO的PIN2引脚。
3. 由于硬件平台内部将GPIO的PIN2强制下拉，上电期间不允许上拉到高电平，否则程序无法启动。
4. 若由于实际应用中不可避免使用GPIO的PIN2，则外部电路须保持上电时下拉状态，程序启动后才可自由操作。
5. 实际应用中可根据需要改变GPIO的PIN脚与TiButton的物理连接。