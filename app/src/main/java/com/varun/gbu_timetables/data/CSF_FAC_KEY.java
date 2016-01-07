package com.varun.gbu_timetables.data;

public class CSF_FAC_KEY {

    private final Long x;
    private final Long y;

    public CSF_FAC_KEY(Long CSF_id, Long FAC_ID) {
        this.x = CSF_id;
        this.y = FAC_ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Key)) return false;
        CSF_FAC_KEY key = (CSF_FAC_KEY) o;
        return x == key.x && y == key.y;
    }

    @Override
    public int hashCode() {
        int result = x.intValue();
        result = 31 * result + y.intValue();
        return result;
    }

}
