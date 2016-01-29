package RPG;

public class RecoveryEvent extends Event {

    public RecoveryEvent(int x, int y, String itemName) {
        // 回復アイテムのチップ番号は2 & ぶつからない
        super(x, y, 2, false);
        this.itemName = itemName;
    }
}
