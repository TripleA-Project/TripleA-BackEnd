package com.triplea.triplea.core.exception;

import com.triplea.triplea.dto.ResponseDTO;
import lombok.Getter;
import org.springframework.http.HttpStatus;


// 권한 없음
@Getter
public class Exception429 extends RuntimeException {
    public Exception429(String message) {
        super(message);
    }

    public ResponseDTO<?> body(){
        return new ResponseDTO<>(HttpStatus.TOO_MANY_REQUESTS, "OpenAI Quota or Rate Limit Exiceed", getMessage());
    }

    public HttpStatus status(){
        return HttpStatus.NOT_FOUND;
    }
}