package io.vgs.track.data;

import java.util.Objects;

public class EntityTrackingFieldData {

    private final String name;
    private final Object oldValue;
    private Object newValue;

    public EntityTrackingFieldData(final String name, final Object oldValue, final Object newValue) {
        this.name = name;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getName() {
        return name;
    }

    public Object getOldValue() {
        return oldValue;
    }

    public Object getNewValue() {
        return newValue;
    }

    public void setNewValue(final Object newValue) {
        this.newValue = newValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityTrackingFieldData)) {
            return false;
        }
        final EntityTrackingFieldData that = (EntityTrackingFieldData) o;
        return Objects.equals(this.name, that.name);
    }

    @Override
    public String toString() {
        return "EntityTrackingFieldData{" + "name='" + name + '\'' + ", oldValue=" + oldValue + ", newValue=" + newValue
                + '}';
    }
}
