package edu.jju.presenter;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Arrays;

import com.xunfang.zigbee.so.CANRS485Uart;

import edu.jju.bean.RecieveData;
import edu.jju.utils.SensorType;
import edu.jju.utils.XOR;

import com.xunfang.warehousesecurity.MainActivity;

/**
 * Created by Administrator on 2016/12/8.
 */

public class ZigBeeHandler extends Handler {
    private RecieveData data;
    private Handler handler = new Handler();
    private CANRS485Uart zigBee;

    public ZigBeeHandler(CANRS485Uart zigBee, Handler handler) {
        this.zigBee = zigBee;
        this.handler = handler;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.obj != null) {
            switch (msg.what) {
                case 5:
                    Log.e("###", "节点上线");
                    data = (RecieveData) msg.obj;
                    //节点上线,判断是哪个节点上线
                    if (data.getSensorType() == SensorType.ZIGBEE_GAS) {
                        Log.i("info1", "浓度上线");
                        Message message = handler.obtainMessage();
                        message.what = 1;//浓度上线
                        message.obj = data.getSensorID();
                        handler.sendMessage(message);
                        byte[] sendMsg = XOR.sendMsg((byte) 0, data.getSensorID(), (byte) 16);
                        zigBee.send_ByteUart(sendMsg);
                    } else if (data.getSensorType() == SensorType.ZIGBEE_INFRARED) {
                        Log.e("###", "红外上线");
                        Message message = handler.obtainMessage();
                        message.what = 2;//热释电红外上线
                        message.obj = data.getSensorID();
                        handler.sendMessage(message);
                        byte[] sendMsg = XOR.sendMsg((byte) 0, data.getSensorID(), (byte) 16);
                        zigBee.send_ByteUart(sendMsg);
                    }
                    break;
                case 6:
                    //节点下线，通过掉线节点的MAC地址判断哪个节点掉线
                    Log.e("info1", "6");
                    Log.e("info1", "mg--"+MainActivity.MAC_GAS);
                    data = (RecieveData) msg.obj;
                    Log.e("info1", "mac--"+data.getSensorID());
                    if (Arrays.equals(data.getData(), MainActivity.MAC_GAS)) {//浓度掉线
                        Message delete = handler.obtainMessage();
                        delete.what = 3;
                        Log.e("info1","浓度节点掉线");
                        byte[] sendMsg = XOR.sendMsg((byte) 0, MainActivity.MAC_GAS, (byte) 0x7);
                        zigBee.send_ByteUart(sendMsg);
                        handler.sendMessage(delete);
                    } else if (Arrays.equals(data.getData(), MainActivity.MAC_INFRARED)) {//热释电红外掉线
                        data = (RecieveData) msg.obj;
                        Message delete = handler.obtainMessage();
                        delete.what = 4;
                        byte[] sendMsg = XOR.sendMsg((byte) 0, MainActivity.MAC_INFRARED, (byte) 0x7);
                        zigBee.send_ByteUart(sendMsg);
                        handler.sendMessage(delete);
                    }
                    break;
                case 8:
                    data = (RecieveData) msg.obj;
                    Log.e("haha", "zigbeeHandler");
                    Log.e("haha", "" + MainActivity.MAC_GAS);
                    if (data.getSensorType() == SensorType.ZIGBEE_GAS && data.getDataLength() != 0 && Arrays.equals(data.getSensorID(), MainActivity.MAC_GAS)) {
                        Log.e("haha", "煤气浓度");
                        Message message = handler.obtainMessage();
                        message.what = 5;//煤气浓度
                        message.obj = data;
                        handler.sendMessage(message);
                    } else if (data.getSensorType() == SensorType.ZIGBEE_INFRARED && data.getDataLength() != 0 && Arrays.equals(data.getSensorID(), MainActivity.MAC_INFRARED)) {
                        Log.e("haha", "红外线");
                        Message message = handler.obtainMessage();
                        message.what = 6;//热红释外
                        message.obj = data;
                        handler.sendMessage(message);
                    }

                    break;

                default:
                    break;
            }
        }
    }
}
