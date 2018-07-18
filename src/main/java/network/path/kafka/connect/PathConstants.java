package network.path.kafka.connect;

public class PathConstants {

    /**
     * Dynamo DB surprisingly does not allow empty strings as values of attributes.
     * https://forums.aws.amazon.com/thread.jspa?threadID=90137
     */
    public static final String EMPTY_STRING_FOR_DYNAMO = " ";

}
