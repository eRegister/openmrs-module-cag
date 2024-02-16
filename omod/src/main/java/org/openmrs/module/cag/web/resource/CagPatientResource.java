package org.openmrs.module.cag.web.resource;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.cag.api.CagService;
import org.openmrs.module.cag.cag.CagPatient;
import org.openmrs.module.cag.web.controller.CagController;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

@Resource(name = RestConstants.VERSION_1 + CagController.CAG_PATIENT_NAMESPACE, supportedClass = CagPatient.class, supportedOpenmrsVersions = {
        "1.8.*", "2.1.*", "2.4.*" })
public class CagPatientResource extends DelegatingCrudResource<CagPatient> {
	
	@Override
	protected void delete(CagPatient cagPatient, String reason, RequestContext requestContext) throws ResponseException {
		System.out.println("Deleting a Patient from Cag!!!!");
		getService().deletePatientFromCag(cagPatient.getUuid());
	}
	
	@Override
	public CagPatient newDelegate() {
		return new CagPatient();
	}
	
	@Override
	public CagPatient save(CagPatient cagPatient) {
		
		CagPatient existOnCag = getService().getCagPatientByUuid(cagPatient.getUuid());
		System.out.println(existOnCag);
		
		Patient patient = getService().saveCagPatient(cagPatient);
		
		return getService().getCagPatientById(patient.getPatientId());
	}
	
	@Override
	public void purge(CagPatient cagPatient, RequestContext requestContext) throws ResponseException {
		getService().deletePatientFromCag(cagPatient.getCagUuid());
	}
	
	@Override
	public CagPatient getByUniqueId(String uuid) {
		
		System.out.println("getByUniqueId id being called!!!");
		CagPatient cagPatient = getService().getActiveCagVisitByAttender(uuid);
		
		return cagPatient;
	}
	
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("cag");
		description.addProperty("patient");
		return description;
	}
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation representation) {
		DelegatingResourceDescription description = null;
		
		if (representation instanceof DefaultRepresentation) {
			description = new DelegatingResourceDescription();
			
			description.addProperty("activeCagVisits");
			
			description.addSelfLink();
			description.addLink("full", ".?v=full");
		} else if (representation instanceof FullRepresentation) {
			description = new DelegatingResourceDescription();
			
			description.addProperty("activeCagVisits");
			
			description.addSelfLink();
		} else {
			
			description.addProperty("activeCagVisits");
			
			description.addSelfLink();
		}
		
		return description;
	}
	
	//        @Override
	//        protected PageableResult doGetAll(RequestContext context) throws ResponseException {
	//            Cag cag =new Cag(10);
	//            return new NeedsPaging<CagPatient>(getService().getCagPatientList(10), context);
	//        }
	
	private CagService getService() {
		return Context.getService(CagService.class);
	}
	
}
