package RPG;

public class ActionKey {
	//--- キーのモード
	// キーが押されている間はisPressed()はtrueを返す
	public static final int NORMAL = 0;
	// キーが初めに押された時だけisPressed()はtrueを返す
	// キーが押され続けても2回目以降はfalseを返す
	// このモードを使うと会話スクロールボタンを押し続けても連続してスクロールしない(detect:検出する)
	public static final int DETECT_INITIAL_PRESS_ONLY = 1;

	//--- キーの状態
	// キーが離された
	private static final int STATE_RELEASED = 0;
	// キーが押されている
	private static final int STATE_PRESSED = 1;
	// キーが離されるのを待っている
	private static final int STATE_WAITING_FOR_RELEASE = 2;

	// キーのモードを入れる変数
	private int mode;
	// キーが押された回数
	private int amount;
	// キーの状態を入れる変数
	private int state;

	// 普通のコンストラクタではノーマルモード
	public ActionKey() {
		this(NORMAL);
	}

	// モードを指定できるコンストラクタ
	public ActionKey(int mode) {
		this.mode = mode;
		reset();    // キーのリセット
	}

	// キーの状態をリセット
	public void reset() {
		state = STATE_RELEASED;
		amount = 0;
	}

	// キーが押された時呼び出す
	public void press() {
		// STATE_WAITING_FOR_RELEASEの時は押されたことにならない
		if ( state != STATE_WAITING_FOR_RELEASE ) {
			amount++;
			state = STATE_PRESSED;
		}
	}

	// キーが離されたとき呼び出す
	public void release() {
		state = STATE_RELEASED;
	}

	// キーが押されたか
	public boolean isPressed() {
		if ( amount != 0 ) {
			if ( state == STATE_RELEASED ) {
				amount = 0;
			} else if ( mode == DETECT_INITIAL_PRESS_ONLY ) {
				state = STATE_WAITING_FOR_RELEASE;
				amount = 0;
			}
			return true;
		}
		return false;
	}
}