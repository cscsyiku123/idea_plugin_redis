package com.right.redis_plugin.window;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.right.redis_plugin.data.DataCenter;
import com.right.redis_plugin.data.RedisConfig;
import com.right.redis_plugin.data.RedisConsole;
import com.right.redis_plugin.data.RedisDB;


import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.event.InputEvent.BUTTON1_MASK;

public class RedisToolsWindow {
    private JPanel panel1;
    private JComboBox connectionList;
    private JButton connectionButton;
    private JButton refreshButton;
    private JTree tree1;
    private JPanel panel2;
    private JScrollPane panel3;
    public void init(){
        //数据初始化
        DataCenter.init();
        //下拉列表初始化
        updateComboBox();
//        DataCenter.getMap().forEach((k,v)->{
//            connectionList.addItem(v);
//        });
//        RedisConfig defaultConfig = new RedisConfig();
//        defaultConfig.setName("<None>");
////        connectionList.addItem(defaultConfig);
//        connectionList.setSelectedItem(defaultConfig);

    }
    public RedisToolsWindow(Project project, ToolWindow toolWindow) {
        connectionButton.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                if (e.getModifiers() == BUTTON1_MASK) {
                    if (DataCenter.getMap().size() == 0) {
                        RedisConfig config = new RedisConfig();
                        config.setName("untitled");
                        DataCenter.addConfig(config);
                    }
                    RedisConnectionWindow.onShow(RedisToolsWindow.this);
                }
            }
        });
        connectionList.addActionListener(new ActionListener() {
            @Override public void actionPerformed(ActionEvent e) {
                RedisConfig redisConfig = DataCenter.get((RedisConfig) connectionList.getSelectedItem());
                DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
                rootNode.setUserObject(redisConfig);
                DefaultTreeModel model = new DefaultTreeModel(rootNode);
                tree1.setModel(model);
            }
        });

        tree1.addTreeSelectionListener(new TreeSelectionListener() {
            @Override public void valueChanged(TreeSelectionEvent e) {
                DefaultMutableTreeNode selectedNode=(DefaultMutableTreeNode) tree1.getLastSelectedPathComponent();//返回最后选定的节点
                Object userObject = selectedNode.getUserObject();
                if (userObject instanceof RedisConfig){
                    RedisConfig redisConfig = DataCenter.get((RedisConfig) userObject);
                    if (redisConfig != null) {
                        //                   DataCenter.sendCommand(redisConfig.getId(),"CONFIG GET databases");
                        if (selectedNode.getChildCount() == 0) {
                            selectedNode.add(new DefaultMutableTreeNode(new RedisDB(0,"db-"+0)));
                        }
                    }
                } else if (userObject instanceof RedisDB){
                    RedisDB db = (RedisDB) userObject;
                    RedisConfig redisConfig = DataCenter.get((RedisConfig) connectionList.getSelectedItem());
                    //打开console 页面
                    RedisConsole redisConsole = redisConfig.getRedisConsole();
                    //发送select 指令
                    DataCenter.sendCommand(redisConfig.getId(),"select "+db.getId());
                }
            }
        });

        init();
    }

    public void updateComboBox(){
        connectionList.setModel(new DefaultComboBoxModel(DataCenter.getMap().values().toArray()));
    }

    public JPanel getJcontent() {
        return panel1;
    }

//    public void showConnectionWindow(){
//        RedisConnectionWindow dialog = new RedisConnectionWindow();
//        dialog.setTitle("Deployment");
//        dialog.setIconImage( new ImageIcon(DataCenter.PROJECT_PATH+"META-INF/icon.png").getImage());
//        dialog.pack();
//        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
//        int x = (int) screensize.getWidth() / 2 - dialog.getWidth() / 2;
//        int y = (int) screensize.getHeight() / 2 - dialog.getHeight() / 2;
//        dialog.setLocation(x,y);
//        dialog.setVisible(true);
//        System.exit(0);
//    }
    public static void main(String[] args){
        System.out.println(123);
    }
}
