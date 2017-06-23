/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.anahata.malefemalesvgdrawing.svg.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author sai.dandem
 */
public abstract class Base implements Serializable {
    private Long id;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Base other = (Base)obj;

        if (id == null || other.id == null) {
            return false;
        }

        return id.equals(other.id);
    }

    @Override
    public int hashCode() {
        if (id == null) {
            return super.hashCode();
        }

        int hash = 7;
        hash = 97 * hash + id.hashCode();
        return hash;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": id=" + id + " ref=" + System.identityHashCode(this);
    }

    /**
     * Gets all ids of a collection of objects whose type extends Base.
     *
     * @param col the collection
     * @return the ids
     */
    public static long[] getIds(Collection<? extends Base> col) {
        long[] ret = new long[col.size()];
        int idx = 0;
        for (Base base : col) {
            ret[idx++] = base.getId();
        }
        return ret;
    }

    /**
     * Gets all ids of a collection of objects whose type extends Base.
     *
     * @param col the collection
     * @return the ids
     */
    public static List<Long> getIdsList(Collection<? extends Base> col) {
        List<Long> ret = new ArrayList<>(col.size());
        for (Base base : col) {
            ret.add(base.getId());
        }
        return ret;

    }

    /**
     * Finds an entity by id in a list.
     *
     * @param <T>  the type
     * @param list the list
     * @param id   the id
     * @return the element in the list or null if not found
     */
    public static <T extends Base> T findById(List<T> list, Long id) {
        for (T t : list) {
            if (Objects.equals(t.getId(), id)) {
                return t;
            }
        }
        return null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
