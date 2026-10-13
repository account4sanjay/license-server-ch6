package com.optimagrowth.license.controller;

import static java.util.Collections.singletonMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.optimagrowth.license.model.utils.ErrorMessage;
import com.optimagrowth.license.model.utils.ResponseWrapper;
import com.optimagrowth.license.model.utils.RestErrorList;

import feign.FeignException;

/**
 * Maps exceptions to HTTP responses. Every error is rendered as a
 * {@link ResponseWrapper} whose HTTP status matches the failure, so that a
 * missing license or organization is reported as 404 rather than 200.
 *
 * @author ihuaylupo
 * @since Jun 28, 2018
 */
@ControllerAdvice
@EnableWebMvc
public class ExceptionController extends ResponseEntityExceptionHandler {

	/** A license id/organization id pair that does not exist in this service. */
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseWrapper> handleNotFound(HttpServletRequest request, IllegalArgumentException e) {
		return error(HttpStatus.NOT_FOUND, e.getMessage());
	}

	/** The organization service answered 404 to a Feign call. */
	@ExceptionHandler(FeignException.NotFound.class)
	public ResponseEntity<ResponseWrapper> handleFeignNotFound(HttpServletRequest request, FeignException.NotFound e) {
		return error(HttpStatus.NOT_FOUND, "Organization not found: " + request.getRequestURI());
	}

	/** The organization service answered 404 to a RestTemplate / DiscoveryClient call. */
	@ExceptionHandler(HttpClientErrorException.NotFound.class)
	public ResponseEntity<ResponseWrapper> handleRestNotFound(HttpServletRequest request, HttpClientErrorException.NotFound e) {
		return error(HttpStatus.NOT_FOUND, "Organization not found: " + request.getRequestURI());
	}

	/** Anything else is an internal error. */
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ResponseWrapper> handleRuntimeException(HttpServletRequest request, RuntimeException e) {
		return error(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
	}

	private static ResponseEntity<ResponseWrapper> error(HttpStatus status, String message) {
		RestErrorList errorList = new RestErrorList(status, new ErrorMessage(message, message));
		ResponseWrapper responseWrapper = new ResponseWrapper(null, singletonMap("status", status), errorList);
		return ResponseEntity.status(status).body(responseWrapper);
	}
}
