package RPG;

public class WeaponEvent extends Event {

    public WeaponEvent(int x, int y, String itemName) {
        // 武器のチップ番号は3 & ぶつからない
        super(x, y, 3, false);
        this.itemName = itemName;
    }
}
