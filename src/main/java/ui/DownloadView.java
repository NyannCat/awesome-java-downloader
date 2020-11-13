/*
 * Created by JFormDesigner on Thu Nov 12 22:39:47 CST 2020
 */

package ui;

import http.DownloadStatus;
import http.HttpDownloadTask;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
import javax.swing.border.*;

/**
 * @author aa
 */
public class DownloadView extends JFrame {
    private HttpDownloadTask instance;
    public DownloadView() {
        initComponents();
    }

    private void button2MouseClicked(MouseEvent e) {
        // TODO add your code here
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.showDialog(new JLabel(), "请选择下载路径");
        File file = chooser.getSelectedFile();
        if (file != null) {
            textField2.setText(file.getAbsolutePath());
        }
    }

    private void button3MouseClicked(MouseEvent e) {
        if (button3.isEnabled()) {
            instance.stop();
            button3.setEnabled(false);
        }
    }

    private void button4MouseClicked(MouseEvent e) {
        // TODO add your code here
        if (button4.isEnabled()) {
            button4.setEnabled(false);
            button3.setEnabled(true);
            String uriStr = textField1.getText();
            String prefixDir = textField2.getText();
            instance = new HttpDownloadTask(uriStr, prefixDir);
            new Thread(() -> {
                try {
                    instance.resolve();
                } catch (RuntimeException runtimeException) {
                    String message = runtimeException.getMessage();
                    JOptionPane.showMessageDialog(dialogPane, message, "错误", JOptionPane.ERROR_MESSAGE);
                    button4.setEnabled(true);
                    button3.setEnabled(false);
                }
                button4.setEnabled(true);
            }).start();
            new Thread(() -> {
                while (instance.getProgressPercentage() < 100) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(300);
                        int percentage = instance.getProgressPercentage();
                        Map<String, String> statusMap = instance.getStatusMap(0.3f);
                        progressBar1.setValue(percentage);
                        progressBar1.setString(String.format("[%d%%] %s/%s", percentage, statusMap.get("currentSize"), statusMap.get("totalSize")));
                        textField4.setText(statusMap.get("speed") + "/s" + " ,[" + instance.getStatus().description + "]" + ", ETA:" + statusMap.get("time"));
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(dialogPane,instance.getStatus().description, "提示", JOptionPane.INFORMATION_MESSAGE);
                button3.setEnabled(false);
                button4.setEnabled(true);
            }).start();
        }
    }

