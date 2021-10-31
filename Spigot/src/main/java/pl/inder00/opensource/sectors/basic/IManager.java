package pl.inder00.opensource.sectors.basic;

import java.util.Collection;

public interface IManager<T, Y> {

    /**
     * Returns collection of data
     *
     * @return Collection
     */
    Collection<T> getDataCollection();

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
    T getByKey(Y key);

    /**
     * Saves data by key
     *
     * @param data Data
     * @param key  Key
     */
    void save(T data, Y key);

    /**
     * Deletes data by key
     *
     * @param key Key
     */
    void delete(Y key);

}
