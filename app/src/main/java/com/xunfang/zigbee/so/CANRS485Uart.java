package com.xunfang.zigbee.so;

/**
 * Created by Administrator on 2016/12/7.
 */

public class CANRS485Uart {

    public CANRS485Uart(){
        try {
            System.loadLibrary("CANRS485");
        } catch (UnsatisfiedLinkError ule) {
        }
    }

   private int fd;
    /**
     * 打开串口
     *
     * @param i
     *            串口号
     * @return 成功：fd>0;失败：-1
     *
     * nodelay  1为不阻塞  0为阻塞
     */
    public  static native int openUart(int i,int nodelay);

    public void open(int i,int nodelay){
        fd = openUart(i,nodelay);
    }



    //关闭串口
    public static native void closeUart(int fd,int i);
    public void close(int i){
        closeUart(fd,i);
    }

    //设置串口波特率
    public static native int setUart(int fd,int i);
    public void setUart(int i){
        setUart(fd,i);
    }

    //发送信息
    public static native int sendMsgUart(int fd,String msg);

    //接收消息
    public static native String receiveMsgUart(int fd);


    /**
     *设置奇偶校验
     * @param databits
     *            数据长度
     * @param stopbits
     *            停止位
     * @param parity
     *            校验类型（(int)n/N 无校验；(int)o/O 奇校验;(int)e/E 偶校验）
     * @return
     */
    public static native int setParity(int fd, int databits, int stopbits, int parity);
    public int set_Parity(int databits, int stopbits, int parity){
        return setParity(fd,databits,stopbits,parity);
    }

    //发送字节组
    public static native  int sendByteUart(int fd,byte[] msg);
    public int  send_ByteUart(byte[] msg){
       return sendByteUart(fd,msg);
    }

    //接受字节组数据
    public static native byte[] receiveByteUart(int fd);
    public byte[] recieve_ByteUart(){
        return receiveByteUart(fd);
    }

    //设置阻塞时间
    public static native int setTimeout(int fd,int timeout);
    public void set_Timeout(int timeout){
        setTimeout(fd,timeout);
    }


    public int getFd(){
        return  fd;
    }





}
