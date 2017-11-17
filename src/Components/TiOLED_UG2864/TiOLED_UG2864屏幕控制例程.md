# TiOLED_UG2864屏幕控制例程

## 简介

本例程为TiOLED_UG2864屏幕的控制例程，对应目录如下：

- OLED_UG2864

## 适用TiKit开发板 

1.TiKit-T600-ESP8266A

## TiKit-T600-ESP8266A与TiOLED_UG2864连接说明

### 电气连接

- 3.3V<------>VCC

- GND<------>GND

- SDA<------>SDA

- SCL<------>SCL


### 示意图

![TiOLED_UG2864屏幕控制例程](.\Picture\TiOLED_UG2864屏幕控制例程.JPG)

### 注意事项

1. 按照上述线序连接硬件平台与TiOLED_UG2864，注意VCC与GND，如果接反，会烧毁屏幕；
2. 使用时请注意别用手接触到屏幕背后的电容，否则，有可能造成乱码、显示不全等问题；