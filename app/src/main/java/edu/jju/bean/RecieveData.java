package edu.jju.bean;

/**
 * Created by Administrator on 2016/12/7.
 */

public class RecieveData {
    //传感器类型
    private byte sensorType;
    //命令码
    private byte order;
    //传感器编号
    private byte[] sensorID;
    //数据长度
    private byte dataLength;
    //接受的数据
    private byte[] data;
    //设备类型
    private byte equipmentType;
    //接收到的信息
    private byte[] recieveMsg;

    public byte getSensorType() {
        return sensorType;
    }

    public void setSensorType(byte sensorType) {
        this.sensorType = sensorType;
    }

    public byte getOrder() {
        return order;
    }

    public void setOrder(byte order) {
        this.order = order;
    }

    public byte[] getSensorID() {
        return sensorID;
    }

    public void setSensorID(byte[] sensorID) {
        this.sensorID = sensorID;
    }

    public byte getDataLength() {
        return dataLength;
    }

    public void setDataLength(byte dataLength) {
        this.dataLength = dataLength;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte getEquipmentType() {
        return equipmentType;
    }

    public void setEquipmentType(byte equipmentType) {
        this.equipmentType = equipmentType;
    }

    public byte[] getRecieveMsg() {
        return recieveMsg;
    }

    public void setRecieveMsg(byte[] recieveMsg) {
        this.recieveMsg = recieveMsg;
    }

    public String toString(){
        return  "sensorType:"+sensorType+"/order:"+order+"/sensorID:"+sensorID
                +"/dataLength:"+dataLength+"/data:"+data
                +"/equipmentType:"+equipmentType+
                "/recieveMsg:"+recieveMsg;
    }
}
