package com.optimagrowth.organization.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.optimagrowth.organization.model.Organization;
import com.optimagrowth.organization.repository.OrganizationRepository;

@Service
public class OrganizationService {

	@Autowired
	private OrganizationRepository repository;

	public Organization findById(String organizationId) {
		return repository.findById(organizationId)
				.orElseThrow(() -> new OrganizationNotFoundException(organizationId));
	}

	public Organization create(Organization organization) {
		organization.setId(UUID.randomUUID().toString());
		return repository.save(organization);
	}

	public Organization update(String organizationId, Organization organization) {
		requireExists(organizationId);
		organization.setId(organizationId);
		return repository.save(organization);
	}

	public String delete(String organizationId) {
		requireExists(organizationId);
		try {
			repository.deleteById(organizationId);
		} catch (DataIntegrityViolationException e) {
			throw new OrganizationInUseException(organizationId, e);
		}
		return String.format("Deleting organization with id %s", organizationId);
	}

	private void requireExists(String organizationId) {
		if (!repository.existsById(organizationId)) {
			throw new OrganizationNotFoundException(organizationId);
		}
	}
}
