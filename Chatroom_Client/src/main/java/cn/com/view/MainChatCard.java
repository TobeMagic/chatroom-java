package cn.com.view;

import cn.com.util.ResourcesUtils;
import cn.com.view.viewutil.Style;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class MainChatCard extends JLabel {


    JLabel name;
    JLabel online;

    public CardLayout cardLayout;
    public JLabel chatView = new ChatView();

    MainChatCard() {


        name = new JLabel();
        online = new JLabel();


        cardLayout = new CardLayout();
        init();
        assemble();
//        setAction();


    }


    private void init() {

        setBounds(0, 0, 422, 622);
        setIcon(new ImageIcon(Objects.requireNonNull(ResourcesUtils.getResource("/view/icon/mainview/chat/mianbgview.png", "mianbgview", ".png")).getAbsolutePath()));
        setLayout(null);


        online.setBounds(20, 22, 20, 20);
        online.setFont(new Font("黑体", Font.PLAIN, 20));
        online.setText("●");
        online.setForeground(Style.greenColor);

        name.setBounds(50, 20, 250, 25);
        name.setFont(new Font("黑体", Font.PLAIN, 20));
        name.setText("在线聊天室");

        chatView.setBounds(8, 83, 410, 544);
//        chatView.setLayout(cardLayout);
        chatView.setLayout(null);


    }

    private void assemble() {


        add(online);
        add(name);
        add(chatView);
//        clientListener.send(new Message(MessageType.JOIN, chatView));
    }


}
