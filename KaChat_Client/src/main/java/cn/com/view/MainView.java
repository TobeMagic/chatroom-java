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
