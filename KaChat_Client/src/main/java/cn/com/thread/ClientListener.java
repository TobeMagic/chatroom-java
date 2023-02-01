package cn.com.thread;


import cn.com.action.ClientAction;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Random;

public class ClientListener implements Runnable, Serializable {

    public Socket socket;
    //    static ClientListener self;
//    private HeartBeat heartBeat;
    //    private ObjectInputStream objectInputStream;
    private DataInputStream dataInputStream;
    //    private ObjectOutputStream objectOutputStream;
    private DataOutputStream dataOutputStream;
    private final String hostname;
    private final int port;
    private int portrait;
    private String name;
    private static final String[] names = new String[]{"John", "Sarah", "Michael", "Emily", "David", "Jessica"};

    //    private ChatView chatView;
    public boolean isConnected;

    public ClientListener(String hostname, int port) {
//    public ClientListener(String hostname, int port, ChatView chatView) {
        isConnected = false;
//        heartBeat = new HeartBeat(50000, this);  // 心跳 （连接确认）
        this.hostname = hostname;
        this.port = port;
        Random random = new Random();
        this.portrait = random.nextInt(names.length);
        this.name = names[random.nextInt(names.length)];
//        self = this;
//        this.chatView = chatView;
    }

    private void connect() throws IOException {
        while (!isConnected) {
            try {
                socket = new Socket(hostname, port);
//                objectInputStream = new ObjectInputStream(socket.getInputStream());
                dataInputStream = new DataInputStream(socket.getInputStream());
//                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                socket.setSoTimeout(0);
                isConnected = true;
//                heartBeat.start();
                if (!ClientAction.action.isNullLV()) {
                    ClientAction.action.connectMSG(isConnected);
                }
//                this.send(new Message(MessageType.JOIN,ClientAction.action.getChatView()));
//                this.send(new Message(MessageType.JOIN,self));
            } catch (IOException e) {
                if (socket != null) {
                    socket.close();
                }
                System.out.println("与服务器连接失败,错误代码:0");
                System.out.println("正在尝试连接...");
                isConnected = false;
                if (!ClientAction.action.isNullLV()) {
                    ClientAction.action.connectMSG(isConnected);
                }
//                heartBeat.stop();
                connect();  // 循环请求
            }
        }

    }

//    public void stopHeartBeat() {
//        heartBeat.stop();
//    }

    @Override
    public void run() {
        while (true) {
            try {
                connect();
                while (isConnected) {
//                    DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                    String msg = dataInputStream.readUTF();

                    System.out.println("消息来咯 : " + msg);
////                            String fromID = message.getFromID();
                    ClientAction.action.receiveMsg(msg);


                }
            } catch (IOException e) {
                System.out.println("与服务器断开连接,错误代码:1");

            } finally {
                isConnected = false;
                closeSocket();
            }
        }


    }


    public void closeSocket() {
        if (socket == null) {
            return;
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        if (isConnected) {
            try {
                dataOutputStream.writeUTF(message);
//                dataOutputStream.reset();
            } catch (IOException e) {
                System.out.println("向服务器发送数据失败,错误代码:2");
                e.printStackTrace();
                isConnected = false;
            }
        }
    }
}
