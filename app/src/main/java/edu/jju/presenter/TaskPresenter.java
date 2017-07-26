package edu.jju.presenter;

import com.xunfang.zigbee.so.CANRS485Uart;

/**
 * Created by Administrator on 2016/12/7.
 */

public interface TaskPresenter {

    CANRS485Uart linkZigBee();
    void doLightGasIcon();
    void doLightInfraredIcon();
    void doShowLocation();
    int getValue(int alarmValue);
    void doShowGasCurrent();
    void doShowIsPeople();
    void doShowSurfaceView();

}
