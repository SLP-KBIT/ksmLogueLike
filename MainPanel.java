package RPG;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.Vector;

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
    private ActionKey spaceKey;

    // ゲームループスレッド
    private Thread gameLoop;

    // 乱数生成器
    private Random rand = new Random();

    // ウィンドウ
    private MessageWindow messageWindow;
    // ウィンドウを表示する領域
    private static Rectangle WND_RECT = new Rectangle(62, 324, 356, 140);

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
        spaceKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);

        // マップを生成
        map = new Mapp("map/map.dat", "event/event.dat", this);

        // 勇者を生成
        hero = new Chara(1, 1, 0, DOWN, 0, map);

        // マップにキャラクターを登録
        // キャラクターはマップに属する
        map.addChara(hero);

        // ウィンドウを追加
        messageWindow = new MessageWindow(WND_RECT);

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
    	if ( offsetX > map.getWidth() - MainPanel.WIDTH ) {
    		offsetX = map.getWidth() - MainPanel.WIDTH;    // マップの右端ではスクロールしないようにする
    	}

    	// Y方向のオフセット(距離)を計算
    	int offsetY = hero.getPy() - ( MainPanel.HEIGHT / 2 );

    	if ( offsetY < 0 ) {
    		offsetY = 0;    // マップの上端ではスクロールしないようにする
    	}
    	if ( offsetY > map.getHeight() - MainPanel.HEIGHT ) {
    		offsetY = map.getHeight() - MainPanel.HEIGHT;    // マップの下端ではスクロールしないようにする
    	}

    	// マップを描く
    	// キャラクターはマップを描いてくれる(キャラクターはマップに属するため)
    	map.draw(g, offsetX, offsetY);

    	// メッセージウィンドウを描画
    	messageWindow.draw(g);
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
        case KeyEvent.VK_SPACE :
            spaceKey.press();
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
        case KeyEvent.VK_SPACE :
            spaceKey.release();
            break;
    	}
    }

    public void keyTyped(KeyEvent e) {
    }

    // ゲームループ
    public void run() {
    	while ( true ) {
    		// キー入力をチェックする
    		if ( messageWindow.isVisible() == true ) {    // メッセージウィンドウ表示中なら
    		    messageWindowCheckInput();
    		} else {
    		    mainWindowCheckInput();    // メイン画面のキー入力チェック
    		}

    		if ( messageWindow.isVisible() == false ) {
    		    // 勇者の移動処理
    		    heroMove();
    		    // 勇者以外のキャラクターの移動処理
    		    charaMove();
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

    // メインウィンドウでのキー入力をチェックする
    public void mainWindowCheckInput() {
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
        if ( spaceKey.isPressed() == true ) {
            if ( hero.isMoving() == true ) { return; }    // 移動中は表示できない

            // しらべる
            Event item = hero.search();
            if ( item != null ) {
                // メッセージをセットする
                messageWindow.setMessage(item.getItemName() + "　を　てにいれた。");
                // メッセージウィンドウを表示
                messageWindow.show();
                // ここにアイテム入手処理を入れる

                // 取得したアイテムを削除
                map.removeEvent(item);
                return;    // しらべた場合ははなさない
            }

            // はなす
            if ( messageWindow.isVisible() == false ) {
                Chara chara = hero.talkWith();
                if ( chara != null ) {
                    // メッセージをセットする
                    messageWindow.setMessage(chara.getMessage());
                } else {
                    messageWindow.setMessage("そのほうこうには　だれもいない。");
                }
                messageWindow.show();    // メッセージウィンドウを表示
            }
        }
    }

    //--- メッセージウィンドウでのキー入力をチェックする
    private void messageWindowCheckInput() {
        if ( spaceKey.isPressed() == true ) {
            if ( messageWindow.nextMessage() == true ) {    // 次のメッセージへ
                messageWindow.hide();    // 終了したら隠す
            }
        }
    }

    //--- 勇者の移動処理
    private void heroMove() {
        // 移動中なら移動する
        if ( hero.isMoving() == true ) {
            if ( hero.move() == true ) {    // 移動する
                // 移動が完了した場合の処理をここに書く
            }
        }
    }

    //--- 勇者以外のキャラクターの移動処理
    private void charaMove() {
        // マップにいるキャラクターを取得
        Vector<Chara> charas = map.getCharas();
        for ( int i = 0; i < charas.size(); i++ ) {
            Chara chara = (Chara)charas.get(i);
            // キャラクターの移動タイプを調べる
            if ( chara.getMoveType() == 1 ) {    // 移動するタイプなら
                if ( chara.isMoving() == true ) {    // 移動中なら
                    chara.move();    // 移動する
                } else if ( rand.nextDouble() < Chara.PROB_MOVE ) {
                    // 移動していない場合はChara.PROB_MOVEの確率で移動する
                    // 移動する方向はランダムに決める
                    chara.setDirection(rand.nextInt(4));
                    chara.setMoving(true);
                }
            }
        }

    }
}
