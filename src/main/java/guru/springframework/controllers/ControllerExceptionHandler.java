package guru.springframework.controllers;

import guru.springframework.exceptions.BadRequestException;
import guru.springframework.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebExchangeBindException;
//import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
public class ControllerExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({WebExchangeBindException.class, NumberFormatException.class})
    public String handleNumberFormatException(Exception exception, Model model) {

        log.error("Handling Number Format Exception");
        log.error(exception.getMessage());

        model.addAttribute("exception", exception);

        return "400error";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({NotFoundException.class})
    public String handleNotFoundException(Exception exception, Model model) {

        log.error("Handling Not Found Exception");
        log.error(exception.getMessage());

        model.addAttribute("exception", exception);

        return "400error";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({BadRequestException.class})
    public String handleBadRequestException(Exception exception, Model model) {

        log.error("Handling Bad Request Exception");
        log.error(exception.getMessage());

        model.addAttribute("exception", exception);

        return "400error";
    }
}
