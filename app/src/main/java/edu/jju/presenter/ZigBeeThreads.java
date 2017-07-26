package edu.jju.presenter;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xunfang.zigbee.so.CANRS485Uart;
import edu.jju.bean.RecieveData;
import edu.jju.model.RecieveDataFactory;

/**
 * Created by Administrator on 2016/12/8.
 */

public class ZigBeeThreads extends Thread {
    //更新界面的handler
    private Handler handler = new Handler();
    //handler发送的消息
    private Message msg;
    //完成zigbee连接标识符
    private boolean flag;
    //打开独立串口操作对象
    private CANRS485Uart zigBee;

    public ZigBeeThreads(Handler mHandler,boolean flag,CANRS485Uart zigBee){
        this.handler = mHandler;
        this.flag =flag;
        this.zigBee = zigBee;
    }
    //设置循环标识符
    public void setFlag(boolean flag){
        this.flag = flag;
    }

    public void run(){
        while (flag) {
            RecieveDataFactory dataFactory = new RecieveDataFactory();
            RecieveData data = dataFactory.getRecieveData(zigBee);
            Log.i("info",data.toString());
            if (data != null) {
                Log.e("data", ""+data.toString());
                msg = handler.obtainMessage();
                msg.what = 10;
                msg.obj = data.getDataLength();
                handler.sendMessage(msg);
                System.out.println("命令码：" + data.toString());
                /**具体命令码含义参照ZigBee协议具体数据格式
                 * 5-节点上线   6-上传节点掉线信息 8-节点上传传感器数据
                 */
                switch (data.getOrder()) {
                    //5-节点上线
                    case 5:
                        msg = handler.obtainMessage();
                        msg.what = 5;
                        Log.i("info1",msg.what+"");
                        msg.obj = data;
                        handler.sendMessage(msg);
                        break;
                    //6-上传节点掉线信息
                    case 6:
                        msg = handler.obtainMessage();
                        msg.what = 6;
                        msg.obj = data;
                        handler.sendMessage(msg);
                        break;
                    //8-节点上传传感器数据
                    case 8:
                        msg = handler.obtainMessage();
                        msg.what = 8;
                        msg.obj = data;
                        handler.sendMessage(msg);
                    default:
                        break;
                }
            }
        }
        }



}
