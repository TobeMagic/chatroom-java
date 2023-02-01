package cn.com.action;

//import cn.com.dao.ClientDAO;

import cn.com.thread.ClientListener;
import cn.com.thread.ThreadPool;
import cn.com.util.ResourcesUtils;
import cn.com.view.*;
import cn.com.view.animate.Animate;

import javax.swing.*;
import java.awt.*;
import java.io.Serializable;
import java.util.Objects;


public class ClientAction implements Serializable {
    private LoginView loginView;
    private MainView mainView;
    private ClientListener clientListener;
    //    public HashMap<String, ChatView> chatCards;
    JLabel chatView;
    public static ClientAction action;


    public ClientAction() {
        action = this;  // 全局对象，资源共享
        init();
    }

    private void init() {
        System.setProperty("sun.java2d.noddraw", "true"); // 设置swing系统的静态变量，存贮在内存中 完全关闭JAVA 2D/3D的DirectDraw或者Direct3D的功能
        loginView = new LoginView();
        Animate.surfaceIn(loginView);
        Animate.slideXOnL(loginView.pictures);
//        clientListener = new ClientListener("127.0.0.1",9090);
        clientListener = new ClientListener("localhost", 8888);
//        113.245.83.186
//        clientListener = new ClientLS                                                                                                                     istener("169.254.183.20",7758);

        ThreadPool.poolExecutor.execute(clientListener);
//        new Thread(clientListener).start();
    }

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


    public void login() {

        if (clientListener == null || !clientListener.isConnected) {
            connectMSG(false);
            Animate.dither(loginView.buttonL);
            Animate.dither(loginView.msg);
            return;
        }

        loginView.lodingJLabel.stop();
        loginOK("OK");

    }


    public void loginOK(String ok) {
        if (loginView == null) {
            return;
        }
        if (ok.equals("OK")) {
            loginView.buttonClickL.setText("欢迎光临..");
//            upData(data);
            creaMainView();
//            clientListener.send(new Message(MessageType.JOIN,null));
            return;
        }

        loginView.lodingJLabel.stop();


    }

    public void creaMainView() {
//        chatCards = new HashMap<String, ChatView>();
        Animate.surfaceOut(loginView);
        mainView = new MainView();
        this.chatView = mainView.getChatView();
        Animate.surfaceIn(mainView);
//        mainChatCard.cardLayout.first(mainChatCard.chatView);
        loginView = null;
//        clientListener.send(new Message(MessageType.JOIN,mainView));
    }


//    public void upData(User u) {
//        ClientDAO.upData(u);
//    }


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

    //    public void receiveMsg(String fromID, String msg) {
    public void receiveMsg(String msg) {
//        ClientDAO.getFriend(fromID).setMotto(msg);
//        ChatView chatView = chatCards.get(fromID);
        System.out.println("消息来咯 : " + msg.split("说")[1]);


        ImageIcon AImagicianSweetheart = new ImageIcon(Objects.requireNonNull(ResourcesUtils.getResource("/view/icon/portrait/portrait" + ".png", "portrait", ".png")).getAbsolutePath());
        ((ChatView) chatView).chatOut.add(new ChatMessage(AImagicianSweetheart, msg.split("说")[1], false, msg.split("说")[0]));
        ((ChatView) chatView).jScrollBarOfOut.setValue(((ChatView) chatView).jScrollBarOfOut.getMaximum());

    }


//    public JLabel getChatView() {
//        return this.mainView.getChatView();
//    }

    public boolean textFocusLost(JTextField textField, String text) {
        if (textField.getText().equals("")) {
            textField.setText(text);
            return true;
        }
        return false;
    }

    public boolean textFocusGained(JTextField textField, String text) {

        if (textField.getText().equals(text)) {
            textField.setText("");
            return false;
        }
        return true;
    }

    public boolean pwFocusGained(JPasswordField pwText, String text) {
        if (pwText.getText().equals(text)) {
            pwText.setText("");
            pwText.setEchoChar('●');
            return false;
        }
        return true;
    }

    public boolean pwFocusLost(JPasswordField pwText, String text) {
        if (pwText.getText().equals("")) {
            pwText.setEchoChar((char) 0);
            pwText.setText(text);
            return true;
        }
        return false;
    }

    public boolean isNullLV() {
        return loginView == null;
    }


    public void useQuit(Frame self, JLabel jLabel, CardLayout cardLayout, boolean isClick) throws InterruptedException {
        if (isClick) {
            Point sp = new Point(self.getX() + self.getWidth() / 2, self.getY() - self.getHeight() / 2);
            Animate.vanishInPoint(self, jLabel, cardLayout, sp);
        } else {
            Animate.surfaceOut(self);
        }

        clientListener.send("EXIT");
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
//        clientListener.closeSocket();
        System.exit(0);
    }

    public void miniSize(Frame self, JLabel jLabel, CardLayout cardLayout, Point windowPoint, boolean sw) {
        Point midpoint = new Point((Toolkit.getDefaultToolkit().getScreenSize().width - self.getWidth()) / 2, Toolkit.getDefaultToolkit().getScreenSize().height);

        if (sw) {
            self.setLocation(midpoint);
            Animate.emergedInPoint(self, jLabel, cardLayout, windowPoint);
        } else {
            windowPoint.setLocation(self.getLocation());

            Animate.vanishInPoint(self, jLabel, cardLayout, midpoint);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            self.setExtendedState(Frame.ICONIFIED);
        }

    }


}
