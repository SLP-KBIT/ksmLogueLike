package RPG;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Chara implements Common {
    // メンバ変数(フィールド)
	// キャラの移動スピード
	private static final int SPEED = 2;

	// キャラの移動確率
	public static final double PROB_MOVE = 0.02;

	// キャラのイメージ(静的変数)
    private static Image charaImage;    // staticがつくと、同じCharaクラスのインスタンス(hero、skeleton、lizard)で共通(共有)の変数となる

    // キャラクター番号(インスタンス変数)
    private int charaNo;    // staticがないフィールドは、同じCharaクラスでも、インスタンスごとに生成される(hero.charaNoとskeleton.charaNoは違う)

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

    // 移動タイプ
    private int moveType;
    // 話すメッセージ
    private String message;

    // キャラクターアニメーション用スレッド
    private Thread threadAnime;

    // マップへの参照
    private Mapp map;

    public Chara(int x, int y, int charaNo, int direction, int moveType, Mapp map) {
    	this.x = x;
    	this.y = y;

    	px = x * CS;
    	py = y * CS;

    	this.charaNo = charaNo;
    	this.direction = direction;
    	count = 0;
    	this.moveType = moveType;
    	this.map = map;

    	// 初回の呼び出しのみイメージをロード
    	if ( charaImage == null ) {
    	    loadImage();
    	}

        // キャラクターアニメーション用スレッド開始
        threadAnime = new Thread(new AnimationThread());
        threadAnime.start();
    }

    private void loadImage() {
    	// キャラのイメージをロード
    	ImageIcon icon = new ImageIcon(getClass().getResource("image/chara.gif"));
    	charaImage = icon.getImage();
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
        int cx = ( charaNo % 8 ) * ( CS * 2 );
        int cy = ( charaNo / 8 ) * ( CS * 4 );
    	// countとdirectionの値に応じて表示する画像を切り替える
    	g.drawImage(charaImage,
    	    px - offsetX, py - offsetY,
    	    px - offsetX + CS, py - offsetY + CS,
    		cx + count * CS, cy + direction * CS,
    		cx + count * CS + CS, cy + direction * CS + CS, null);
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

    	if ( nextX > map.getCol() - 1 ) { nextX = map.getCol() - 1; }

    	// 移動先に障害物がなければ移動開始
    	if ( map.isHit(nextX, nextY) == false ) {
    		// SPEEDピクセル分移動
    		px += SPEED;
    		if ( px > map.getWidth() - CS ) { px = map.getWidth() - CS; }
    		// 移動距離を加算
    		movingLength += SPEED;
    		// 移動距離が1マス分を超えたら
    		if ( movingLength >= CS ) {
    			x++;
    			if ( x > map.getCol() - 1 ) { x = map.getCol() - 1; }
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

    	if ( nextY > map.getRow() - 1 ) { nextY = map.getRow() - 1; }

    	// 移動先に障害物がなければ移動開始
    	if ( map.isHit(nextX, nextY) == false ) {
    		// SPEEDピクセル分移動
    		py += SPEED;
    		if ( py > map.getHeight() - CS ) { py = map.getHeight() - CS; }
    		// 移動距離を加算
    		movingLength += SPEED;
    		// 移動距離が1マス分を超えたら
    		if ( movingLength >= CS ) {
    			y++;
    			if ( y > map.getRow() - 1 ) { y = map.getRow() - 1; }
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

    //--- 勇者が向いている方向の隣にキャラクターがいるか調べる
    // @return キャラクターがいたらそのCharaオブジェクトを返す
    public Chara talkWith() {
        int nextX = 0;
        int nextY = 0;
        // キャラクターの向いている方向の一歩隣の座標
        switch ( direction ) {
        case LEFT :
            nextX = x - 1;
            nextY = y;
            break;
        case RIGHT :
            nextX = x + 1;
            nextY = y;
            break;
        case UP :
            nextX = x;
            nextY = y - 1;
            break;
        case DOWN :
            nextX = x;
            nextY = y + 1;
            break;
        }
        // その方向にキャラクターがいるか調べる
        Chara chara = map.charaCheck(nextX, nextY);
        // キャラクターがいれば話しかけた勇者の方に向ける
        if ( chara != null ) {
            switch ( direction ) {
            case LEFT :
                chara.setDirection(RIGHT);
                break;
            case RIGHT :
                chara.setDirection(LEFT);
                break;
            case UP :
                chara.setDirection(DOWN);
                break;
            case DOWN :
                chara.setDirection(UP);
                break;
            }
        }
        return chara;
    }

    //--- 足元にアイテムがあるか調べる
    // @return 足元にあるEventオブジェクト
    public Event search() {
        Event event = map.eventCheck(x, y);
        return event;
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

    // キャラクターのメッセージを取得する
    public String getMessage() {
        return message;
    }

    // キャラクターのメッセージを登録する
    public void setMessage(String message) {
        this.message = message;
    }

    // キャラクターの移動タイプを取得する
    public int getMoveType() {
        return moveType;
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
    				Thread.sleep(300);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    }
}
