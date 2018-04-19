# Car-eye-device
 
car-eye 设备子系统提供了一整套硬件设计，固件程序，系统软件，行车记录仪等功能模块得解决方案

其中SH-CAMERA 是设备端行车记录仪模块，该功能模块实现了传统的DVR功能。还支持远程控制，支持远程观看实时视频和检索录像。


## 多路行车记录仪功能介绍

sh_camera 是car-eye开源团队开发的基于android系统的一个应用程序。主要界面如下图：

![](https://github.com/Car-eye-team/Car-eye-device/blob/master/picture/car-eye-camera%E4%B8%BB%E7%95%8C%E9%9D%A2.png)

其中面板的中按钮依次为拍照，录像，实时上传，打开回放，内部可以远程和本地回放文件，设置按钮

设置界面的图大致如下：

![](https://github.com/Car-eye-team/Car-eye-device/blob/master/picture/car-eye-camera%E8%AE%BE%E7%BD%AE%E7%95%8C%E9%9D%A2.jpg)

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

## 库接口说明

接口原型： public native int  CarEyeInitNetWork(Context context,String serverIP, String serverPort, String streamName, int videoformat, int fps,int audioformat, int audiochannel, int audiosamplerate);    
接口功能：初始化流媒体通道  
参数说明：   
context：应用句柄   
server IP:流媒体服务器的IP，可以是域名如www.car-eye.cn  
serverPort:RTSP流媒体的端口号     
streamName： 设备名：如手机号码13510671870 是设备的唯一标识    
videoformat： 视频格式，支持H264，265 MJPEG    
fps： 帧频率  
audioformat： 音频格式支持AAC,G711,G726等    
返回：通道号

接口原型：public native int 	 CarEyePusherIsReady(int channel);     
接口功能：判断通道是否准备好，用来开启推送1：已经准备好，0还没准备好。   
参数说明：   
channel：通道号
返回：1 通道已经准备好 0 通道还没准备好

接口原型： public native long   CarEyeSendBuffer(long time, byte[] data, int lenth, int type, int channel);   
接口功能：填充流媒体数据到RTSP服务器 
参数说明：   
time: 推送时间数，毫秒单位
data:  多媒体数据   
lenth：数据长度    
type ：视频还是音频   
channel：推送的通道号  
返回：0 为发送数据成功  其他 为错误码


接口原型 public native int    CarEyeStopNativeFileRTSP(int channel);   
接口功能：结束文件的推送   
参数说明:   
channel:通道号  

接口原型： public native int   CarEyeStartNativeFileRTSPEX(Context context, String serverIP, String serverPort, String streamName,  String fileName,int start, int end);          

接口功能：启动文件的推送 
参数说明:context：应用句柄  
serverIP:流媒体服务器的IP，可以是域名如www.car-eye.cn     
serverPort:RTSP流媒体的端口号      
streamName： 设备名：如手机号码13510671870 是设备的唯一标识  
fileName：文件的绝对路径      
start：推送的文件相对偏移的开始时间     
end：  推送文件的相对偏移的结束时间     
返回：通道号（1-8） 其他为错误  

接口原型   public void  CarEyeCallBack(int channel, int Result)   
接口功能：推送文件的callback函数      
参数说明:    
channel：通道号     
Result:返回码，为结束或者错误码      

# 注意事项

如果在手机上使用，请开启悬浮窗权限


# 联系我们   

car-eye 开源官方网址：www.car-eye.cn     

car-eye 流媒体平台网址：www.liveoss.com     

car-eye 技术官方邮箱: support@car-eye.cn   

car-eye技术交流QQ群: 590411159      

![](https://github.com/Car-eye-team/Car-eye-server/blob/master/car-server/doc/QQ.jpg)    


CopyRight©  car-eye 开源团队 2018 


