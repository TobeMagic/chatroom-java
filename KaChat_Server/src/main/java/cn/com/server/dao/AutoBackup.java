package cn.com.server.dao;

import cn.com.server.db.DB;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

/*
 descriptions: auto backup the user data
  */
public class AutoBackup {
    private File file;
    private Timer timer;
    private int delay;
    private DB db;
    private DB database;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    AutoBackup(int delay,File file,DB database)  {
        //DAO的db传进来
        this.database = database;
        this.file = file;
        this.delay = delay;
        init();
    }

    private void init() {
        this.timer = new Timer(delay, new backupData());;
        try {
            //确认有没有文件
            if (!file.isFile()){
                file.createNewFile();

            }else {
                //如果有备份，读取文件对象并赋给DAO的DB
//                ois = new ObjectInputStream(new FileInputStream(file));
//                db = (DB) ois.readObject();
//                database.setUserData(db.getUserData()) ;
//                database.setOfflineMsg(db.getOfflineMsg());
//                database.setUserOut(db.getUserOut());
//                pass;
                System.out.println("TEST");
            }
            oos = new ObjectOutputStream(new FileOutputStream(file));
        }catch (IOException  e) { //| ClassNotFoundException
            e.printStackTrace();
        }finally {
            if (ois!=null){
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    //初始化完成后运行
    public void start(){
        timer.start();
    }

    private class backupData implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                //每隔一段时间向文件写入数据
                System.out.println(database.getUserData());
                oos.writeObject(Dao.getDatabase());
                oos.flush();
                oos.reset();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    protected void finalize(){
        try {
            if (ois!=null){
                ois.close();
            }
            if (oos!=null){
                oos.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
}
