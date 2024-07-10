package org.openmrs.module.cag.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.cag.api.CagService;
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
import org.openmrs.module.webservices.rest.web.resource.api.Searchable;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.List;

@Resource(name = RestConstants.VERSION_1 + CagController.CAG_VISIT_NAMESPACE, supportedClass = CagVisit.class, supportedOpenmrsVersions = {
        "1.8.*", "2.1.*", "2.4.*" })
@Component
public class CagVisitResource extends DelegatingCrudResource<CagVisit> implements Searchable {
	
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		System.out.println("propertiesToUpdate.toString():\n" + propertiesToUpdate.get("dateStopped").toString() + "\n");
		try {
			return getService().closeCagVisit(uuid, propertiesToUpdate.get("dateStopped").toString());
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("dateStopped");
		
		return description;
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<CagVisit>(getService().getCagAllVisits(), context);
	}
	
	@Override
	public CagVisit getByUniqueId(String uuid) {
		System.out.println("Get CagVisit By Uuid been called !!!");
		CagVisit cagVisit = getService().getCagVisitByUuid(uuid);
		System.out.println("Our cagVisit is here : " + cagVisit);
		
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
	protected PageableResult doSearch(RequestContext context) {
		
		String attenderUuid = context.getParameter("attenderuuid");
		Boolean isActive = Boolean.valueOf(context.getParameter("isactive"));
		
		System.out.println("uuid : " + attenderUuid + " , isactive :" + isActive);
		
		List<CagVisit> searchedCagVisits = getService().searchCagVisits(attenderUuid, isActive);
		
		return new NeedsPaging<CagVisit>(searchedCagVisits, context);
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
			description.addProperty("isActive");
			description.addProperty("dateStopped");
			description.addProperty("absentees");
			description.addProperty("cag", Representation.REF);
			description.addProperty("attender", Representation.REF);
			description.addProperty("visits", Representation.REF);
			
			description.addLink("full", ".?v=full");
		} else if (representation instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			
			description.addProperty("uuid");
			description.addProperty("dateStarted");
			description.addProperty("isActive");
			description.addProperty("dateStopped");
			description.addProperty("absentees");
			description.addProperty("cag", Representation.REF);
			description.addProperty("attender", Representation.REF);
			description.addProperty("visits", Representation.FULL);
			
			description.addSelfLink();
		} else {
			description = new DelegatingResourceDescription();
			
			description.addProperty("uuid");
			description.addProperty("dateStarted");
			description.addProperty("isActive");
			description.addProperty("dateStopped");
			description.addProperty("absentees");
			description.addProperty("cag", Representation.REF);
			description.addProperty("attender", Representation.REF);
			description.addProperty("visits", Representation.FULL);
			
			description.addLink("full", ".?v=full");
		}
		
		return description;
	}
	
	private CagService getService() {
		return Context.getService(CagService.class);
	}
}
