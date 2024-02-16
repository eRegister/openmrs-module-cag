package org.openmrs.module.cag.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.cag.api.CagService;
import org.openmrs.module.cag.cag.Cag;
import org.openmrs.module.cag.cag.CagVisit;
import org.openmrs.module.cag.web.controller.CagController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + CagController.CAG_VISIT_NAMESPACE, supportedClass = CagVisit.class, supportedOpenmrsVersions = {
        "1.8.*", "2.1.*", "2.4.*" })
@Component
public class CagVisitResource extends DelegatingCrudResource<CagVisit> {
	
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		System.out.println("propertiesToUpdate.toString():\n" + propertiesToUpdate.get("dateStopped") + "\n");
		return getService().closeCagVisit(uuid, propertiesToUpdate.get("dateStopped").toString());
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("dateStopped");
		
		return description;
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<CagVisit>(getService().getCagVisitList(), context);
	}
	
	@Override
	public CagVisit getByUniqueId(String uuid) {
		System.out.println("Get CagVisit By Uuid been called !!!");
		CagVisit cagVisit = getService().getCagVisitByUuid(uuid);
		System.out.println("Our cagVisit : " + cagVisit);
		
		return cagVisit;
	}
	
	@Override
	protected void delete(CagVisit cagVisit, String s, RequestContext requestContext) throws ResponseException {
		System.out.println("DELETE CagVisit has been called : uuid=" + cagVisit.getUuid());
		getService().deleteCagVisit(cagVisit.getUuid());
	}
	
	@Override
	public void purge(CagVisit cagVisit, RequestContext requestContext) throws ResponseException {
		
	}
	
	@Override
	public CagVisit newDelegate() {
		return new CagVisit();
	}
	
	@Override
	public CagVisit save(CagVisit cagVisit) {
		return getService().openCagVisit(cagVisit);
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		
		description.addProperty("cag");
		description.addProperty("dateStarted");
		description.addProperty("attender");
		description.addProperty("absentees");
		description.addProperty("visits");
		description.addProperty("locationName");
		
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = null;
		
		if (representation instanceof DefaultRepresentation) {
			description = new DelegatingResourceDescription();
			
			description.addProperty("uuid");
			description.addProperty("dateStarted");
			description.addProperty("dateStopped");
			description.addProperty("cag", Representation.REF);
			description.addProperty("attender", Representation.REF);
			description.addProperty("visits");
			//			description.addProperty("attenderVisit");
			//			description.addProperty("otherMemberVisits");
			description.addProperty("absentees");
			
			description.addLink("full", ".?v=full");
		} else if (representation instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			
			description.addProperty("uuid");
			description.addProperty("cag", Representation.REF);
			description.addProperty("attender", Representation.REF);
			description.addProperty("dateStarted");
			description.addProperty("dateStopped");
			description.addProperty("visits");
			//			description.addProperty("attenderVisit");
			//			description.addProperty("otherMemberVisits");
			description.addProperty("absentees");
			
			description.addSelfLink();
		} else {
			description = new DelegatingResourceDescription();
			
			description.addProperty("uuid");
			description.addProperty("dateStarted");
			description.addProperty("dateStopped");
			description.addProperty("cag", Representation.REF);
			description.addProperty("attender", Representation.REF);
			description.addProperty("visits");
			//			description.addProperty("attenderVisit");
			//			description.addProperty("otherMemberVisits");
			description.addProperty("absentees");
			
			description.addLink("full", ".?v=full");
		}
		
		return description;
	}
	
	private CagService getService() {
		return Context.getService(CagService.class);
	}
}
