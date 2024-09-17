package kr.co.backend.exception;

import org.aspectj.weaver.ast.Not;

public class NotEnoughStockException extends RuntimeException{

    public NotEnoughStockException(String message){
        super(message);
    }
    
}
