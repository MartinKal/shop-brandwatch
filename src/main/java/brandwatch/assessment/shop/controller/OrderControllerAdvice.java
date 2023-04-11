package brandwatch.assessment.shop.controller;

import brandwatch.assessment.shop.exception.IllegalCreateOrderRequest;
import brandwatch.assessment.shop.exception.OrderCouldNotBeCompleted;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class OrderControllerAdvice {
    private static final String MISSING_REQUEST_BODY = "Required request body is missing";

    @ExceptionHandler(IllegalCreateOrderRequest.class)
    public ResponseEntity<String> handleIllegalCreateOrderRequest(IllegalCreateOrderRequest ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadableException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(MISSING_REQUEST_BODY);
    }

    @ExceptionHandler(OrderCouldNotBeCompleted.class)
    public ResponseEntity<String> handleOrderCouldNotBeCompleted(OrderCouldNotBeCompleted ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
