package com.optimagrowth.organization.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an organization id does not exist. Mapped to HTTP 404 so that
 * callers (including the licensing service) can tell "not found" from success.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class OrganizationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OrganizationNotFoundException(String organizationId) {
		super(String.format("Organization with id %s not found", organizationId));
	}
}
