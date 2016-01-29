package RPG;

import java.awt.Container;

import javax.swing.JFrame;

public class Rpg extends JFrame {

    public Rpg() {
        // タイトルを設定
        setTitle("さすらいのマイケル");

        // メインパネルを作成してフレームに追加
        MainPanel panel = new MainPanel();
        Container contentPane = getContentPane();
        contentPane.add(panel);

        // パネルサイズに合わせてフレームサイズを自動設定
        pack();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Rpg();
    }
}
