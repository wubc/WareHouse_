package com.xunfang.warehousesecurity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import edu.jju.adapter.MyAdapter;
import edu.jju.model.db.MyDBOpenHelper;
import edu.jju.model.db.MyDataBase;
import jju.edu.warehouse_.R;

/**
 * Created by Administrator on 2016/12/15.
 */

public class LocationActivity  extends Activity{


    private ImageView img_back;
    private LinearLayout linearLayout;
    private RecyclerView rv;
    private MyAdapter mAdapter;
    private TextView tv_add;
    private LinearLayoutManager mLayoutManager;
    private List<String> dataset =  new ArrayList<>();
    public static SharedPreferences sp;
    private CheckBox temp_cb;
    private CheckBox cur_cb;
    private EditText et;
    private MyDBOpenHelper helper;
    private SQLiteDatabase db;
    public static boolean mBoolean = false;
    private int index =-1;
    public static String name;
    public static String del_name;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("info1",":"+index);
        setContentView(R.layout.loclist_layout);
        img_back
                = (ImageView) findViewById(R.id.img_back);
        linearLayout
                = (LinearLayout) findViewById(R.id.linearLayout);
        rv
                = (RecyclerView) findViewById(R.id.rv);
        tv_add
                = (TextView) findViewById(R.id.tv_add);

        img_back.setOnClickListener(listener);
        tv_add.setOnClickListener(listener);

        helper = new MyDBOpenHelper(LocationActivity.this, "user.db", null, 1);
        db = helper.getReadableDatabase();
        if(MyDataBase.select(db)!=null){
            dataset = MyDataBase.select(db);
            showRecyclerView( dataset);

        }

    }

    public void showRecyclerView(final List<String> dataset){
        //设置固定大小
        rv.setHasFixedSize(true);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(LocationActivity.this);
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //设置布局管理器
        rv.setLayoutManager(mLayoutManager);
        // Log.i("info1",""+dataset.size());sp.getString("name","")

        //创建适配器，并且设置
        mAdapter = new MyAdapter(LocationActivity.this, dataset);
        rv.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MyAdapter.MyItenClickListener() {
            @Override
            public void onItemClick(View v, final int postion) {
                //Log.i("test",postion+"PP");
               // Log.i("id",v.getId()+"^^id");
             //   Log.i("conn",R.id.default_cb+"id#");
                switch (v.getId()){
                    case R.id.default_cb:
                       // Log.i("test",postion+"PP");
                        //Log.i("check","v:"+v.getId());
                        CheckBox cb = (CheckBox) v;
               //         Log.i("check","temp："+cb.isChecked());
                        if (temp_cb==null){
                            //首次点击checkbox，要取消掉上次退出时勾选框状态
                //            Log.i("check","temp0："+cb.isChecked());
                            if (MyAdapter.cb!=null) {
                                MyAdapter.cb.setChecked(false);
                            }
                            temp_cb =cb;
                            cur_cb = cb;
                        }else {

                            //从第二次点击开始
                            cur_cb = cb;//保存当前点击的checkbox
                            if (cur_cb!=temp_cb){
                                //当点击与上次点击的checkbox不同时
                //                Log.i("check","boolean1:"+temp_cb.isChecked());
                                temp_cb.setChecked(false);//将上一次点击的checkbox取消
                                temp_cb = cur_cb;//保存当前checkbox状态，用于下一次点击
                               // Log.i("check","temp1："+cb);
                //                Log.i("check","boolean1:"+temp_cb.isChecked());
                            }
                        }
                        index =postion;
                        name = dataset.get(postion);//退出前或删除前，默认地址名
                        mBoolean = cur_cb.isChecked();//退出前或删除前，勾选框状态

                        break;
                    case R.id.del_img:

                        del_name = dataset.get(postion);//获取删除地址名
                        MyDataBase.delete(db,del_name);//根据地址名从数据库删除对应字段
                        dataset.remove(postion);//数据集合删除对应数据

                        if (!dataset.isEmpty()){
                            //补充：直接调用该方法刷新界面，会清空上次勾选状态，需要在MyAdapterz中记录状态
                            showRecyclerView(dataset);
                        }


                        break;
                    default:
                        break;
                }


            }
        });
    }


        @Override
    protected void onPause() {
        super.onPause();
        sp = getSharedPreferences("address",MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (index != -1) {
            editor.putString("name", dataset.get(index));
            editor.putString("delname",del_name);
            editor.putInt("id", index);
            editor.putBoolean("status", cur_cb.isChecked());
            //Log.i("check","temp--："+cb.isChecked());
            editor.commit();
            Log.i("test", "get:" + sp.getString("name", ""));
        }
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.img_back:
                    finish();
                   // startActivity(new Intent(LocationActivity.this,MainActivity.class));
                    break;
                case R.id.tv_add:
                    LayoutInflater li_loc = LayoutInflater.from(LocationActivity.this);
                //加载xml布局对象
                final View setView_loc = li_loc.inflate(R.layout.setloc_layout,null);
                AlertDialog.Builder alertDialogBuilder_loc = new AlertDialog.Builder(LocationActivity.this);
                alertDialogBuilder_loc.setView(setView_loc)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                et = (EditText) setView_loc.findViewById(R.id.et_setloc);
                                if(TextUtils.isEmpty(et.getText().toString())){

                                }else {
                                    MyDataBase.inset(db,et.getText().toString(),null,null);
                                    Log.i("conn","插入成功");
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            super.run();
                                            Message msg = handler.obtainMessage();
                                            msg.obj = et.getText().toString();
                                            msg.what = 1;
                                            handler.sendMessage(msg);

                                        }
                                    }.start();

                                }
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
                    break;
            }
        }
    };

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==1){
                String name = (String) msg.obj;
                dataset.add(name);
                showRecyclerView( dataset);
            }
        }
    };

}
