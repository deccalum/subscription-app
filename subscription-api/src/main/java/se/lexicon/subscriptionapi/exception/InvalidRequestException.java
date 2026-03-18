package se.lexicon.subscriptionapi.exception;

public class InvalidRequestException extends RuntimeException {
    public InvalidRequestException() {
        super("Invalid request");
    }

    public InvalidRequestException(String message) {
        super(message);
    }

    public Object withErrorCode(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'withErrorCode'");
    }
}
