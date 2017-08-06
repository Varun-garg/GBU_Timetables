package com.varun.gbu_timetables.data.Model;

public class PairKey {

    private final int x;
    private final int y;

    public PairKey(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PairKey)) return false;
        PairKey key = (PairKey) o;
        return x == key.x && y == key.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

}
