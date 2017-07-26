package edu.jju.utils;

import edu.jju.bean.RecieveData;

/**
 * Created by Administrator on 2016/12/8.
 */

public class ParseRecieveSensorMessage {

    public static RecieveData parseZigBee(byte[] msg){
        RecieveData data = new RecieveData();

        int length = msg.length;
        if(msg[msg.length-1]==-86 && msg[msg.length-2]==-2){//判断结束码
            //当上传的信息包含信息头，并且信息长度包含一条完整的信息
            for(int i=0;i<length && length-11>0;i++){
                //检测到开始码
                if(msg[i++]==-1 && msg[i++]==85){
                    data.setRecieveMsg(msg);

                    data.setEquipmentType(msg[i++]);//设备类型
                    data.setSensorType(msg[i++]);//传感器类型
                    //设备地址编号
                    byte[] sensorid = { msg[i++],  msg[i++],msg[i++],  msg[i++],msg[i++],  msg[i++],msg[i++],  msg[i++]};
                    data.setSensorID(sensorid);
                    data.setOrder(msg[i++]);//命令码
                    data.setDataLength(msg[i++]);//数据长度
                    byte[] message = new byte[data.getDataLength()];
                    for(int j=0;j<data.getDataLength();j++){
                        message[j] =msg[i++];//数据
                    }
                    data.setData(message);
                    i+=3;//最后校验码、结束码(2位）
                }
            }
        }
        return  data;
    }

}
