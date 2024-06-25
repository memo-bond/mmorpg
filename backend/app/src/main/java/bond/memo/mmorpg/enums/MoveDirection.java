package bond.memo.mmorpg.enums;

public enum MoveDirection {
    LEFT(270), RIGHT(90), UP(0), DOWN(180);

    MoveDirection(int degree) {
        this.degree = degree;
    }
    private int degree;
    public int degree() {
        return degree;
    }
}
