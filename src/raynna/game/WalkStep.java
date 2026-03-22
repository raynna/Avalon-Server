package raynna.game;

public class WalkStep {
    public final int dir;
    public final int x;
    public final int y;
    public final boolean check;

    public WalkStep(int dir, int x, int y, boolean check) {
        this.dir = dir;
        this.x = x;
        this.y = y;
        this.check = check;
    }
}