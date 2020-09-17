# Car-eye-device
 
car-eye-device 是车辆管理系统的设备端程序，负责视频采集，gps采集等，实现了传统的DVR功能。还支持远程控制，支持远程观看实时视频和检索录像。

## Car-eye-device 工作原理
设备端通过808协议跟Car-eye-server或者Car-eye-CMS进行通信，上传位置信息和视频控制信息，整个框架如下图    


![](https://gitee.com/careye_open_source_platform_group/Car-eye-device/raw/master/picture/car-eye-device-machine.png)




## 多路行车记录仪功能介绍

主要实现功能有：

1. 多通道视频录像
2. 多通道实时视频上传
3. 文件回放上传
4. GPS 数据上传
5. 实时双向对讲
6. 本地人脸别和对接平台识别
7. 录像本地回放

sh_camera 是car-eye开源团队开发的基于android系统的一个应用程序。主要界面如下图：

![](https://github.com/Car-eye-team/Car-eye-device/blob/master/picture/pusher.jpg)

其中面板的中按钮依次为拍照，录像，实时上传，打开回放，内部可以远程和本地回放文件，设置按钮，人脸识别



设置界面的图大致如下：

![](https://github.com/Car-eye-team/Car-eye-device/blob/master/picture/settings.png)

其中流媒体服务器IP是流媒体服务的IP地址
端口是服务器的端口
设备号是设备编码，如果使在car-server上注册为手机号码，则可以用car-eye-client远程控制设备开始推流。
平台服务IP 是车辆管理平台的IP地址，端口是通信服务的端口

推流的格式如下：

实时直播流：
rtmp://www.car-eye.cn:10077/live/13510671870_channel_1     
IP为：www.car-eye.cn 域名
端口为:10077
注册手机号码为13510671870?
通道0

其中流媒体服务器上的IP和设备端设置的IP，客户端的IP保持一致

回放流：
rtmp://www.car-eye.cn:10077/live/13510671870_channel_1 

可以通过第三方播放器，如VLC等进行播放。在car-eye注册平台注册账号，并在客户端控制播放。
JT1078 视频平台网址：http://www.liveoss.com:8088/   


## 库接口说明

接口原型： public native int CarEyeInitNetWorkRTP(Context context,String key,String serverIP, String serverPort, String streamName, int logchannel, int videoformat, int fps,int audioformat, int audiochannel, int audiosamplerate);     
接口功能：初始化流媒体通道     
参数说明：   
key: 授权码    
context：应用句柄   
server IP:流媒体服务器的IP，可以是域名如www.car-eye.cn  
serverPort:RTMP流媒体的端口号     
streamName： 设备名：如手机号码13510671870 是设备的唯一标识    
videoformat： 视频格式，支持H264，265 MJPEG    
fps： 帧频率  
audioformat： 音频格式支持AAC,G711,G726等    
返回：通道号

接口原型：public native int 	 CarEyePusherIsReadyRTP(int channel);     
接口功能：判断通道是否准备好，用来开启推送1：已经准备好，0还没准备好。   
参数说明：   
channel：通道号
返回：1 通道已经准备好 0 通道还没准备好

接口原型： public native long   CarEyeSendBufferRTP(long time, byte[] data, int lenth, int type, int channel)
接口功能：填充流媒体数据到JT1078服务器 
参数说明：   
time: 推送时间数，毫秒单位
data:  多媒体数据   
lenth：数据长度    
type ：视频还是音频   
channel：推送的通道号  
返回：0 为发送数据成功  其他 为错误码    

接口原型 public native int    CarEyeStopNativeFileRTP(int channel);   
接口功能：结束文件的推送   
参数说明:   
channel:通道号  

接口原型： public native int   CarEyeStartNativeFileRTPEX(Context context, String serverIP, String serverPort, String streamName,  String fileName,int start, int end);          

接口功能：启动文件的推送 
参数说明:context：应用句柄  
serverIP:流媒体服务器的IP，可以是域名如www.car-eye.cn     
serverPort:JT1078流媒体的端口号      
streamName： 设备名：如手机号码13510671870 是设备的唯一标识  
fileName：文件的绝对路径      
start：推送的文件相对偏移的开始时间     
end：  推送文件的相对偏移的结束时间     
返回：通道号（1-8） 其他为错误  

接口原型   public void  CarEyeCallBack(int channel, int Result)   
接口功能：推送文件的callback函数      
参数说明:    
channel：通道号     
Result:返回码，为结束或者错误码     

# 注意事项

Car-eye-CMS 目前支持RTSP/RTMP通信方式的设备管理

Car-eye JT1078流媒体平台 目前支持JT1078/RTMP 两种设备的管理

# 商业合作

Car-eye-device 商业用户需要鉴权，具体请联系团队管理人员


# 扫描二维码下载       
![](https://gitee.com/careye_open_source_platform_group/Car-eye-device/raw/master/picture/android%20DVR%20%E4%BA%8C%E7%BB%B4%E7%A0%81.png)


# 联系我们

car-eye 开源官方网址：www.car-eye.cn    
car-eye 车辆管理平台网址：www.liveoss.com  
car-eye GB28181管理平台网址 ：www.streaming-vip.com:10088     
car-eye 技术官方邮箱: support@car-eye.cn  
car-eye 车辆管理平台技术交流QQ群:590411159   
car-eye 视频服务和管理平台QQ群：713522732     
![](https://gitee.com/careye_open_source_platform_group/car-eye-jtt1078-media-server/raw/master/QQ/QQ.jpg)   
CopyRight©  car-eye 开源团队 2018-2020

