Car-eye-device
 
car-eye 设备子系统提供了一整套硬件设计，固件程序，系统软件，行车记录仪等功能模块得解决方案

其中SH-CAMERA 是设备端行车记录仪模块，该功能模块实现了传统的DVR功能。还支持远程控制，支持远程观看实时视频和检索录像。


多路行车记录仪：sh_camera

sh_camera 是car-eye开源团队开发的基于android系统的一个应用程序。主要界面如下图：

![](https://github.com/Car-eye-team/doc/raw/master/car-eye-device/car-eye-camera主界面.jpg)

其中面板的中按钮依次为拍照，录像，实时上传，打开回放，内部可以远程和本地回放文件，设置按钮

设置界面的图大致如下：

![](https://github.com/Car-eye-team/doc/raw/master/car-eye-device/car-eye-camera设置界面.jpg)

其中服务器IP是流媒体服务的IP地址
端口是服务器的端口
设备号是设备编码，如果使在car-server上注册为手机号码，则可以用car-eye-client远程控制设备开始推流。

推流的格式如下：

实时直播流：
rtsp://120.76.235.109:10554/13510671870?channel=1.sdp
IP为：120.76.235.109
端口为:10554
注册手机号码为13510671870?
通道0

其中流媒体服务器上的IP和设备端设置的IP，客户端的IP保持一致

回放流：
rtsp://120.76.235.109:10554/13510671870-channel=1.sdp

可以通过第三方播放器，如VLC等进行播放。在car-eye注册平台注册账号，并在客户端控制播放。
注册网址为：http://39.108.246.45:800/

car-eye车辆管理平台：www.car-eye.cn; car-eye开源平台网址：https://github.com/Car-eye-team/Car-eye-device 有关car-eye 可以加QQ群590411159。
