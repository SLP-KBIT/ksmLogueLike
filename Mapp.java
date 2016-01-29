package RPG;

import java.awt.Graphics;
import java.awt.Image;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.ImageIcon;

public class Mapp implements Common {
    //--- マップの行数と列数(単位：マス)
    private int row;
    private int col;

    // マップ全体の大きさ(単位：ピクセル)
    private int width;
    private int height;

    // マップ
    private int[][] map;

    // マップのイメージ
    private static Image chipImage;

    // このマップにいるキャラクターたち
    private Vector<Chara> charas = new Vector<Chara>();
    // このマップにあるイベント
    private Vector<Event> events = new Vector<Event>();

    // メインパネルへの参照
    private MainPanel panel;

    public Mapp(String mapFile, String eventFile, MainPanel panel) {
    	this.panel = panel;

    	// マップをロード
    	loadMap(mapFile);

    	// イベントをロード
    	loadEvent(eventFile);

    	// 初回呼び出しのみイメージをロード
    	if ( chipImage == null ) {
    	    loadImage();
    	}
    }

    private void loadImage() {
    	// マップチップのイメージをロード
    	ImageIcon icon = new ImageIcon(getClass().getResource("image/mapChip.gif"));
    	chipImage = icon.getImage();
    }

    //--- ファイルからマップを読み込む
    private void loadMap(String fileName) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(getClass().getResourceAsStream(fileName)));
            //--- 行を読み込む
            String line = br.readLine();
            row = Integer.parseInt(line);
            //--- 列を読み込む
            line = br.readLine();
            col = Integer.parseInt(line);
            //--- マップサイズを設定
            width = col * CS;
            height = row * CS;
            //--- マップを作成
            this.map = new int[row][col];
            for ( int i = 0; i < row; i++ ) {
                line = br.readLine();
                for ( int j = 0; j < col; j++ ) {
                    map[i][j] = Integer.parseInt(line.charAt(j)+"");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //--- ファイルからイベントを読み込む
    private void loadEvent(String fileName) {
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(getClass().getResourceAsStream(fileName)));
            String line;
            while ( ( line = br.readLine() ) != null ) {
                // 空行は読み飛ばす
                if ( line.equals("") == true ) { continue; }
                // コメント行は読み飛ばす
                if ( line.startsWith("#") == true ) { continue; }
                // イベント情報を取得する
                StringTokenizer st = new StringTokenizer(line, ",");
                // イベントタイプを取得してイベントごとに処理する
                String eventType = st.nextToken();
                if ( eventType.equals("CHARA") == true ) {    // キャラクターイベントならば
                    makeCharacter(st);
                } else {
                    makeItem(st, eventType);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--- キャラクターイベントを作成
    private void makeCharacter(StringTokenizer st) {
        // イベント(キャラ)の座標
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        // キャラ番号
        int charaNo = Integer.parseInt(st.nextToken());
        // 向き
        int dir = Integer.parseInt(st.nextToken());
        // 移動タイプ
        int moveType = Integer.parseInt(st.nextToken());
        // メッセージ
        String message = st.nextToken();
        // キャラクターを作成
        Chara c = new Chara(x, y, charaNo, dir, moveType, this);
        // メッセージを登録
        c.setMessage(message);
        // キャラクターベクトルに登録
        charas.add(c);
    }

    // アイテムイベントを作成
    private void makeItem(StringTokenizer st, String eventType) {
        // イベント(アイテム)の座標
        int x = Integer.parseInt(st.nextToken());
        int y = Integer.parseInt(st.nextToken());
        // アイテム名
        String itemName = st.nextToken();
        // アイテムイベントを作成
        if ( eventType.equals("RECOVERY") == true ) {
            RecoveryEvent r = new RecoveryEvent(x, y, itemName);
            // イベントベクトルに登録
            events.add(r);
        }
        if ( eventType.equals("WEAPON") == true ) {
            WeaponEvent w = new WeaponEvent(x, y, itemName);
            // イベントベクトルに登録
            events.add(w);
        }
    }

    //--- キャラクターをこのマップに追加する
    public void addChara(Chara chara) {
        charas.add(chara);
    }

    //--- (x, y)にキャラクターがいるか調べる
    // @return (x, y)にいるキャラクター(いなければnull)
    public Chara charaCheck(int x, int y) {
        for ( int i = 0; i < charas.size(); i++ ) {
            Chara chara = (Chara)charas.get(i);
            if ( chara.getX() == x && chara.getY() == y ) {
                return chara;
            }
        }
        return null;
    }

    //--- (x, y)にイベントがあるか調べる
    public Event eventCheck(int x, int y) {
        for ( int i = 0; i < events.size(); i++ ) {
            Event event = (Event)events.get(i);
            if ( event.getX() == x && event.getY()   == y ) {
                return event;
            }
        }
        return null;
    }

    //--- 登録されているイベントを削除する
    public void removeEvent(Event event) {
        events.remove(event);
    }

    public void draw(Graphics g, int offsetX, int offsetY) {
    	// オフセットを元に描画範囲を求める
    	int firstTileX = pixelsToTiles(offsetX);
    	int lastTileX = firstTileX + pixelsToTiles(MainPanel.WIDTH) + 1;
    	// 描画範囲がマップの大きさより大きくならないように調整
    	lastTileX = Math.min(lastTileX, col);

    	int firstTileY = pixelsToTiles(offsetY);
    	int lastTileY = firstTileY + pixelsToTiles(MainPanel.HEIGHT) + 1;
    	// 描画範囲がマップの大きさより大きくならないように調整
    	lastTileY = Math.min(lastTileY, row);

    	for ( int i = firstTileY; i < lastTileY; i++ ) {
    		for ( int j = firstTileX; j < lastTileX; j++ ) {
    		    int mapChipNo = map[i][j];
    		    // イメージ上の位置を決める
    		    int cx = ( mapChipNo % 8 ) * CS;
                int cy = ( mapChipNo / 8 ) * CS;
                g.drawImage(chipImage,
                    tilesToPixels(j) - offsetX, tilesToPixels(i) - offsetY,
                    tilesToPixels(j) - offsetX + CS, tilesToPixels(i) - offsetY + CS,
                    cx, cy,
                    cx + CS, cy +CS,
                    panel);

                // (j, i)にあるイベントを描画
                for ( int n = 0; n < events.size(); n++ ) {
                    Event event = (Event)events.get(n);
                    // イベントが(j, i)にあれば描画
                    if ( event.getX() == j && event.getY() == i ) {
                        mapChipNo = event.chipNo;
                        // イメージ上の位置を決める
                        cx = ( mapChipNo % 8 ) * CS;
                        cy = ( mapChipNo / 8 ) * CS;
                        g.drawImage(chipImage,
                            tilesToPixels(j) - offsetX, tilesToPixels(i) - offsetY,
                            tilesToPixels(j) - offsetX + CS, tilesToPixels(i) - offsetY + CS,
                            cx, cy,
                            cx + CS, cy +CS,
                            panel);
                    }
                }
    		}
    	}
    	// このマップにいるキャラクターを描画
    	for ( int n = 0; n < charas.size(); n++ ) {
    	    Chara chara = (Chara)charas.get(n);
    	    chara.draw(g, offsetX, offsetY);
    	}
    }

    //--- (x,y)にぶつかるものがあるか調べる
    public boolean isHit(int x, int y) {
        //--- 壁ならtrueを返す(ぶつかる)
    	if ( map[y][x] == 1 ) { return true; }

    	//---他のキャラクターがいたらぶつかる
    	for ( int i = 0; i < charas.size(); i++ ) {
    	    Chara chara = (Chara)charas.get(i);
    	    if ( x == chara.getX() && y == chara.getY() ) {
    	        return true;
    	    }
    	}

    	// ぶつかるイベントがあるか
        for ( int i = 0; i < events.size(); i++ ) {
            Event event = (Event)events.get(i);
            if ( x == event.getX() && y == event.getY() ) {
                return event.isHit;
            }
        }

        // なければぶつからない
    	return false;
    }

    //--- ピクセル単位をマス単位に変更する
    public static int pixelsToTiles(double pixels) {
    	return (int)Math.floor(pixels / CS);
    }

    //--- マス単位をピクセル単位に変更する
    public static int tilesToPixels(int tiles) {
    	return tiles * CS;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public Vector<Chara> getCharas() {
        return charas;
    }
}
