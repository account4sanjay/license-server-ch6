package com.optimagrowth.organization.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when an organization cannot be deleted because licenses still refer
 * to it (foreign key licenses_organization_id_fkey). Mapped to HTTP 409.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class OrganizationInUseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public OrganizationInUseException(String organizationId, Throwable cause) {
		super(String.format("Organization with id %s still has licenses; delete them first", organizationId), cause);
	}
}
