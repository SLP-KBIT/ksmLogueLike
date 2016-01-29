package RPG;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;

public class MessageWindow {
    // 白枠の幅
    private static final int EDGE_WIDTH = 2;

    // 行間の大きさ
    protected static final int LINE_HEIGHT = 8;
    // 1行の最大文字数
    private static final int MAX_CHAR_IN_LINE = 20;
    // 1ページに表示できる最大行数
    private static final int MAX_LINES = 3;
    // 1ページに表示できる最大文字数
    private static final int MAX_CHAR_IN_PAGE = MAX_CHAR_IN_LINE * MAX_LINES;

    // 一番外側の枠
    private Rectangle rect;
    // 一つ内側の枠(白い枠線ができるように)
    private Rectangle innerRect;
    // 実際にテキストを描画する枠
    private Rectangle textRect;

    // メッセージウィンドウを表示中かどうか
    private boolean isVisible = false;

    // カーソルのアニメーションGIF
    private Image cursorImage;

    // メッセージを格納する配列
    private char[] text = new char[128 * MAX_CHAR_IN_LINE];
    // 最後のページ
    private int finalPage;
    // 現在表示しているページ
    private int currentPage = 0;
    // 表示した文字数
    private int currentPos;
    // 次のページへ行けるか(▼が表示されていればtrue)
    private boolean nextFlag = false;

    // メッセージエンジン
    private MessageEngine messageEngine;

    // テキストを流すタイマータスク
    private Timer timer;
    private TimerTask task;

    public MessageWindow(Rectangle rect) {
        this.rect = rect;

        innerRect = new Rectangle(
                rect.x + EDGE_WIDTH,
                rect.y + EDGE_WIDTH,
                rect.width - EDGE_WIDTH * 2,
                rect.height - EDGE_WIDTH * 2);

        textRect = new Rectangle(
                innerRect.x + 16,
                innerRect.y + 16,
                320,
                120);

        // メッセージエンジンを作成
        messageEngine = new MessageEngine();

        // カーソルイメージをロード
        ImageIcon icon = new ImageIcon(getClass().getResource("image/cursor.gif"));
        cursorImage = icon.getImage();

        timer = new Timer();
    }

    public void draw(Graphics g) {
        if ( isVisible == false ) { return; }

        // 外枠を描く
        g.setColor(Color.WHITE);
        g.fillRect(rect.x, rect.y, rect.width, rect.height);    // 白で塗りつぶす

        // 内枠を描く
        g.setColor(Color.BLACK);
        g.fillRect(innerRect.x, innerRect.y, innerRect.width, innerRect.height);    // 黒で塗りつぶす

        // 現在のページ(currentPage)のcurrentPosまでの内容を表示
        for ( int i = 0; i < currentPos; i++ ) {
            char c = text[currentPage * MAX_CHAR_IN_PAGE + i];
            int dx = textRect.x + MessageEngine.FONT_WIDTH * ( i % MAX_CHAR_IN_LINE );
            int dy = textRect.y + ( LINE_HEIGHT + MessageEngine.FONT_HEIGHT ) * ( i / MAX_CHAR_IN_LINE );
            messageEngine.drawCharacter(dx, dy, c, g);
        }

        // 最後のページでない & 1ページ分の表示が終わったならページの下部に矢印を表示する
        if ( currentPage < finalPage && nextFlag == true ) {
            int dx = textRect.x + ( MAX_CHAR_IN_LINE / 2 ) * MessageEngine.FONT_WIDTH - 8;
            int dy = textRect.y + ( LINE_HEIGHT + MessageEngine.FONT_HEIGHT ) * 3;
            g.drawImage(cursorImage, dx, dy, null);
        }
    }

    //--- メッセージをセットする
    public void setMessage(String msg) {
        currentPos = 0;
        currentPage = 0;
        nextFlag = false;

        // 全角スペースで初期化
        for ( int i = 0; i < text.length; i++ ) {
            text[i] = '　';
        }

        int p = 0;    // 処理中の文字位置
        for ( int i = 0; i < msg.length(); i++ ) {
            char c = msg.charAt(i);
            if ( c == '\\' ) {
                i++;
                if ( msg.charAt(i) == 'n' ) {    // 改行
                    p += MAX_CHAR_IN_LINE;
                    p = ( p / MAX_CHAR_IN_LINE ) * MAX_CHAR_IN_LINE;
                } else if ( msg.charAt(i) == 'f' ) {    // 改ページ
                    p += MAX_CHAR_IN_PAGE;
                    p = ( p / MAX_CHAR_IN_PAGE ) * MAX_CHAR_IN_PAGE;
                }
            } else {
                text[p++] = c;
            }
        }
        finalPage = p / MAX_CHAR_IN_PAGE;

        // 文字を流すタスクを起動
        task = new DrawingMessageTask();
        timer.schedule(task, 0L, 20L);
    }

    //--- 次のページに進める
    // @return メッセージが終了したらtrueを返す
    public boolean nextMessage() {
        // 現在ページが最後のページならメッセージを終了する
        if ( currentPage == finalPage  ) {
            // タスクは終了しないと動き続ける
            task.cancel();
            task = null;
            return true;
        }
        // ▼が表示されているなら次ページへ進む
        if ( nextFlag == true ) {
            currentPage++;
            currentPos = 0;
            nextFlag = false;
        }
        return false;
    }

    // ウィンドウを表示
    public void show() {
        isVisible = true;
    }

    // ウィンドウを隠す
    public void hide() {
        isVisible = false;
    }

    // ウィンドウを表示中かどうか
    public boolean isVisible() {
        return isVisible;
    }

    //--- メッセージを1文字ずつ順に描画するタスク
    class DrawingMessageTask extends TimerTask {
        public void run() {
            if ( nextFlag == false ) {
                currentPos++;    // 1文字増やす
                // 1ページの文字数に達したら▼を表示
                if ( currentPos % MAX_CHAR_IN_PAGE == 0 ) {
                    nextFlag = true;
                }
            }
        }
    }
}
