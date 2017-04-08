package com.eagle.boot.ws;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@RequestMapping(value="/error")
public final class EagleErrorEndpoint implements ErrorController {

	@Override
	public String getErrorPath() {
		return "/error";
	}
	
	
}
