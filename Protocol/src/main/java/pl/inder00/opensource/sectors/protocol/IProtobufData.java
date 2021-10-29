package pl.inder00.opensource.sectors.protocol;

public interface IProtobufData<T, Y> {

    /**
     * Returns a protobuf data
     *
     * @return Protobuf data
     */
    T getData();

    /**
     * Executes protobuf data
     *
     * @param var Execuction var
     */
    void execute(Y var);

}
