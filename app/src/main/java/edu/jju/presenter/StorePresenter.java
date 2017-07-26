package edu.jju.presenter;

import android.util.Log;

import com.xunfang.zigbee.so.CANRS485Uart;

/**
 * Created by Administrator on 2016/12/7.
 */

public class StorePresenter implements TaskPresenter{
    private CANRS485Uart zigBee;
    @Override
    public  CANRS485Uart linkZigBee() {
        zigBee = new CANRS485Uart();
        zigBee.open(4,0);
        zigBee.setUart(115200);
        zigBee.set_Parity(8,1,'N');
        zigBee.set_Timeout(5);
        System.out.println("haha"+zigBee.recieve_ByteUart());
        return zigBee;
    }

    @Override
    public void doLightGasIcon() {

    }

    @Override
    public void doLightInfraredIcon() {

    }

    @Override
    public void doShowLocation() {

    }

    @Override
    public int  getValue(int alarmValue) {
        return alarmValue;
    }

    @Override
    public void doShowGasCurrent() {

    }

    @Override
    public void doShowIsPeople() {

    }

    @Override
    public void doShowSurfaceView() {

    }
}
