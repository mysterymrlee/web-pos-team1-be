package com.ssg.webpos.domain;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PosStoreCompositeId implements Serializable {
    private Long pos_id; // (Spring) posId -> (DB) pos_id
    private Long store_id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PosStoreCompositeId posStoreCompositeId = (PosStoreCompositeId) o;
        return Objects.equals(pos_id, posStoreCompositeId.pos_id) && Objects.equals(store_id, posStoreCompositeId.store_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos_id, store_id);
    }

    public Long getPos_id() {
        return pos_id;
    }

    public void setPos_id(Long pos_id) {
        this.pos_id = pos_id;
    }

    public Long getStore_id() {
        return store_id;
    }

    public void setStore_id(Long store_id) {
        this.store_id = store_id;
    }

    @Override
    public String toString() {
        return "PosStoreCompositeId{" +
            "pos_id=" + pos_id +
            ", store_id=" + store_id +
            '}';
    }
}
