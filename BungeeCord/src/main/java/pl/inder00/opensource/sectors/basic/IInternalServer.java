package pl.inder00.opensource.sectors.basic;

public interface IInternalServer {

    /**
     * Returns internal server hostname
     *
     * @return String
     */
    String getHostname();

    /**
     * Returns internal server port
     *
     * @return int
     */
    int getPort();

}
