package edu.jju.model.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.CheckBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/19.
 */

public class MyDataBase {
    private Context context;
    public MyDataBase(Context context){
        this.context = context;
    }

    //插入地址数据
    public static void inset(SQLiteDatabase db,String name,String ip,String port){
        String sql_add = "insert into address(name,ip,port) values(?,?,?)";
        db.execSQL(sql_add,new String[]{name,ip,port});
    }

    //查询表所有信息，并返回
    public static List<String> select(SQLiteDatabase db){
        String sql_sel = "select * from address";
        Cursor cursor = db.rawQuery(sql_sel,null);
        List<String> oList = new ArrayList<>();

        while (cursor.moveToNext()){
            int id_index = cursor.getColumnIndex(cursor.getColumnName(0));
            int id_count = cursor.getInt(id_index);
            int name_index = cursor.getColumnIndex(cursor.getColumnName(1));
            String name  = cursor.getString(name_index);
            int ip_index = cursor.getColumnIndex(cursor.getColumnName(2));
            String ip = cursor.getString(ip_index);
            int port_index = cursor.getColumnIndex(cursor.getColumnName(3));
            String port = cursor.getString(port_index);
            oList.add(name);
        }
        return oList;
    }

    //插入勾选状态数据
    public static void inset_(SQLiteDatabase db,CheckBox cb){
        String sql_add = "insert into isChecked(cbox) values(?)";
        db.execSQL(sql_add,new CheckBox[]{cb});
    }

    //根据id删除数据
    public static void delete(SQLiteDatabase db,String name){
        String sql_del = "delete from address where name=?";
        db.execSQL(sql_del,new String[]{name});
    }


}
