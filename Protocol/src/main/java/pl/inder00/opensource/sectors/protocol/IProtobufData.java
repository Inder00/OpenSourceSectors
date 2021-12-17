package pl.inder00.opensource.sectors.protocol;

import com.google.protobuf.MessageLite;

public interface IProtobufData<T extends MessageLite, Y> {

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
