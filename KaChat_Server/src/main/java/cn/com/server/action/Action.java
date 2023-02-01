package cn.com.server.action;


import cn.com.server.util.ServerUtil;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;

public class Action {

    private ServerSocket serverSocket;
    //    public static HashMap<String, ChatView> chatViews = new HashMap<>();
//    public static HashMap<String, MainView> mainViews = new HashMap<>();
    public static HashMap<String, ClientCoon> clientChatLists = new HashMap<>();

    public static Action action;

    public Action() {
//        dao = new Dao();
        action = this;
        try {
            serverSocket = new ServerSocket(8888);
            System.out.println("welcome (￣y▽,￣)╭ \n");
            init();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void init() throws IOException {
        while (true) {
            try {
                Socket s = serverSocket.accept();
                if (s != null) {
//                    sockets.put(String.valueOf(s.getPort()), s);
//                    clientChatList.add(new ClientCoon(s));
                    clientChatLists.put(String.valueOf(s.getPort()), new ClientCoon(s));
//                    clientChatLists.put(String.valueOf(s.getPort()),new ServerThread(s));
                    System.out.println("[JOIN] \tIP:" + s.getInetAddress() + "已连接,对方端口为:" + s.getPort() + ServerUtil.getDateInNow());

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public void useQuit(SocketAddress socketAddress, String quitPort) {

        System.out.println("[QUIT]\tIP: " + socketAddress + "  退出聊天室，欢迎下次见~(/▽＼)" + ServerUtil.getDateInNow());
//            Action.action.fdState(uID);
        clientChatLists.remove(quitPort);
//        }
    }

    class ClientCoon implements Runnable {
        Socket socket = null;
        Thread self = null;
        Boolean isConnected = false;

        public ClientCoon(Socket socket) {
            this.socket = socket;
            isConnected = true;
            self = new Thread(this);
            self.start();
        }

        //接受客户端信息（多线程run（）方法）
        @Override
        public void run() {
            try {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                // 为了让服务器能够接受到每个客户端的多句话
                while (isConnected) {
                    //readUTF()是一种阻塞方法，接一句就执行完了，所以循环中
//                    dataInputStream.available();
                    if (!socket.isClosed()) {
                        String str = dataInputStream.readUTF(); // unicode transformation format
                        if (str.equals("EXIT")) {
//                        socket.close();
                            action.useQuit(socket.getRemoteSocketAddress(), String.valueOf(socket.getPort()));
//                        this.close();
                            self.interrupt();
//                        self.stop();
//                        isConnected = false;
//                        dataInputStream.close();
                            break;
                        }
                        System.out.println("\n" + socket.getInetAddress() + ":" + socket.getPort() + "发送消息: " + str + "\n");

                        String strSend = socket.getPort() + "说" + str;
                        for (String port : clientChatLists.keySet()) {
                            if (!port.equals(String.valueOf(socket.getPort()))) {
                                ClientCoon clientCoon = clientChatLists.get(port);
                                clientCoon.send(strSend);
                            }
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 服务器向每個连接对象发送数据的方法
        public void send(String str) {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(this.socket.getOutputStream());
                dataOutputStream.writeUTF(str);
            } catch (IOException ignored) {
//                e.printStackTrace();
            }
        }
    }


}
