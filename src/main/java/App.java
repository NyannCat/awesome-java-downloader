import ui.DownloadView;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        DownloadView view = new DownloadView();
        view.setVisible(true);
        view.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
