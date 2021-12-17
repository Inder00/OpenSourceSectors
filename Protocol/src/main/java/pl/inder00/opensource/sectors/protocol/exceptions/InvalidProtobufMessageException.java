package pl.inder00.opensource.sectors.protocol.exceptions;

public class InvalidProtobufMessageException extends Exception {

    public InvalidProtobufMessageException() {
        super();
    }

    public InvalidProtobufMessageException(String message) {
        super(message);
    }
}
