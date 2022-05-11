package com.right.redis_plugin.window;

import com.right.redis_plugin.data.DataCenter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RedisConnectionResultWindow extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea textArea1;

    public RedisConnectionResultWindow() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
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

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


    public void  setConnectionResult(String message){
        textArea1.setText(message);
    }
    public static  void onShow(String message){
        RedisConnectionResultWindow dialog = new RedisConnectionResultWindow();
        dialog.setTitle("Connection result");
        dialog.setIconImage( new ImageIcon(DataCenter.PROJECT_PATH + "META-INF/icon.png").getImage());
        dialog.pack();
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screensize.getWidth() / 2 - dialog.getWidth() / 2;
        int y = (int) screensize.getHeight() / 2 - dialog.getHeight() / 2;
        dialog.setLocation(x,y);
        dialog.setConnectionResult(message);
        dialog.setVisible(true);

    }



    public static void main(String[] args) {
        RedisConnectionResultWindow dialog = new RedisConnectionResultWindow();
        dialog.setTitle("connection result");
        dialog.setIconImage( new ImageIcon(DataCenter.PROJECT_PATH + "META-INF/icon.png").getImage());

        dialog.pack();
        dialog.setVisible(true);

    }
}
