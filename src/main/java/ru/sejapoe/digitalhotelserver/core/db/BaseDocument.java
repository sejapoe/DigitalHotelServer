package ru.sejapoe.digitalhotelserver.core.db;

import org.springframework.data.annotation.Id;

import java.math.BigInteger;

public abstract class BaseDocument {
    @Id
    private BigInteger id;

    /**
     * Returns the identifier of the document.
     *
     * @return the id
     */
    public BigInteger getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }

        if (this.id == null || obj == null || !(this.getClass().equals(obj.getClass()))) {
            return false;
        }

        BaseDocument that = (BaseDocument) obj;

        return this.id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
