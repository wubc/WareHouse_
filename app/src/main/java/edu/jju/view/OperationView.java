package edu.jju.view;

import edu.jju.bean.RecieveData;

/**
 * Created by Administrator on 2016/12/7.
 */

public interface OperationView {

    //煤气节点上线,点亮指示灯
    void gasIconLight();
    //煤气浓度掉线，指示灯灭、浓度值为0
    void gasOff();
    //红热释外上线，点亮指示灯
    void infraredIconLight();
    //红热释外掉线，指示灯灭、没人
    void infraredOff();
    //仓库信息位置
    void showLocation();
    //煤气警戒浓度
    void showGasWarning();
    //煤气当前浓度
    void showGasCurrent(RecieveData data);
    //仓库内是否有人
    void showIsPeople(RecieveData data);
    //报警

    //监控图像
    void showSurfaceView();

}
