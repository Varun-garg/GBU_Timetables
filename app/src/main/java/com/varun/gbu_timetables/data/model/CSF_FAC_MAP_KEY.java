package com.varun.gbu_timetables.data.model;

public class CSF_FAC_MAP_KEY {

    private final Long x;
    private final Long y;

    public CSF_FAC_MAP_KEY(Long CSF_id, Long FAC_ID) {
        this.x = CSF_id;
        this.y = FAC_ID;
    }

    public Long getCSF_id() {
        return x;
    }

    public Long getFAC_ID() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CSF_FAC_MAP_KEY)) return false;
        CSF_FAC_MAP_KEY key = (CSF_FAC_MAP_KEY) o;
        return x.longValue() == key.x.longValue() && y.longValue() == key.y.longValue();
    }

    @Override
    public int hashCode() {
        int result = x.intValue();
        result = 31 * result + y.intValue();
        return result;
    }

}
