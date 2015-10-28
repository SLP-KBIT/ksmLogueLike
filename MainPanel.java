package RPG;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;

public class MainPanel extends JPanel implements KeyListener, Runnable, Common {
    // パネルサイズ
    public static final int WIDTH = 480;
    public static final int HEIGHT = 480;

    // マップ
    private Mapp map;

    // 勇者
    private Chara hero;

    // アクションキー
    private ActionKey leftKey;
    private ActionKey rightKey;
    private ActionKey upKey;
    private ActionKey downKey;

    // ゲームループスレッド
    private Thread gameLoop;

    public MainPanel() {
        // パネルの推奨サイズを設定
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        // パネルがキー操作を受け付けるように登録する
        setFocusable(true);
        addKeyListener(this);

        // アクションキーを生成
        leftKey = new ActionKey();
        rightKey = new ActionKey();
        upKey = new ActionKey();
        downKey = new ActionKey();

        // マップを生成
        map = new Mapp(this);

        // 勇者を生成
        hero = new Chara(1, 1, "Hero.gif", map);

        // ゲームループの生成と開始
        gameLoop = new Thread(this);
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
    	super.paintComponent(g);

    	// X方向のオフセット(距離)を計算
    	int offsetX = hero.getPx() - ( MainPanel.WIDTH / 2 );

    	if ( offsetX < 0 ) {
    		offsetX = 0;    // マップの左端ではスクロールしないようにする
    	}
    	if ( offsetX > Mapp.WIDTH - MainPanel.WIDTH ) {
    		offsetX = Mapp.WIDTH - MainPanel.WIDTH;    // マップの右端ではスクロールしないようにする
    	}

    	// Y方向のオフセット(距離)を計算
    	int offsetY = hero.getPy() - ( MainPanel.HEIGHT / 2 );

    	if ( offsetY < 0 ) {
    		offsetY = 0;    // マップの上端ではスクロールしないようにする
    	}
    	if ( offsetY > Mapp.HEIGHT - MainPanel.HEIGHT ) {
    		offsetY = Mapp.HEIGHT - MainPanel.HEIGHT;    // マップの下端ではスクロールしないようにする
    	}

    	// マップを描く
    	map.drawMap(g, offsetX, offsetY);

    	// 勇者を描く
    	hero.drawChara(g, offsetX, offsetY);
    }

    // キーが押されたらキーの状態を「押された」に変える
    public void keyPressed(KeyEvent e) {
    	// 押されたキーを調べて取得
    	int KeyCode = e.getKeyCode();

    	switch ( KeyCode ) {
    	case KeyEvent.VK_LEFT :
    		leftKey.press();
    		break;
    	case KeyEvent.VK_RIGHT :
    		rightKey.press();
    		break;
    	case KeyEvent.VK_UP :
    		upKey.press();
    		break;
    	case KeyEvent.VK_DOWN :
    		downKey.press();
    		break;
    	}
    }

    // キーが離されたらキーの状態を「離された」に変える
    public void keyReleased(KeyEvent e) {
    	// 離されたキーを調べて取得
    	int KeyCode = e.getKeyCode();

    	switch ( KeyCode ) {
    	case KeyEvent.VK_LEFT :
    		leftKey.release();
    		break;
    	case KeyEvent.VK_RIGHT :
    		rightKey.release();
    		break;
    	case KeyEvent.VK_UP :
    		upKey.release();
    		break;
    	case KeyEvent.VK_DOWN :
    		downKey.release();
    		break;
    	}
    }

    public void keyTyped(KeyEvent e) {
    }

    // ゲームループ
    public void run() {
    	while ( true ) {
    		// キー入力をチェックする
    		checkInput();

    		// 移動中なら移動する
    		if ( hero.isMoving() == true ) {
    			if ( hero.move() == true ) {    // 移動
    				// 移動が完了した場合の処理をここに書く
    			}
    		}

    		// 再描画
    		repaint();

    		// 休止
    		try {
    			Thread.sleep(20);
    		} catch (InterruptedException  e) {
    			e.printStackTrace();
    		}
    	}
    }

    public void checkInput() {
    	if ( leftKey.isPressed() == true ) {     // キーが押されている
    		if ( hero.isMoving() == false ) {    // 移動中でなければ
    			hero.setDirection(LEFT);         // 方向をセットして
    			hero.setMoving(true);            // 移動開始
    		}
    	}
    	if ( rightKey.isPressed() == true ) {
    		if ( hero.isMoving() == false ) {
    			hero.setDirection(RIGHT);
    			hero.setMoving(true);
    		}
    	}
    	if ( upKey.isPressed() == true ) {
    		if ( hero.isMoving() == false ) {
    			hero.setDirection(UP);
    			hero.setMoving(true);
    		}
    	}
    	if ( downKey.isPressed() == true ) {
    		if ( hero.isMoving() == false ) {
    			hero.setDirection(DOWN);
    			hero.setMoving(true);
    		}
    	}
    }
}
