package pl.inder00.opensource.sectors.basic;

import java.util.Collection;

/**
 * Equivalent of std::variant as data manager.
 * Unused at this moment.
 * Will be used as future protobuf data manager.
 */
public interface IMixedManager<T, Y> {

    /**
     * Returns collection of data
     *
     * @return Collection
     */
    <A extends T> Collection<A> getDataCollection();

    /**
     * Returns count of data
     *
     * @return int
     */
    int getDataCount();

    /**
     * Returns data by key
     *
     * @param key Key
     * @return Data
     */
    <A extends T, B extends Y> A getByKey(B key);

    /**
     * Saves data by key
     *
     * @param data Data
     * @param key  Key
     */
    <A extends T, B extends Y> void save(A data, B key);

    /**
     * Deletes data by key
     *
     * @param key Key
     */
    <B extends Y> void delete(B key);

}
