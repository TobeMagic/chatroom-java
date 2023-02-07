# TCP用多线程实现多人聊天的项目

知识要点: I0 编程、Socket 编程、多线程编程、异常处理、集合类的使用

编程思路:

1、客户端聊天窗口的创建

2、添加服务器与客户端的连接

3、连接上以后考虑将客户端的信息发送到服务器上

4、利用多线程实现连接多个客户端

5、多个客户端的信息发送到服务器上以后，考虑把服务器上的信息发送到每个客户端

6、在服务器端要取到每个客户端的socket,才可以把信息发送到每个客户端上，利用集合类存储客户的多个线程的连接

7、在客户端考虑利用多线程接受服务器上的信息

8、最后实现多人聊天室的效果

9、后期修改程序中的不足和发现bug

![image-20210302201219701](https://typora-wenjiuzhou.oss-cn-beijing.aliyuncs.com/20210302201226.png)

## 编程思路 —— （后端）:



### 1、 添加服务器（分发和接受）与客户端（多线程）的连接

#### 服务器

![image-20210302210503340](https://typora-wenjiuzhou.oss-cn-beijing.aliyuncs.com/20210302210503.png)

```java
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

```



####客户端

![image-20210302212838914](https://typora-wenjiuzhou.oss-cn-beijing.aliyuncs.com/20210302212838.png)

```java
    private void init() {
        System.setProperty("sun.java2d.noddraw", "true"); // 设置swing系统的静态变量，存贮在内存中 完全关闭JAVA 2D/3D的DirectDraw或者Direct3D的功能
        loginView = new LoginView();
        Animate.surfaceIn(loginView);
        Animate.slideXOnL(loginView.pictures);
//        clientListener = new ClientListener("127.0.0.1",9090);
        clientListener = new ClientListener("localhost", 8888);

        ThreadPool.poolExecutor.execute(clientListener);
//        new Thread(clientListener).start();
    }

```

ClientListener

```java
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

```



### 2、连接上以后考虑将客户端的信息发送到服务器上

在考虑发送信息到服务器上

![image-20210302215052078](https://typora-wenjiuzhou.oss-cn-beijing.aliyuncs.com/20210302215052.png)

```java
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

```



### 3、.利用多线程实现连接多个客户端

![image-20210302221332736](https://typora-wenjiuzhou.oss-cn-beijing.aliyuncs.com/20210302221332.png)



```java
// 多个客户端访问时，客户端对象存放入List中
    private ArrayList<ClientCoon> ccList = new ArrayList<ClientCoon>();

    // 服务器启动的标志 (其实ServerSocket ss 初始化出来时以为者服务器的启动)
    private boolean isStart = false;
```

```java

class ClientCoon implements Runnable {
        Socket socket = null;

        public ClientCoon(Socket socket) {
            this.socket = socket;
            /**
             * 线程启动在这里：
             * 初始化方法里 初始化一个线程 ，线程中封装的是自己，做整个线程的调用
             */
            (new Thread(this)).start();
        }

        @Override
        public void run() {
            try {
                DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
                String str = dataInputStream.readUTF();
                System.out.println(str);
                serverTa.append(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
```



### 4、多个客户端的信息发送到服务器上以后，考虑把服务器上的信息发送到每个客户端

![image-20210302233159877](https://typora-wenjiuzhou.oss-cn-beijing.aliyuncs.com/20210302233159.png)

#### 服務器端：

```java
// 服务器向每個连接对象发送数据的方法
        public void send(String str) {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(this.s.getOutputStream());
                dataOutputStream.writeUTF(str);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
```

```java
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


```

终端效果

![image-20230207093921140](多人聊天系统 .assets/image-20230207093921140.png)



## 编程思路 —— （前端）：

### 1、登录页面

![image-20230207092722112](多人聊天系统 .assets/image-20230207092722112.png)

当未连接到服务器时，标签文字显示和加载

![image-20230207092802383](多人聊天系统 .assets/image-20230207092802383.png)

使用卡片式布局，前面的登录页面继承Jframe，并封装animate，使其最小化或关闭活动获得特效效果。

背景切换在animate中开线程组件循环图片。

```java
package cn.com.view;


import cn.com.action.ClientAction;
import cn.com.util.ResourcesUtils;
import cn.com.view.animate.LodingJLabel;
import cn.com.view.viewutil.Style;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class LoginView extends JFrame {
    private LoginView self; // 自应用

    private int fWidth;
    private int fHeight;

    ImageIcon textIcon;
    //    ImageIcon textClickIcon;
    ImageIcon buttonIcon;
    ImageIcon buttonClickIcon;

    JLabel login;
    JLabel left;
    JLabel quit;
    JLabel munuBar;
    JLabel minimize;

    public JLabel bg1;
    public JLabel bg2;
    public CardLayout bgCardLayout;
    public JLabel uidL;
    public JLabel pwdL;
    public JLabel msg;
    public JLabel buttonL;
    public JLabel buttonClickL;
    //    public JTextArea introduce;
//    public JPasswordField pwgText;
    public JLabel userIconL;
    public LodingJLabel lodingJLabel;
    private Point windowPoint;

    Font buttonFont;
    Font msgFont;
//    public JLabel registerIcon;
//    public Register register;

    JLabel fPicture;
    public List<Component> pictures;

    public LoginView() {
        self = this;
        fWidth = 1400;
        fHeight = 750;


        bg1 = new JLabel();
        bg2 = new JLabel();
        bgCardLayout = new CardLayout();
        windowPoint = new Point();

        lodingJLabel = new LodingJLabel(5, 0, 5);
        left = new JLabel();
//        register = new Register();
        pictures = new ArrayList<Component>();
        fPicture = new JLabel();
        login = new JLabel();
        buttonClickL = new JLabel();
        quit = new JLabel("●");  // 退出
        minimize = new JLabel("●"); // 最小化
        munuBar = new JLabel();
        userIconL = new JLabel();
        msg = new JLabel();
        uidL = new JLabel();
        pwdL = new JLabel();
        buttonL = new JLabel();
        textIcon = new ImageIcon(ResourcesUtils.getResource("/view/icon/loginview/text1.png", "text1", ".png").getAbsolutePath());
//        textClickIcon = new ImageIcon(ResourcesUtils.getResource("/view/icon/loginview/text.png", "text", ".png").getAbsolutePath());
        buttonIcon = new ImageIcon(ResourcesUtils.getResource("/view/icon/loginview/button.png", "button", ".png").getAbsolutePath());
        buttonClickIcon = new ImageIcon(ResourcesUtils.getResource("/view/icon/loginview/button1.png", "button", ".png").getAbsolutePath());
//        introduce = new JTextArea("Welcome to magic-chatroom !");
//        pwgText = new JPasswordField("请输入密码");
        msgFont = new Font("黑体", Font.CENTER_BASELINE, 20);
        buttonFont = new Font("黑体", Font.CENTER_BASELINE, 30);
//        registerIcon = new JLabel();
        init();
        assemble();
        setAction();
    }


    private void init() {

        setTitle("magic-chatroom:登录界面");
        setLayout(null);
        setLocationRelativeTo(null);
        setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - fWidth) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - fHeight) / 2, fWidth, fHeight);
        setFocusable(true);
        setUndecorated(true);
        setBackground(Style.nullColor);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        bg1.setBounds(0, 0, fWidth, fHeight);
        bg1.setLayout(bgCardLayout);

        bg2.setBounds(0, 0, fWidth, fHeight);
        bg2.setLayout(null);


        login.setBounds(730, 0, 650, 750);
        login.setIcon(new ImageIcon(ResourcesUtils.getResource("/view/icon/loginview/login.png", "login", ".png").getAbsolutePath()));

        left.setBounds(0, 55, 1400, 570);
        left.setLayout(null);
        left.setIcon(new ImageIcon(ResourcesUtils.getResource("/view/icon/loginview/left.png", "left", ".png").getAbsolutePath()));


        fPicture.setBounds(10, 10, 1380, 550);
        fPicture.setLayout(null);
        fPicture.setIcon(new ImageIcon(ResourcesUtils.getResource("/view/icon/picture/picture4.png", "picture4", ".png").getAbsolutePath()));

        munuBar.setBounds(30, 15, 470, 70);
        munuBar.setText("<html><div style=\"font-family:Microsoft YaHei;color: gray;font-size:12px\" >Magic-Chat</div></html>");

        quit.setBounds(535, 37, 40, 30);
        quit.setFont(buttonFont);
        quit.setForeground(Style.pinColor);

        minimize.setBounds(495, 37, 40, 30);
        minimize.setFont(buttonFont);
        minimize.setForeground(Style.bluColor);

        userIconL.setBounds(240, 95, 120, 120);
        ImageIcon icon = Style.Icon;
        icon.setImage(icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
        userIconL.setIcon(icon);
        setIconImage(icon.getImage());

        lodingJLabel.setBounds(235, 90, 130, 130);


        msg.setBounds(105, 235, 390, 25);
        msg.setForeground(Style.pinColor);
        msg.setFont(msgFont);
        msg.setHorizontalAlignment(SwingConstants.CENTER);


        uidL.setOpaque(false); // 设置透明
        uidL.setBounds(40, 285, 500, 200);
        uidL.setIcon(textIcon);


        buttonL.setOpaque(false);
        buttonL.setBounds(65, 510, 500, 150);
        buttonL.setIcon(buttonIcon);

        buttonClickL.setBounds(15, 27, 445, 80);
        buttonClickL.setText("登录");
        buttonClickL.setForeground(Color.white);
        buttonClickL.setHorizontalAlignment(SwingConstants.CENTER);
        buttonClickL.setFont(buttonFont);


    }

    private void assemble() {
        buttonL.add(buttonClickL);

        for (int i = 1; i <= 3; i++) {
            JLabel jLabel = new JLabel(); // 循环技巧
            jLabel.setIcon(new ImageIcon(ResourcesUtils.getResource("/view/icon/picture/picture" + i + ".png", "picture" + i + ".png", ".png").getAbsolutePath()));
            jLabel.setBounds(0, 0, 1380, 550);
            pictures.add(jLabel);
            fPicture.add(jLabel);
        }
        left.add(fPicture);
        login.add(userIconL);
        login.add(msg);
        login.add(munuBar);
        login.add(minimize);
        login.add(quit);
        login.add(buttonL);
//        login.add(pwgText);
//        login.add(uIDText);
        login.add(pwdL);
        login.add(uidL);
//        login.add(registerIcon);
        login.add(lodingJLabel);

        bg2.add(login);
//        bg2.add(register);
        bg2.add(left);

        bg1.add(bg2);
        add(bg1);


    }

    public JLabel getMsg() {
        return msg;
    }


    public void setAction() {

        final Point origin = new Point();
        munuBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                origin.x = e.getX();
                origin.y = e.getY();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

            }
        });
        munuBar.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                Point point = getLocation();
                setLocation(point.x + e.getX() - origin.x, point.y + e.getY() - origin.y);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });

        minimize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ClientAction.action.miniSize(self, bg1, bgCardLayout, windowPoint, false);
            }
        });


        quit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ClientAction.action.useQuit(self, bg1, bgCardLayout, true);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeiconified(WindowEvent e) {
                ClientAction.action.miniSize(self, bg1, bgCardLayout, windowPoint, true);
            }

//            @Override
//            public void windowClosing(WindowEvent e) {
//                ClientAction.action.useQuit(self, bg1, bgCardLayout, false);
//            }

            @Override
            public void windowIconified(WindowEvent e) {
                if (windowPoint.getX() == 0) {
                    windowPoint.setLocation(self.getLocation());
                }
            }
        });


        buttonClickL.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mousePressed(MouseEvent e) {
//                buttonL.setLocation(buttonL.getX()-20,buttonL.getY()-20);
//                buttonClickL.setLocation(buttonClickL.getX()+25,buttonClickL.getY()+25);
//                buttonL.setIcon(buttonClickIcon);
//
//            }
//
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                buttonL.setLocation(buttonL.getX()+20,buttonL.getY()+20);
//                buttonClickL.setLocation(buttonClickL.getX()-25,buttonClickL.getY()-25);
//
//                buttonL.setIcon(buttonIcon);
//
//            }


            @Override
            public void mouseClicked(MouseEvent e) {
//                load
                buttonClickL.setText("正在登录请稍后...");

                ClientAction.action.login();

            }


        });


    }


}

```

根据是否连接设置页面是否连接到服务器

```java
    public void connectMSG(boolean isConnect) {
        if (isConnect) {
            loginView.getMsg().setText("");
            loginView.lodingJLabel.stop();
            loginView.buttonClickL.setText("进入聊天室");
        } else {
            loginView.getMsg().setText("无法连接至服务器,正在尝试重连...");
            loginView.lodingJLabel.show();
            loginView.buttonClickL.setText("进入聊天室");
        }


    }

```

其中animate代码如下

```java
package cn.com.view.animate;

import cn.com.action.ClientAction;
import cn.com.thread.ThreadPool;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class Animate {
    public static  void dither (Component component){

       ThreadPool.poolExecutor.execute(new Runnable() {
            synchronized
            @Override
            public void run() {
                synchronized (component){
                    int range = 3;
                    for (int i = 0; i <= 5; i++) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (i%2==1){
                            component.setLocation(component.getX()+range,component.getY()-range);
                        }else {
                            component.setLocation(component.getX()-range,component.getY()+range);
                        }
                    }
                }
            }
        });
    }

    public static void slideXOnL(List<Component> components){

        ThreadPool.poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (components){
                    while (!ClientAction.action.isNullLV()){
                        try {
                            Thread.sleep(10000);
                            for (int i = 0;i<components.size();i++){
                                Component component = components.get(i);
                                for (int j =0;j<=component.getWidth();j++){
                                    Thread.sleep(1);
                                    component.setLocation(j,component.getY());
                                    component.revalidate();
                                }
                                Thread.sleep(10000);
                            }

                            for (int i = components.size()-1;i>=0;i--){
                                Component component = components.get(i);
                                for (int j =component.getWidth();j>=0;j--){
                                    Thread.sleep(1);
                                    component.setLocation(j,component.getY());
                                }
                                Thread.sleep(10000);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        });
    }

    public static void surfaceOut(Window window){
        ThreadPool.poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (window){
                    Point p =  window.getLocation();
                    for (float i = 1f;i>=0f;i-=0.04f){
                        p.setLocation(p.getX(),p.getY()+1f);
                        window.setLocation(p);
                        window.setOpacity( i);
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    window.setOpacity(0f);
                    window.setVisible(false);
                }
            }
        });
    }
    public static void surfaceIn(Window window){
        ThreadPool.poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (window){  // 保证只运行该线程一个
                    window.setOpacity(0f);
                    Point p =  window.getLocation();
                    window.setVisible(true);
                    for (float i = 0f;i<=1f;i+=0.04f){
                        p.setLocation(p.getX(),p.getY()-1f);
                        window.setLocation(p);
                        window.setOpacity(i);
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    window.setOpacity(1f);
                }
            }
        });
    }


    public static  void vanishInPoint(Frame jFrame, JLabel jLabel, CardLayout cardLayout, Point toPoint) {
        ThreadPool.poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (jFrame) {

                    Point p = jFrame.getLocation();
                    Dimension d = jFrame.getSize();
                    double speed = 0.05f;
                    double scaleHgap = d.getWidth() / (4 / speed);
                    double scaleVgap = d.getHeight() / (4 / speed);
                    double scaleX = (toPoint.getX() - p.getX()) / (2 / speed);
                    double scaleY = (toPoint.getY() - p.getY()) / (2 / speed);
                    for (float i = 1f; i > 0f; i -= speed) {
                        p.setLocation(p.getX() + scaleX, p.getY() + scaleY);
                        cardLayout.setHgap((int) (cardLayout.getHgap() + scaleHgap));
                        cardLayout.setVgap((int) (cardLayout.getVgap() + scaleVgap));
                        jLabel.setLayout(cardLayout);
                        jFrame.setLocation(p);
                        jFrame.setOpacity(i);
                        jLabel.revalidate();
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    cardLayout.setHgap(jFrame.getHeight() / 2);
                    cardLayout.setVgap(jFrame.getWidth() / 2);
                    jLabel.setLayout(cardLayout);
                    jFrame.setLocation(toPoint);
                    jFrame.setOpacity(0);
                    jLabel.revalidate();
                    ;
                }
            }
        });

    }
    public static  void emergedInPoint(Frame jFrame, JLabel jLabel, CardLayout cardLayout, Point toPoint){
        ThreadPool.poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (jFrame){
                    if (cardLayout.getVgap()!=jFrame.getWidth()/2){
                        cardLayout.setHgap(jFrame.getHeight()/2);
                        cardLayout.setVgap(jFrame.getWidth()/2);
                        jLabel.setLayout(cardLayout);
                        jFrame.setOpacity(0);
                        jLabel.revalidate();;
                    }
                    Point p = jFrame.getLocation();
                    double speed = 0.02f;
                    double scaleHgap = cardLayout.getHgap()/ (1/speed);
                    double scaleVgap = cardLayout.getVgap()/(1/speed);
                    double scaleX = (toPoint.getX()-p.getX())/ (1/speed);
                    double scaleY =(toPoint.getY()-p.getY())/ (1/speed);
                    for (float i = 0f;i<=1f;i+=speed){
                        p.setLocation(p.getX()+scaleX,p.getY()+scaleY);
                        cardLayout.setHgap((int) (cardLayout.getHgap()-scaleHgap));
                        cardLayout.setVgap((int) (cardLayout.getVgap()-scaleVgap));
                        jLabel.setLayout(cardLayout);
                        jFrame.setLocation(p);
                        jFrame.setOpacity(i);
                        jLabel.revalidate();
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    cardLayout.setHgap(0);
                    cardLayout.setVgap(0);
                    jLabel.setLayout(cardLayout);
                    jFrame.setLocation(toPoint);
                    jFrame.setOpacity(1);
                    jLabel.revalidate();

                }
            }
        });

    }




}

```

### 2、聊天页面

![image-20230207093321795](多人聊天系统 .assets/image-20230207093321795.png)

使用卡片式布局，自定义标签，设置window和时间监听器，达到用户UI交互体验，

```java
package cn.com.view;


import cn.com.action.ClientAction;
//import cn.com.dao.ClientDAO;
import cn.com.thread.ClientListener;
import cn.com.util.ResourcesUtils;
import cn.com.view.animate.Animate;
import cn.com.view.viewutil.Style;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainView extends JFrame {

    private JFrame self;
    private int fWidth;
    private int fHeight;

    JLabel mainView;
//    JLabel menubarView;

    JLabel winMunuBar;

    public JLabel bg1;
    public JLabel bg2;
    public CardLayout bgCardLayout;

    public Point windowPoint;



    JLabel quit;
    JLabel minimSize;
    private JLabel main;
    CardLayout cardLayout;
    private MainChatCard mainChatCard;
    ClientListener clientListener;



    public MainView() {
        fWidth = 422;
        fHeight = 700;

        bg1 = new JLabel();
        bg2 = new JLabel();
        bgCardLayout = new CardLayout();
        self = this;
        windowPoint = new Point();

        mainView = new JLabel();


        cardLayout = new CardLayout(10, 10);
        main = new JLabel();
        quit = new JLabel();
        minimSize = new JLabel();
        winMunuBar = new JLabel();
        mainChatCard = new MainChatCard();


        init();
        assemble();
        setAction();
        cardLayout.next(main);
    }


    private void init() {
        setLayout(null);
        setIconImage(Style.Icon.getImage());
        setTitle("主界面");
        setBounds((Toolkit.getDefaultToolkit().getScreenSize().width - fWidth) / 2, (Toolkit.getDefaultToolkit().getScreenSize().height - fHeight) / 2, fWidth, fHeight); // 居中保持
        setFocusable(true);
        setUndecorated(true);
        setBackground(Style.nullColor);

        bg1.setBounds(0, 0, fWidth, fHeight);
        bg1.setLayout(bgCardLayout);

        bg2.setBounds(0, 0, fWidth, fHeight);
        bg2.setLayout(null);

        mainView.setBounds(0, 30, 422, 640);
        mainView.setIcon(new ImageIcon(ResourcesUtils.getResource("/view/icon/mainview/main.png", "main", ".png").getAbsolutePath()));
        mainView.setLayout(null);



        quit.setBounds(380, 45, 20, 20);
        quit.setFont(new Font("黑体", 0, 20));
        quit.setForeground(Style.pinColor);
        quit.setText("●");

        minimSize.setBounds(350, 45, 20, 20);
        minimSize.setFont(new Font("黑体", 0, 20));
        minimSize.setForeground(Style.bluColor);
        minimSize.setText("●");
        winMunuBar.setBounds(0, 0, 422, 40);


        main.setBounds(0, 0, 422, 640);
        main.setLayout(cardLayout);


    }

    private void assemble() {


        main.add(mainChatCard, "chat");
//        main.add(mainAddCard,"add");
        mainView.add(main);
        mainView.add(winMunuBar);

        bg2.add(quit);
        bg2.add(minimSize);
//        bg2.add(menubarView);
        bg2.add(mainView);

        bg1.add(bg2);
        add(bg1);

    }

    private void setAction() {


        final Point origin = new Point();
        winMunuBar.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                origin.x = e.getX();
                origin.y = e.getY();
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

            }
        });
        winMunuBar.addMouseMotionListener(new MouseAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                Point point = getLocation();
                setLocation(point.x + e.getX() - origin.x, point.y + e.getY() - origin.y);
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

            }
        });

        minimSize.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ClientAction.action.miniSize(self, bg1, bgCardLayout, windowPoint, false);
            }
        });

        quit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    ClientAction.action.useQuit(self, bg1, bgCardLayout, true);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        });
        addWindowListener(new WindowAdapter() {
            boolean stateSW = true;

            @Override
            public void windowDeiconified(WindowEvent e) {
                super.windowIconified(e);
                if (!stateSW) {
                    ClientAction.action.miniSize(self, bg1, bgCardLayout, windowPoint, true);
                    stateSW = !stateSW;
                }
            }

            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    ClientAction.action.useQuit(self, bg1, bgCardLayout, false);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void windowIconified(WindowEvent e) {
                if (windowPoint.getX() == 0) {
                    windowPoint.setLocation(self.getX(), self.getY());
                }
                stateSW = !stateSW;
            }
        });

    }

    public JLabel getChatView() {
        return mainChatCard.chatView;
    }

//    public MainAddCard getMainAddCard() {
//        return mainAddCard;
//    }
}

```





![image-20230207093705917](多人聊天系统 .assets/image-20230207093705917.png)

发送数据带有聊天气泡，且其左右对称，具有头像， 使用了支持HTML渲染的性质，以及卡片布局，达到左右对称的效果，且通过标签的setIcon函数自定义portriat ， 获取头像

```java
    public void sendMsg(ChatView chatView) {
        ImageIcon AImagician = new ImageIcon(Objects.requireNonNull(ResourcesUtils.getResource("/view/icon/portrait/portrait" + ".png", "portrait", ".png")).getAbsolutePath());
        ChatMessage selfMsg = new ChatMessage(AImagician, chatView.chatIn.getText(), true, "AImagician");
        chatView.chatOut.add(selfMsg);
//
//        clientListener.send(new Message(MessageType.MESSAGE, chatView.chatIn.getText()));
        clientListener.send(chatView.chatIn.getText());
//
//        ClientDAO.getFriend(chatView.toID).setMotto(chatView.chatIn.getText());
//        flushUserInFList(chatView.toID, true); // 离线缓存输出
        chatView.chatIn.setText("");
        chatView.chatOut.revalidate();  // 刷新页面
        chatView.jScrollBarOfOut.setValue(chatView.jScrollBarOfOut.getMaximum());
    }

```



终端效果

![image-20230207094000784](多人聊天系统 .assets/image-20230207094000784.png)