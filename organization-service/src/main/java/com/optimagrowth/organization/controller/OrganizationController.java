package com.optimagrowth.organization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.service.OrganizationService;

@RestController
@RequestMapping(value = "v1/organization")
public class OrganizationController {

	@Autowired
	private OrganizationService service;

	/** 200 with the organization, or 404 when the id does not exist. */
	@GetMapping(value = "/{organizationId}")
	public ResponseEntity<Organization> getOrganization(@PathVariable("organizationId") String organizationId) {
		return ResponseEntity.ok(service.findById(organizationId));
	}

	/** 200 with the updated organization, or 404 when the id does not exist. */
	@PutMapping(value = "/{organizationId}")
	public ResponseEntity<Organization> updateOrganization(@PathVariable("organizationId") String organizationId,
			@RequestBody Organization organization) {
		return ResponseEntity.ok(service.update(organizationId, organization));
	}

	@PostMapping
	public ResponseEntity<Organization> saveOrganization(@RequestBody Organization organization) {
		return ResponseEntity.ok(service.create(organization));
	}

	/** 204 when deleted, or 404 when the id does not exist. */
	@DeleteMapping(value = "/{organizationId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteOrganization(@PathVariable("organizationId") String organizationId) {
		service.delete(organizationId);
	}
}
