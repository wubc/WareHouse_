package com.xunfang.warehousesecurity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

import com.githang.statusbar.StatusBarCompat;
import com.xunfang.zigbee.so.CANRS485Uart;
import edu.jju.bean.RecieveData;
import edu.jju.model.db.MyDBOpenHelper;
import edu.jju.model.db.MyDataBase;
import edu.jju.presenter.ResultHandler;
import edu.jju.presenter.ZigBeeHandler;
import edu.jju.presenter.ZigBeeThreads;
import edu.jju.view.OperationView;
import jju.edu.warehouse_.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OperationView{

    private SurfaceView sv;
    private ImageView iv_gas;
    private ImageView iv_infrared;
    private TextView tv_store_location;
    private TextView tv_location_set;
    private TextView tv_gas_current;
    public static TextView tv_gas;
    private Button btn_gas_set;
    private TextView tv_status;

    private final static Byte HEAD1 = (byte) 0xFF;
    private final static Byte HEAD2 = (byte) 0x55;

    private Socket socket = null;
    private SurfaceHolder holder = null;
    private ProgressDialog pDialog = null;
    private String dstAddress;
    private int port;
    //private long starttime = 0;
    public static byte[] MAC_GAS = null;
    public static byte[] MAC_INFRARED = null;

    private CANRS485Uart zigBee;
    private boolean flag;
    private ZigBeeHandler mZigBeeHandler;
    private ZigBeeThreads mThreads;
    private ResultHandler mResultHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 因为ZigBee节点刚打开时发送的是上线命令，当有上线应答之后只发送数据。
        // 但是当APP页面关闭或是跳转到其他应用时不能发送命令关闭ZigBee节点，
        // 所以当重新打开该APP时如果没有重新打开ZigBee节点，ZigBee只会发送数据
        // 而不会发送上线命令,就无法记录ZigBee上线状态。所以需要在退出时记录节点的连接状态。
        // onSaveInstanceState(Bundle savedInstanceState) 该方法在退出时自动保存数据

        // 通过节点的MAC地址是否为空判断是否在线。

        // 读取 savedInstanceState中的数据
        if (null != savedInstanceState) {
            MAC_GAS = savedInstanceState.getByteArray("con");
            MAC_INFRARED = savedInstanceState.getByteArray("inf");
        }
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        //设置状态栏颜色
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#FF00BAD2"), true);
        sv
                =(SurfaceView)findViewById(R.id.surfaceView);
        initSurfaceHolder();
        zigBee = new CANRS485Uart();
        zigBee.open(4,0);
        zigBee.setUart(115200);
        zigBee.set_Parity(8,1,'N');
        zigBee.set_Timeout(5);
         Log.e("haha","");
        //zigBee.setFd(22);
        //zigBee = presenter.linkZigBee();
        flag =true;
        mResultHandler = new ResultHandler(this,zigBee);
        mZigBeeHandler = new ZigBeeHandler(zigBee,mResultHandler);
        mThreads = new ZigBeeThreads(mZigBeeHandler,flag,zigBee);
        mThreads.start();
        init();

    }

    private void initSurfaceHolder() {
        holder = sv.getHolder();
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.e("surfaceCreated ", holder.toString());

                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                final View ip = factory.inflate(R.layout.server_ip_port, null);
                final EditText server2IP = (EditText) ip
                        .findViewById(R.id.server_ip);
                final EditText server2Port = (EditText) ip
                        .findViewById(R.id.server_port);

                server2IP.setText("192.168.1.120");
                server2Port.setText("8888");

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("设置IP地址")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(ip)
                        .setPositiveButton("连接",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        if (pDialog == null) {
                                            pDialog = new ProgressDialog(
                                                    MainActivity.this);
                                            dstAddress = server2IP.getText()
                                                    .toString();
                                            String str="\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\" +
                                                    ".((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";//ip正则表达式
                                            if (dstAddress.matches(str)) {
                                                port = Integer
                                                        .parseInt(server2Port
                                                                .getText()
                                                                .toString());
                                                pDialog.setMessage("正在连接到"
                                                        + dstAddress + "摄像头...");
                                                pDialog.setCancelable(true);
                                                pDialog.show();
                                            }else{
                                                Toast.makeText(getApplicationContext(), "ip地址不正确", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        thread.start();
                                    }
                                }).setNegativeButton("取消", null).show();

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format,
                                       int width, int height) {
            }
        });

    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {

            InputStream is = null;
            try {
                socket = new Socket(dstAddress, port);
                Log.e("info", "socket:" + socket);
                is = socket.getInputStream();
                while (true) {// 获取一帧数据
                    byte[] head = new byte[4];
                    is.read(head);
                    if (head[0] == HEAD1 && head[1] == HEAD2) {

                        Bitmap bitmapTemp = BitmapFactory.decodeStream(is);
                        if (bitmapTemp != null) {
                            Message msg = handler.obtainMessage();
                            msg.what = 10;
                            msg.obj = bitmapTemp;
                            handler.sendMessage(msg);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }
    });
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 7 :// 返回退出
                    try {
                        if (socket != null)
                            socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 10 :// wifi节点传输的数据
                    if (pDialog != null) {
                        pDialog.dismiss();
                    }
                    Bitmap bitmapTemp = (Bitmap) msg.obj;
                    Canvas canvas = holder.lockCanvas();
                    if (canvas != null && bitmapTemp != null) {
                        canvas.drawColor(Color.BLACK);
                        Matrix matrix = new Matrix();
                        matrix.setScale(1f, 1f);
                        int bmpW = bitmapTemp.getWidth();
                        int bmpH = bitmapTemp.getHeight();

                        Bitmap resizeBmp = Bitmap.createBitmap(bitmapTemp, 0, 0,
                                bmpW, bmpH, matrix, true);
                        canvas.drawBitmap(resizeBmp, 0, 0, new Paint());
                        holder.unlockCanvasAndPost(canvas);
                    }
                break;
                default :
                break;
                }

        }
    };
    //初始化
    public void init(){

        iv_gas
                = (ImageView) findViewById(R.id.iv_gas);
        iv_infrared
                = (ImageView) findViewById(R.id.iv_infrared);
        tv_store_location
                = (TextView) findViewById(R.id.tv_store_location);
        tv_location_set
                = (TextView) findViewById(R.id.tv_location_set);
        tv_gas_current
                = (TextView) findViewById(R.id.tv_gas_current);
        tv_gas
                = (TextView) findViewById(R.id.tv_gas);
        btn_gas_set
                = (Button) findViewById(R.id.btn_gas_set);
        tv_status
                = (TextView) findViewById(R.id.tv_status);

        //设置按钮监听
        tv_location_set.setOnClickListener(this);

        btn_gas_set.setOnClickListener(this);
    }
    //从LocationActivity返回到MainActivity,MainActivity不执行 onCreate()
    //pDialog != null

    @Override
    protected void onRestart() {
        super.onRestart();
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = getSharedPreferences("address",MODE_PRIVATE);
        String name = sp.getString("name","");
        //String delname = sp.getString("delname","");
        int position = sp.getInt("id",-1);
        boolean b = sp.getBoolean("status",false);
        if (!TextUtils.isEmpty(name) && !name.equals(LocationActivity.del_name) && b==true) {
            tv_store_location.setText(name);
        }else {
            tv_store_location.setText("沿山路");
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_gas_set:
                LayoutInflater li = LayoutInflater.from(this);
                //加载xml布局对象
                final View setView = li.inflate(R.layout.setgas_layout, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setView(setView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditText et = (EditText) setView.findViewById(R.id.et_setcon);
                                if (TextUtils.isEmpty(et.getText().toString())) {

                                } else {
                                    tv_gas.setText(Integer.parseInt(et.getText().toString()) + "");//防止以0开头的数
                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                break;
            case R.id.tv_location_set:
//                LayoutInflater li_loc = LayoutInflater.from(this);
//                //加载xml布局对象
//                final View setView_loc = li_loc.inflate(R.layout.setloc_layout,null);
//                AlertDialog.Builder alertDialogBuilder_loc = new AlertDialog.Builder(this);
//                alertDialogBuilder_loc.setView(setView_loc)
//                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                EditText et = (EditText) setView_loc.findViewById(R.id.et_setloc);
//                                if(TextUtils.isEmpty(et.getText().toString())){
//
//                                }else {
//                                    tv_store_location.setText(et.getText().toString());
//                                }
//                            }
//                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                }).show();
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                //intent.putExtra("state",LocationActivity.sp.getBoolean("status",true));
                startActivity(intent);
                break;
            default :
            break;
            }
    }

    @Override
    public void gasIconLight() {
            iv_gas.setImageResource(R.mipmap.btn_on);
    }

    @Override
    public void gasOff() {
            iv_gas.setImageResource(R.mipmap.btn_off);
            tv_gas_current.setText("0");
    }

    @Override
    public void infraredIconLight() {
        iv_infrared.setImageResource(R.mipmap.btn_on);
    }

    @Override
    public void infraredOff() {
        iv_infrared.setImageResource(R.mipmap.btn_off);
        tv_status.setText("没人");
    }

    @Override
    public void showLocation() {

    }

    @Override
    public void showGasWarning() {

    }

    @Override
    public void showGasCurrent(RecieveData data) {
        tv_gas_current.setText(data.getData()[0]+"");
        int tem = data.getData()[0];
        if(tem >= Integer.parseInt(tv_gas.getText().toString())){
            tv_gas_current.setTextColor(Color.RED);
            if(MAC_GAS == null){
                Toast.makeText(getApplicationContext(), "请重新打开气体浓度节点",
                        Toast.LENGTH_SHORT).show();
            }
        }else {
            tv_gas_current.setTextColor(Color.parseColor("#FF00BAD2"));
        }
    }

    @Override
    public void showIsPeople(RecieveData data) {
        if (data.getData()[0]==1){
            tv_status.setText("有人");
            tv_status.setTextColor(Color.RED);
            if (MAC_INFRARED == null) {
                Toast.makeText(getApplicationContext(), "请重新打开热释电红外节点",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (data.getData()[0] == 0) {
            tv_status.setText("无人");
            tv_status.setTextColor(Color.parseColor("#FF00BAD2"));
        }

    }

    @Override
    public void showSurfaceView() {

    }

    @Override
    protected void onDestroy() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
        mThreads.setFlag(false);
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            handler.sendEmptyMessage(7);
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("提示");
            dialog.setMessage("确定退出么？");
            dialog.setNegativeButton("再看看", null);
            dialog.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dialog.show();
        }

        return false;

    }
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putByteArray("con", MAC_GAS);
        savedInstanceState.putByteArray("inf", MAC_INFRARED);
        super.onSaveInstanceState(savedInstanceState);

    }



    //双击退出
//    private long starttime = 0;
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        long currentTime = System.currentTimeMillis();
//        if ((currentTime - starttime)>=2000){
//            Toast.makeText(MainActivity.this,"再按一次退出",Toast.LENGTH_SHORT).show();
//            starttime = currentTime;
//        }else {
//            finish();
//        }
//    }
}
