package RPG;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Mapp implements Common {
    // 行数
    public static final int ROW = 20;
    // 列数
    public static final int COL = 30;

    // マップ全体の大きさ(単位：ピクセル)
    public static int WIDTH = COL * CS;
    public static int HEIGHT = ROW * CS;

    // マップ(0:床、1:壁)
    private int[][] map = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };

    // マップチップ
    private Image floorImage;
    private Image wallImage;

    // メインパネルへの参照
    private MainPanel panel;

    public Mapp(MainPanel panel) {
    	this.panel = panel;

    	// イメージをロード
    	loadImage();
    }

    private void loadImage() {
    	// 床の画像を取得
    	ImageIcon icon = new ImageIcon(getClass().getResource("map_c02-edit.gif"));
    	floorImage = icon.getImage();

    	// 壁の画像を取得
    	icon = new ImageIcon(getClass().getResource("wall.gif"));
    	wallImage = icon.getImage();
    }

    public void drawMap(Graphics g, int offsetX, int offsetY) {
    	// オフセットを元に描画範囲を求める
    	int firstTileX = pixelsToTiles(offsetX);
    	int lastTileX = firstTileX + pixelsToTiles(MainPanel.WIDTH) + 1;
    	// 描画範囲がマップの大きさより大きくならないように調整
    	lastTileX = Math.min(lastTileX, COL);

    	int firstTileY = pixelsToTiles(offsetY);
    	int lastTileY = firstTileY + pixelsToTiles(MainPanel.HEIGHT) + 1;
    	// 描画範囲がマップの大きさより大きくならないように調整
    	lastTileY = Math.min(lastTileY, ROW);

    	for ( int i = firstTileY; i < lastTileY; i++ ) {
    		for ( int j = firstTileX; j < lastTileX; j++ ) {
    			switch ( map[i][j] ) {    // mapの値に応じて画像を描く
    			case 0 :
    				g.drawImage(floorImage, tilesToPixels(j) - offsetX, tilesToPixels(i) - offsetY, panel);    // 床を描く
    				break;
    			case 1 :
    				g.drawImage(wallImage, tilesToPixels(j) - offsetX, tilesToPixels(i) - offsetY, panel);    // 壁を描く
    				break;
    			}
    		}
    	}
    }

    public boolean isHit(int x, int y) {
    	if ( map[y][x] == 1 ) { return true; }    // 壁ならtrueを返す
    	return false;
    }

    public static int pixelsToTiles(double pixels) {
    	return (int)Math.floor(pixels / CS);
    }

    public static int tilesToPixels(int tiles) {
    	return tiles * CS;
    }
}
