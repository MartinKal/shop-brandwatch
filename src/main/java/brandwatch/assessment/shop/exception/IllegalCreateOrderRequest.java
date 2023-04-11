package brandwatch.assessment.shop.exception;

public class IllegalCreateOrderRequest extends RuntimeException{

    public IllegalCreateOrderRequest(String message) {
        super(message);
    }
}