    private void button1MouseClicked(MouseEvent e) {
        // TODO add your code here
        try {
            new URL(textField1.getText());
            JOptionPane.showMessageDialog(dialogPane, "URL有效", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (MalformedURLException e1) {
            JOptionPane.showMessageDialog(dialogPane, "URL无效", "提示", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void button5MouseClicked(MouseEvent e) {
        // TODO add your code here
        String prefixDir = textField2.getText();
        try {
            Runtime.getRuntime().exec("explorer " + prefixDir);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        label1 = new JLabel();
        textField1 = new JTextField();
        label2 = new JLabel();
        textField2 = new JTextField();
        button2 = new JButton();
        label3 = new JLabel();
        progressBar1 = new JProgressBar();
        button3 = new JButton();
        label4 = new JLabel();
        checkBox1 = new JCheckBox();
        button1 = new JButton();
        textField4 = new JTextField();
        button4 = new JButton();
        button5 = new JButton();

        //======== this ========
        setTitle("[Lazyzzz] \u7f51\u7edc\u8d44\u6e90\u4e0b\u8f7d\u5668");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- label1 ----
                label1.setText("\u4e0b\u8f7d\u5730\u5740");

                //---- label2 ----
                label2.setText("\u4fdd\u5b58\u8def\u5f84");

                //---- textField2 ----
                textField2.setText(System.getProperty("user.home") + "\\Downloads");
                textField2.setEditable(false);

                //---- button2 ----
                button2.setText("\u9009\u62e9");
                button2.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 10));
                button2.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        button2MouseClicked(e);
                    }
                });

                //---- label3 ----
                label3.setText("\u4e0b\u8f7d\u8bbe\u7f6e");

                //---- progressBar1 ----
                progressBar1.setStringPainted(true);

                //---- button3 ----
                button3.setText("\u6682\u505c");
                button3.setEnabled(false);
                button3.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        button3MouseClicked(e);
                    }
                });

                //---- label4 ----
                label4.setText("\u4e0b\u8f7d\u8fdb\u5ea6");

                //---- checkBox1 ----
                checkBox1.setText("\u5f00\u542f\u591a\u7ebf\u7a0b");
                checkBox1.setSelected(true);

                //---- button1 ----
                button1.setText("\u9a8c\u8bc1\u8fde\u63a5");
                button1.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        button1MouseClicked(e);
                    }
                });

                //---- textField4 ----
                textField4.setText("\u6682\u65e0\u4e0b\u8f7d\u4fe1\u606f");
                textField4.setHorizontalAlignment(SwingConstants.CENTER);
                textField4.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 12));
                textField4.setEditable(false);

                //---- button4 ----
                button4.setText("\u5f00\u59cb");
                button4.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        button4MouseClicked(e);
                    }
                });

                //---- button5 ----
                button5.setText("\u6253\u5f00");
                button5.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 10));
                button5.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        button5MouseClicked(e);
                    }
                });

                GroupLayout contentPanelLayout = new GroupLayout(contentPanel);
                contentPanel.setLayout(contentPanelLayout);
                contentPanelLayout.setHorizontalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGap(16, 16, 16)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                .addComponent(label1, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label2, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label3, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label4, GroupLayout.PREFERRED_SIZE, 64, GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addComponent(checkBox1)
                                        .addComponent(textField2, GroupLayout.PREFERRED_SIZE, 236, GroupLayout.PREFERRED_SIZE)
                                        .addComponent(textField1, GroupLayout.PREFERRED_SIZE, 236, GroupLayout.PREFERRED_SIZE))
                                    .addGap(12, 12, 12)
                                    .addGroup(contentPanelLayout.createParallelGroup()
                                        .addGroup(contentPanelLayout.createSequentialGroup()
                                            .addComponent(button2, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                                            .addComponent(button5, GroupLayout.PREFERRED_SIZE, 55, GroupLayout.PREFERRED_SIZE))
                                        .addComponent(button3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(button1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addComponent(progressBar1, GroupLayout.PREFERRED_SIZE, 236, GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(button4, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGroup(GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(textField4, GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE))
                );
                contentPanelLayout.setVerticalGroup(
                    contentPanelLayout.createParallelGroup()
                        .addGroup(contentPanelLayout.createSequentialGroup()
                            .addGap(26, 26, 26)
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(label1)
                                .addComponent(textField1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                .addComponent(button1))
                            .addGap(18, 18, 18)
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                    .addComponent(textField2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                                    .addComponent(button2)
                                    .addComponent(button5))
                                .addComponent(label2))
                            .addGroup(contentPanelLayout.createParallelGroup()
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                        .addComponent(label3)
                                        .addComponent(checkBox1))
                                    .addGap(8, 8, 8))
                                .addGroup(contentPanelLayout.createSequentialGroup()
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(button3)
                                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(contentPanelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(progressBar1, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                .addComponent(label4)
                                .addComponent(button4))
                            .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(textField4, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
                            .addContainerGap())
                );
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel label1;
    private JTextField textField1;
    private JLabel label2;
    private JTextField textField2;
    private JButton button2;
    private JLabel label3;
    private JProgressBar progressBar1;
    private JButton button3;
    private JLabel label4;
    private JCheckBox checkBox1;
    private JButton button1;
    private JTextField textField4;
    private JButton button4;
    private JButton button5;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
