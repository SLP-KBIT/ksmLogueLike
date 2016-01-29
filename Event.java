package RPG;

public abstract class Event {
    // x座標
    private int x;
    // y座標
    private int y;
    // アイテム名
    protected String itemName;    // privateにしてしまうとサブクラスからアクセスできない！
    // チップ番号
    protected int chipNo;
    // ぶつかるかどうか
    protected boolean isHit;

    public Event(int x, int y, int chipNo, boolean isHit) {
        this.x = x;
        this.y = y;
        this.chipNo = chipNo;
        this.isHit = isHit;
    }

    //--- アイテム名を返す
    public String getItemName() {
        return itemName;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
}
