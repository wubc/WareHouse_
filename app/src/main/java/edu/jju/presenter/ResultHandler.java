package edu.jju.presenter;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xunfang.zigbee.so.CANRS485Uart;

import edu.jju.bean.RecieveData;
import edu.jju.utils.XOR;
import edu.jju.view.OperationView;

import com.xunfang.warehousesecurity.MainActivity;

/**
 * Created by Administrator on 2016/12/8.
 */

public class ResultHandler extends Handler {

    private OperationView operationView;
    private int alarmValue;
    private CANRS485Uart zigBee;

    public ResultHandler(OperationView operationView, CANRS485Uart zigBee) {

        this.operationView = operationView;
        this.zigBee = zigBee;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case 1://浓度上线
                operationView.gasIconLight();
                MainActivity.MAC_GAS = (byte[]) msg.obj;
                Log.i("info1", "1--浓度上线");
                Log.i("info1", "mac--"+MainActivity.MAC_GAS);
                break;
            case 2://热释电红外上线
                operationView.infraredIconLight();
                MainActivity.MAC_INFRARED = (byte[]) msg.obj;
                Log.i("haha", "红外上线");
                break;
            case 3://浓度节点掉线
                operationView.gasOff();
                MainActivity.MAC_GAS = null;
                Log.i("info1", "3--浓度掉线");
                break;
            case 4://热释电红外掉线
                operationView.infraredOff();
                MainActivity.MAC_INFRARED = null;
                Log.e("haha", "红外掉线");
                break;
            case 5://设置煤气浓度值
                Log.e("haha", "浓度值");
                RecieveData gasData = (RecieveData) msg.obj;
                operationView.showGasCurrent(gasData);
                int gasValue = gasData.getData()[0];
                //下发鸣叫命令
                alarmValue = Integer.parseInt(MainActivity.tv_gas.getText().toString());
                if (gasValue >= alarmValue) {
                    byte[] sendMsg = XOR.sendMsg((byte) 0, MainActivity.MAC_GAS, (byte) 13);
                    zigBee.send_ByteUart(sendMsg);
                }

                break;
            case 6://是否有人
                RecieveData infraredData = (RecieveData) msg.obj;
                Log.e("有人", infraredData.getOrder() + "");
                operationView.showIsPeople(infraredData);
                // 下发鸣叫命令
                if (infraredData.getData()[0] == 1) {
                    byte[] sendMsg = XOR.sendMsg((byte) 0, MainActivity.MAC_INFRARED, (byte) 13);
                    zigBee.send_ByteUart(sendMsg);
                }
                break;

            default:
                break;
        }
    }

    //获取警戒值

}
