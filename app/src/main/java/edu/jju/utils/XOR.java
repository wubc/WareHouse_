package edu.jju.utils;

import android.util.Log;

import edu.jju.bean.RecieveData;

/**
 * Created by Administrator on 2016/12/8.
 */

public class XOR {
    /*
    * 网关下发控制指令通用格式
    * 参数：传感器类型、设备地址编号、命令码
    *
    * */
    public static byte[] sendMsg(byte sensorType,byte[] address,byte command ){
        byte[] order = new byte[17];
        order[0] = -1;//起始码
        order[1] = 85;//起始码
        order[2] = 0;//设备类型
        order[3] = sensorType;//传感器类型

        System.arraycopy(address,0,order,4,8);//设备地址编号(8字节)
        order[12] = command;//命令码
        order[13] = 0;
        order[14] = XOR(order,14);
        order[15] = -2;
        order[16] = -86;

        Log.i("conn","又要开始测试了！");
        for (byte d:order){
            Log.e("",""+d+"");
        }
        return order;
    }

    //异或校验
    public static byte XOR(byte[] order,int length){
        byte xorValue = 0;
        for(int i = 0;i<length;i++){
            xorValue^=order[i];
        }
        return xorValue;
    }


}
