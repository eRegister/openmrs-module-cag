package org.openmrs.module.cag.web.resource;

import org.openmrs.api.context.Context;
import org.openmrs.module.cag.api.CagService;
import org.openmrs.module.cag.cag.CagEncounter;
import org.openmrs.module.cag.web.controller.CagController;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.springframework.expression.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;

@Resource(name = RestConstants.VERSION_1 + CagController.CAG_ENCOUNTER_NAMESPACE, supportedClass = CagEncounter.class, supportedOpenmrsVersions = {
        "1.8.*", "2.1.*", "2.4.*" })
public class CagEncounterResource extends DelegatingCrudResource<CagEncounter> {
	
	@Override
	protected void delete(CagEncounter cagEncounter, String reason, RequestContext requestContext) throws ResponseException {
		getService().deleteCagEncounter(cagEncounter.getUuid());
	}
	
	@Override
	public CagEncounter newDelegate() {
		return new CagEncounter();
	}
	
	@Override
	public CagEncounter save(CagEncounter cagEncounter) {
		return getService().saveCagEncounter(cagEncounter);
	}
	
	@Override
	public void purge(CagEncounter cagEncounter, RequestContext requestContext) throws ResponseException {
		getService().deleteCagEncounter(cagEncounter.getUuid());
	}
	
	@Override
	protected PageableResult doGetAll(RequestContext context) throws ResponseException {
		return new NeedsPaging<CagEncounter>(getService().getAllCagEncounters(), context);
	}
	
	@Override
	protected PageableResult doSearch(RequestContext context) throws ResponseException {
		String cagUuid = context.getParameter("caguuid");
		try {
			return new NeedsPaging<CagEncounter>(getService().searchCagEncounters(cagUuid), context);
		}
		catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public CagEncounter getByUniqueId(String uuid) {
		return getService().getCagEncounterByUuid(uuid);
	}
	
	@Override
	public Object update(String uuid, SimpleObject propertiesToUpdate, RequestContext context) throws ResponseException {
		String locationUuid = propertiesToUpdate.get("location").toString();
		Date nextEncounterDateTime = formatDate(propertiesToUpdate.get("nextEncounterDate").toString());
		return getService().updateCagEncounter(uuid, locationUuid, nextEncounterDateTime);
	}
	
	@Override
	public DelegatingResourceDescription getUpdatableProperties() throws ResourceDoesNotSupportOperationException {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("location");
		description.addProperty("nextEncounterDate");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("cag");
		description.addProperty("cagVisit");
		description.addProperty("location");
		description.addProperty("attender");
		description.addProperty("cagEncounterDateTime");
		description.addProperty("nextEncounterDate");
		description.addProperty("encounters");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = null;
		
		if (representation instanceof RefRepresentation) {
			description = new DelegatingResourceDescription();
			
			description.addProperty("uuid");
			description.addProperty("cagEncounterDateTime");
			description.addProperty("cag", Representation.REF);
			
			description.addLink("full", ".?v=full");
		} else if (representation instanceof DefaultRepresentation) {
			description = new DelegatingResourceDescription();
			
			description.addProperty("uuid");
			description.addProperty("cagEncounterDateTime");
			description.addProperty("nextEncounterDate");
			description.addProperty("cag", Representation.REF);
			description.addProperty("attender", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("encounters", Representation.REF);
			
			description.addSelfLink();
		} else if (representation instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			
			description.addProperty("uuid");
			description.addProperty("cagEncounterDateTime");
			description.addProperty("nextEncounterDate");
			description.addProperty("dateCreated");
			description.addProperty("dateChanged");
			description.addProperty("nextEncounterDate");
			description.addProperty("creator", Representation.REF);
			description.addProperty("changedBy", Representation.REF);
			description.addProperty("cag", Representation.REF);
			description.addProperty("attender", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("encounters", Representation.REF);
			
			description.addLink("full", ".?v=full");
		}
		return description;
	}
	
	private CagService getService() {
		return Context.getService(CagService.class);
	}
	
	private Date formatDate(String dateString) {
		Date date;
		
		try {
			date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
		}
		catch (Exception e) {
			throw new ParseException(1, "Unable to parse provided date, expects format : yyyy-MM-dd HH:mm:ss " + e);
		}
		return date;
	}
	
}
