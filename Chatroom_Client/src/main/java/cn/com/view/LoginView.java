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
