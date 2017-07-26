package edu.jju.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xunfang.warehousesecurity.LocationActivity;

import java.util.ArrayList;
import java.util.List;

import jju.edu.warehouse_.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2016/12/16.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<String> mDataset;
    public static Context context;
    private LayoutInflater  mInflater;
    private MyItenClickListener mListener;
    public static CheckBox cb;

    public MyAdapter(Context context,List<String> myDataset){
        this.mDataset = myDataset;
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public interface MyItenClickListener{
          void onItemClick(View v,int postion);
    }

    public static class MyViewHolder extends  RecyclerView.ViewHolder {
        public TextView tv;
        public CheckBox cb;
        public ImageView img;
        public MyViewHolder(View v){
            super(v);
            tv = (TextView) v.findViewById(R.id.item_tv);
            cb = (CheckBox) v.findViewById(R.id.default_cb);
            img = (ImageView) v.findViewById(R.id.del_img);

        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.recyc_item,parent,false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tv.setText(mDataset.get(position));

        //通过SharedPreferences，获取在LocationActivity退出时勾选框状态
        SharedPreferences sp = context.getSharedPreferences("address",MODE_PRIVATE);
        Log.i("conn","$$"+sp.getInt("id",-1));
        Log.i("conn","$$!!"+position);
        if (sp.getInt("id",-1)!=-1 && position == sp.getInt("id",-1) && mDataset.get(position).equals(sp.getString("name",""))){
            Log.i("conn","$$"+sp.getBoolean("status",true));
            holder.cb.setChecked(sp.getBoolean("status",true));
            cb = holder.cb;
        }

        //删除时，记录上次勾选状态
        //通过判断删除时，勾选的地址名是否为当前所在的position，找到当前控件并赋予勾选状态
        if (!TextUtils.isEmpty(LocationActivity.del_name) && mDataset.get(position).equals(LocationActivity.name)){
            holder.cb.setChecked(LocationActivity.mBoolean);
            Log.i("conn","$$!!"+position);
        }


        if (mListener!=null){

            holder.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(holder.cb,position);
                    //Log.i("conn",""+position);
                }
            });
            holder.img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(holder.img,position);
                    //Log.i("conn",""+position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    /**
     * 设置Item点击监听
     * @param listener
     */
    public void setOnItemClickListener(MyItenClickListener listener){
        this.mListener = listener;
    }

}
