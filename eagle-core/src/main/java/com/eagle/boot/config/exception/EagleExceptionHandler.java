package com.eagle.boot.config.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class EagleExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(EagleExceptionHandler.class);

    private final MappingJackson2JsonView jsonView = new MappingJackson2JsonView();

    @ResponseBody
    @ExceptionHandler(EagleException.class)
    public ModelAndView handleEagleException(EagleException eagleException, HttpServletRequest request,
                                                     HttpServletResponse response) {
        response.setStatus(eagleException.getHttpResponseCode());

        return handleException(eagleException, request);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception exception, HttpServletRequest request) {
        return handleException(exception, request);
    }

    private ModelAndView handleException(Exception exception, HttpServletRequest request) {
        Exception loggedException = LOGGER.isDebugEnabled() ? exception : null;

        LOGGER.error("Request: {} raised exception: {}", request.getRequestURL(), exception.toString(), loggedException);

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("exceptionMessage", exception.getMessage());
        responseBody.put("httpMethod", request.getMethod());
        return new ModelAndView(jsonView, responseBody);
    }
}
