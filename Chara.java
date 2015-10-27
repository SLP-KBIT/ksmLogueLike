package RPG;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Chara implements Common {
	// キャラの移動スピード
	private static final int SPEED = 2;

	// キャラのイメージ
    private Image image;

    // キャラの位置
    private int x, y;      // 単位：マス
    private int px, py;    // 単位：ピクセル

    // キャラの向いている方向(LEFT,RIGHT,UP,DOWNのどれか)
    private int direction;
    // キャラのアニメーションカウンタ
    private int count;

    // 移動中か判定する変数
    private boolean isMoving = false;
    // 移動中の場合の移動ピクセル数
    private int movingLength;

    // キャラクターアニメーション用スレッド
    private Thread threadAnime;

    // マップへの参照
    private Mapp map;

    public Chara(int x, int y, String filename, Mapp map) {
    	this.x = x;
    	this.y = y;

    	px = x * CS;
    	py = y * CS;

    	direction = DOWN;
    	count = 0;

    	this.map = map;

    	// イメージをロード
    	loadImage(filename);

        // キャラクターアニメーション用スレッド開始
        threadAnime = new Thread(new AnimationThread());
        threadAnime.start();
    }

    private void loadImage(String filename) {
    	// キャラの画像を取得
    	ImageIcon icon = new ImageIcon(getClass().getResource(filename));
    	image = icon.getImage();
    }

    public void drawChara(Graphics g, int offsetX, int offsetY) {
    	// countとdirectionの値に応じて表示する画像を切り替える
    	g.drawImage(image, px - offsetX, py - offsetY, px - offsetX + CS, py - offsetY + CS,
    			count * CS, direction * CS, count * CS + CS, direction * CS + CS, null);
    }

    public boolean move() {
    	switch ( direction ) {
    	case LEFT :
    		if ( moveLeft() == true ) { return true; }    // 移動が完了したらtrueを返す
    		break;
    	case RIGHT :
    		if ( moveRight() == true ) { return true; }
    		break;
    	case UP :
    		if ( moveUp() == true ) { return true; }
       		break;
    	case DOWN :
    		if ( moveDown() == true ) { return true; }
    		break;
    	}

    	return false;    // 移動が完了していない
    }

    //--- 左へ移動する
    // 移動中はfalseを返し、1マス移動が完了したらtrueを返す
    private boolean moveLeft() {
    	// 1マス先の座標
    	int nextX = x - 1;
    	int nextY = y;

    	if ( nextX < 0 ) { nextX = 0; }

    	// 移動先に障害物がなければ移動開始
    	if ( map.isHit(nextX, nextY) == false ) {
    		// SPEEDピクセル分移動
    		px -= SPEED;
    		if ( px < 0 ) { px = 0; }
    		// 移動距離を加算
    		movingLength += SPEED;
    		// 移動距離が1マス分を超えたら
    		if ( movingLength >= CS ) {
    			x--;
    			if ( x < 0 ) { x = 0; }
    			// 移動が完了
    			isMoving = false;
    			return true;
    		}
    	} else {
    		isMoving = false;
    		// 元の位置に戻す
    		px = x * CS;
    		py = y * CS;
    	}

    	return false;
    }

    //--- 右へ移動する
    // 移動中はfalseを返し、1マス移動が完了したらtrueを返す
    private boolean moveRight() {
    	// 1マス先の座標
    	int nextX = x + 1;
    	int nextY = y;

    	if ( nextX > Mapp.COL - 1 ) { nextX = Mapp.COL - 1; }

    	// 移動先に障害物がなければ移動開始
    	if ( map.isHit(nextX, nextY) == false ) {
    		// SPEEDピクセル分移動
    		px += SPEED;
    		if ( px > Mapp.WIDTH - CS ) { px = Mapp.WIDTH - CS; }
    		// 移動距離を加算
    		movingLength += SPEED;
    		// 移動距離が1マス分を超えたら
    		if ( movingLength >= CS ) {
    			x++;
    			if ( x > Mapp.COL - 1 ) { x = Mapp.COL - 1; }
    			// 移動が完了
    			isMoving = false;
    			return true;
    		}
    	} else {
    		isMoving = false;
    		// 元の位置に戻す
    		px = x * CS;
    		py = y * CS;
    	}

    	return false;
    }

    //--- 上へ移動する
    // 移動中はfalseを返し、1マス移動が完了したらtrueを返す
    private boolean moveUp() {
    	// 1マス先の座標
    	int nextX = x;
    	int nextY = y - 1;

    	if ( nextY < 0 ) { nextY = 0; }

    	// 移動先に障害物がなければ移動開始
    	if ( map.isHit(nextX, nextY) == false ) {
    		// SPEEDピクセル分移動
    		py -= SPEED;
    		if ( py < 0 ) { py = 0; }
    		// 移動距離を加算
    		movingLength += SPEED;
    		// 移動距離が1マス分を超えたら
    		if ( movingLength >= CS ) {
    			y--;
    			if ( y < 0 ) { y = 0; }
    			// 移動が完了
    			isMoving = false;
    			return true;
    		}
    	} else {
    		isMoving = false;
    		// 元の位置に戻す
    		px = x * CS;
    		py = y * CS;
    	}

    	return false;
    }

    //--- 下へ移動する
    // 移動中はfalseを返し、1マス移動が完了したらtrueを返す
    private boolean moveDown() {
    	// 1マス先の座標
    	int nextX = x;
    	int nextY = y + 1;

    	if ( nextY > Mapp.ROW - 1 ) { nextY = Mapp.ROW - 1; }

    	// 移動先に障害物がなければ移動開始
    	if ( map.isHit(nextX, nextY) == false ) {
    		// SPEEDピクセル分移動
    		py += SPEED;
    		if ( py > Mapp.HEIGHT - CS ) { py = Mapp.HEIGHT - CS; }
    		// 移動距離を加算
    		movingLength += SPEED;
    		// 移動距離が1マス分を超えたら
    		if ( movingLength >= CS ) {
    			y++;
    			if ( y > Mapp.ROW - 1 ) { y = Mapp.ROW - 1; }
    			// 移動が完了
    			isMoving = false;
    			return true;
    		}
    	} else {
    		isMoving = false;
    		// 元の位置に戻す
    		px = x * CS;
    		py = y * CS;
    	}

    	return false;
    }

    public int getX() {
    	return x;
    }
    public int getY() {
    	return y;
    }
    public int getPx() {
    	return px;
    }
    public int getPy() {
    	return py;
    }

    public boolean isMoving() {
    	return isMoving;
    }

    public void setDirection(int dir) {
    	direction = dir;
    }

    public void setMoving(boolean flag) {
    	isMoving = flag;
    	movingLength = 0;
    }

    public class AnimationThread extends Thread {
    	public void run() {
    		while ( true ) {
    			// countを切り替える
    			if ( count == 0 ) {
    				count = 1;
    			} else if ( count == 1 ) {
    				count = 0;
    			}

    			// AnimationThreadスレッドを300ミリ秒休止(300ミリ秒おきに勇者の画像を切り替える)
    			try {
    				Thread.sleep(200);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
}
