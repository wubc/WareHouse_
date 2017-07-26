package edu.jju.model;

import android.util.Log;

import com.xunfang.zigbee.so.CANRS485Uart;
import edu.jju.bean.RecieveData;
import edu.jju.utils.ParseRecieveSensorMessage;
import edu.jju.utils.SensorType;

/**
 * Created by Administrator on 2016/12/7.
 */

public class RecieveDataFactory {

    private byte[] recieveMsg;

    //获取从zigBee节点接收到的各位数据
    public RecieveData getRecieveData(CANRS485Uart zigBee){
            RecieveData data = new RecieveData();

            recieveMsg = zigBee.recieve_ByteUart(); //接受字节组数据
            Log.e("byte",""+zigBee.recieve_ByteUart());

            if (recieveMsg!=null){
                for (int i=0;i<recieveMsg.length;i++){
                    System.out.print("Msg"+recieveMsg[i]);
                    Log.e("fd:",recieveMsg[i]+"");
                }
                System.out.println("zigBee 输出:");
            }else {
                Log.e("zigBee 输出失败:","!!!");
            }

            //开始位、传感器类型
            if (recieveMsg!=null && recieveMsg[0]== -1 && recieveMsg[1] == 85 && recieveMsg[2] == SensorType.ZIGBEE_TYPE){

                //解析从zigBee节点接收到的数据
                data = ParseRecieveSensorMessage.parseZigBee(recieveMsg);
                Log.e("data",data.toString());
            }
            return  data;
        }

}
