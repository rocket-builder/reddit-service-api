package com.anthill.ofhelperredditmvc;

import com.anthill.ofhelperredditmvc.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UsernameNotFoundException.class,
            ResourceNotFoundedException.class
    })
    public ResponseEntity<String> notFound(Exception ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            IncorrectPasswordException.class,
            LoginAlreadyTakenException.class,
            ResourceAlreadyExists.class,
            IncorrectGoogleSheetUrlException.class,
            RedditAccountScrapperException.class,
            IncorrectSessionTemplateException.class,
            NoRedditAccountsException.class,
            GoogleSheetsAccessException.class,
            GoogleSheetsReadException.class,
            GroupAlreadyStartedException.class,
            GroupAlreadyStoppedException.class,
            NoCompatibleRedditAccountsException.class,
            NotEnoughRedditAccountsException.class,
            IncorrectInputDataException.class,
            UnknownSearchFieldException.class,
            GoogleSheetsRowsNotFoundException.class,
            SessionServiceParseSessionException.class,
            InsufficientUpVoteBalanceException.class,
            BadCredentialsException.class,
            UserBannedException.class
    })
    public ResponseEntity<String> badRequest(Exception ex){
        ex.printStackTrace();
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            AccessDeniedException.class
    })
    public ResponseEntity<String> forbidden(Exception ex){
        ex.printStackTrace();
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({
            NoUseragentsInDatabaseException.class,
            IllegalStateException.class
    })
    public ResponseEntity<String> internalServerError(Exception ex){
        ex.printStackTrace();
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({
            RedditBotServiceException.class,
            TelegramBotServiceException.class
    })
    public ResponseEntity<String> serviceNotAvailable(Exception ex){
        ex.printStackTrace();
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
