package pl.inder00.opensource.sectors.commons.concurrent;

public interface FutureCallback<T> {

    /**
     * Future callback
     * 
     * @param data Mixed data
     */
    void execute(T data);
    
}
