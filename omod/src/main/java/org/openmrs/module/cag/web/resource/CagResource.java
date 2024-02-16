package org.openmrs.module.cag.web.resource;

import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.cag.api.CagService;
import org.openmrs.module.cag.cag.Cag;
import org.openmrs.module.cag.web.controller.CagController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.api.Updatable;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Resource(name = RestConstants.VERSION_1 + CagController.CAG_NAMESPACE, supportedClass = Cag.class, supportedOpenmrsVersions = {
        "1.8.*", "2.1.*", "2.4.*" })
@Component
public class CagResource extends DelegatingCrudResource<Cag> implements Updatable {
	
	@Override
	public Cag getByUniqueId(String uuid) {
		Cag cag = getService().getCagByUuid(uuid);
		List<Patient> cagPatientList = getService().getCagPatientList(cag.getId());
		cag.setCagPatientList(cagPatientList);
		
		return cag;
	}
	
	@Override
	protected void delete(Cag cag, String reason, RequestContext requestContext) throws ResponseException {
		getService().deleteCag(cag.getUuid());
	}
	
	@Override
	public Cag newDelegate() {
		return new Cag();
	}
	
	@Override
	public Cag save(Cag cag) {
		
		getService().saveCag(cag);
		
		return getService().getCagByUuid(cag.getUuid());
	}
	
	@Override
	public void purge(Cag cag, RequestContext requestContext) throws ResponseException {
		getService().deleteCag(cag.getUuid());
	}
	
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		System.out.println("update has been called!!!!!!!!!!!");
		
		Cag updatingCag = new Cag();
		updatingCag.setUuid(uuid);
		updatingCag.setCreator(Context.getAuthenticatedUser());
		updatingCag.setName(propertiesToUpdate.get("name").toString());
		updatingCag.setDescription(propertiesToUpdate.get("description").toString());
		updatingCag.setConstituency(propertiesToUpdate.get("constituency").toString());
		updatingCag.setVillage(propertiesToUpdate.get("village").toString());
		updatingCag.setDistrict(propertiesToUpdate.get("district").toString());
		
		return getService().updateCag(updatingCag);
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("name");
		description.addProperty("description");
		description.addProperty("constituency");
		description.addProperty("village");
		description.addProperty("district");
		description.addProperty("date_changed");
		
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("name");
		description.addProperty("description");
		description.addProperty("constituency");
		description.addProperty("village");
		description.addProperty("district");
		description.addProperty("cagPatientList");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = null;
		
		if (representation instanceof DefaultRepresentation) {
			description = new DelegatingResourceDescription();
			
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("village");
			description.addProperty("constituency");
			description.addProperty("district");
			description.addProperty("dateCreated");
			description.addProperty("cagPatientList", Representation.REF);
			
			description.addLink("full", ".?v=full");
		} else if (representation instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			
			description.addProperty("uuid");
			description.addProperty("name");
			description.addProperty("description");
			description.addProperty("village");
			description.addProperty("constituency");
			description.addProperty("district");
			description.addProperty("dateCreated");
			description.addProperty("cagPatientList", Representation.REF);
			description.addProperty("creator", Representation.REF);
			
			description.addSelfLink();
		} else if (representation instanceof RefRepresentation) {
			description = new DelegatingResourceDescription();
			
			description.addProperty("uuid");
			description.addProperty("name");
			
			description.addLink("full", ".?v=full");
		}
		
		return description;
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		
		return new NeedsPaging<Cag>(getService().getCagList(), context);
	}
	
	private CagService getService() {
		return Context.getService(CagService.class);
	}
}
