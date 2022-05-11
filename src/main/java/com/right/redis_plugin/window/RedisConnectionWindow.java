package com.right.redis_plugin.window;

import com.right.redis_plugin.data.DataCenter;
import com.right.redis_plugin.data.RedisConfig;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import static java.awt.event.InputEvent.BUTTON1_MASK;

public class RedisConnectionWindow extends JDialog {
    private JPanel contentPane;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTabbedPane tabbedPane1;
    private JList list1;
    private JButton testConnectionButton;
    private JSlider slider1;
    private JTextField name;
    private JTextField host;
    private JTextField port;
    private JTextField password;
    private RedisToolsWindow redisToolsWindow;

    public RedisConnectionWindow(RedisToolsWindow redisToolsWindow) {
        this.redisToolsWindow = redisToolsWindow;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        init();
        button1.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if (e.getModifiers() == BUTTON1_MASK) {
                    RedisConfig config = new RedisConfig();
                    config.setName("untitled");
                    DataCenter.addConfig(config);
                    DefaultListModel dlm = (DefaultListModel) list1.getModel();
                    dlm.addElement(config);
                    list1.setModel(dlm);
                }
            }
        });
        testConnectionButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if (e.getModifiers() == BUTTON1_MASK) {
                    String message = "";
                    if (StringUtils.isEmpty(name.getText())) {
                        message = "name is not null";
                    } else if (StringUtils.isEmpty(host.getText())) {
                        message = "host is not null";
                    } else if (StringUtils.isEmpty(port.getText())) {
                        message = "port is not null";
                    } else {
                        RedisConfig redisConfig = DataCenter.get((RedisConfig)list1.getSelectedValue());
                        redisConfig.setName(name.getText());
                        redisConfig.setIp(host.getText());
                        redisConfig.setPort(port.getText());
                        redisConfig.setPassword(password.getText());
                        boolean connection = DataCenter.connection(redisConfig.getId());
                        if (connection) {
                            message = "connection success!";
                        } else {
                            message = "connection failed! /(ㄒoㄒ)/~~";
                        }
                    }
                    RedisConnectionResultWindow.onShow(message);
                    DataCenter.doUpdateListen();
                    redisToolsWindow.updateComboBox();
                }
            }
        });
        list1.addListSelectionListener(new ListSelectionListener() {
            @Override public void valueChanged(ListSelectionEvent e) {
                if (!list1.getValueIsAdjusting()) {    //设置只有释放鼠标时才触发
                    System.out.println(list1.getSelectedValue());
                    RedisConfig redisConfig = DataCenter.get((RedisConfig) list1.getSelectedValue());
                    if (redisConfig != null) {
                        name.setText(redisConfig.getName());
                        host.setText(redisConfig.getIp());
                        port.setText(redisConfig.getPort());
                        password.setText(redisConfig.getPassword());
                    }
                }
            }
        });
        button2.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if (e.getModifiers() == BUTTON1_MASK) {
                    if (!list1.getValueIsAdjusting()) {    //设置只有释放鼠标时才触发
                        System.out.println(list1.getSelectedValue());
                        RedisConfig redisConfig = DataCenter.remove((RedisConfig) list1.getSelectedValue());
                        DefaultListModel dlm = (DefaultListModel) list1.getModel();
                        dlm.removeElement(list1.getSelectedValue());
                    }
                }
            }
        });
        button3.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if (e.getModifiers() == BUTTON1_MASK) {
                    onOK();
                }
            }
        });
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getModifiers() == BUTTON1_MASK) {
                    onOK();
                }
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (e.getModifiers() == BUTTON1_MASK) {
                    onCancel();
                }
            }
        });
        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public static void onShow(ActionListener actionListener) {
    }

    public static void onShow(RedisToolsWindow redisToolsWindow) {
        RedisConnectionWindow dialog = new RedisConnectionWindow(redisToolsWindow);
        dialog.setTitle("Deployment");
        dialog.setIconImage(new ImageIcon(DataCenter.PROJECT_PATH + "META-INF/icon.png").getImage());
        dialog.pack();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - dialog.getWidth() / 2;
        int y = (int) screensize.getHeight() / 2 - dialog.getHeight() / 2;
        dialog.setLocation(x, y);
        dialog.setVisible(true);
    }

    public static void main(String[] args) throws URISyntaxException, MalformedURLException {
    }

    public void updateList() {
        //        DefaultListModel defaultListModel = new DefaultListModel();
        //        list1.setModel(defaultListModel.add);
    }

    public void init() {
        DataCenter.getMap().forEach((k, v) -> {
            DefaultListModel dlm = (DefaultListModel) list1.getModel();
            dlm.addElement(v);
            list1.setModel(dlm);
        });
    }

    private void onOK() {
        //保存
        DataCenter.doUpdateListen();
        redisToolsWindow.updateComboBox();
        // add your code here
        dispose();
    }

    private void onCancel() {
        //不保存
        // add your code here if necessary
        dispose();
    }
}
