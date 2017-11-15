package uk.nhs.careconnect.ri.daointerface.transforms;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.careconnect.ri.entity.BaseAddress;
import uk.nhs.careconnect.ri.entity.procedure.ProcedureEntity;
import uk.nhs.careconnect.ri.entity.procedure.ProcedurePerformer;
import uk.org.hl7.fhir.core.Stu3.CareConnectProfile;

@Component
public class ProcedureEntityToFHIRProcedureTransformer implements Transformer<ProcedureEntity, Procedure> {

    private final Transformer<BaseAddress, Address> addressTransformer;

    public ProcedureEntityToFHIRProcedureTransformer(@Autowired Transformer<BaseAddress, Address> addressTransformer) {
        this.addressTransformer = addressTransformer;
    }

    @Override
    public Procedure transform(final ProcedureEntity procedureEntity) {
        final Procedure procedure = new Procedure();

        Meta meta = new Meta().addProfile(CareConnectProfile.Procedure_1);

        if (procedureEntity.getUpdated() != null) {
            meta.setLastUpdated(procedureEntity.getUpdated());
        }
        else {
            if (procedureEntity.getCreated() != null) {
                meta.setLastUpdated(procedureEntity.getCreated());
            }
        } 
        procedure.setMeta(meta);

        procedure.setId(procedureEntity.getId().toString());

        if (procedureEntity.getPatient() != null) {
            procedure
                    .setSubject(new Reference("Patient/"+procedureEntity.getPatient().getId())
                    .setDisplay(procedureEntity.getPatient().getNames().get(0).getDisplayName()));
        }
        if (procedureEntity.getContextEncounter() !=null) {
            procedure.setContext(new Reference("Encounter/"+procedureEntity.getContextEncounter().getId()));
        } else if (procedureEntity.getContextEpisode() !=null) {
            procedure.setContext(new Reference("EpisodeOfCare/"+procedureEntity.getContextEpisode().getId()));
        }
        if (procedureEntity.getNotDone() !=null) {
            procedure.setNotDone(procedureEntity.getNotDone());
        }
        if (procedureEntity.getBasedOnReferral() != null) {
            procedure.addBasedOn().setReference("ReferralRequest/"+procedureEntity.getContextEpisode().getId());
        }
        if (procedureEntity.getBodySite()!= null) {
            procedure.addBodySite().addCoding()
                    .setSystem(procedureEntity.getBodySite().getSystem())
                    .setCode(procedureEntity.getBodySite().getCode())
                    .setDisplay(procedureEntity.getBodySite().getDisplay());
        }
        if (procedureEntity.getCategory() != null) {
            procedure.getCategory().addCoding()
                    .setSystem(procedureEntity.getCategory().getSystem())
                    .setCode(procedureEntity.getCategory().getCode())
                    .setDisplay(procedureEntity.getCategory().getDisplay());
        }
        if (procedureEntity.getCode() != null) {
            procedure.getCode().addCoding()
                    .setSystem(procedureEntity.getCode().getSystem())
                    .setCode(procedureEntity.getCode().getCode())
                    .setDisplay(procedureEntity.getCode().getDisplay());
        }
        if (procedureEntity.getLocation() != null) {
            procedure
                    .setLocation(new Reference("Location/"+procedureEntity.getLocation().getId()))
                    .getLocation()
                        .setDisplay(procedureEntity.getLocation().getName());
        }
        if (procedureEntity.getNotDoneReason() !=null) {
            procedure.getNotDoneReason().addCoding()
                    .setSystem(procedureEntity.getNotDoneReason().getSystem())
                    .setCode(procedureEntity.getNotDoneReason().getCode())
                    .setDisplay(procedureEntity.getNotDoneReason().getDisplay());
        }
        if (procedureEntity.getReason() != null) {
            procedure.addReasonCode().addCoding()
                    .setSystem(procedureEntity.getReason().getSystem())
                    .setCode(procedureEntity.getReason().getCode())
                    .setDisplay(procedureEntity.getReason().getDisplay());
        }
        if (procedureEntity.getReasonObservation() != null) {
            procedure.addReasonReference(new Reference("Observation/"+procedureEntity.getReasonObservation().getId()));
        } else if (procedureEntity.getReasonCondition() != null) {
            procedure.addReasonReference(new Reference("Condition/"+procedureEntity.getReasonCondition().getId()));
        }

        if (procedureEntity.getPerformedDate() != null) {
            procedure.setPerformed(new DateTimeType().setValue(procedureEntity.getPerformedDate()));
        }
        if (procedureEntity.getStatus() != null) {
            procedure.setStatus(procedureEntity.getStatus());
        }

        for (ProcedurePerformer performer : procedureEntity.getPerformers()) {
            Reference actor = null;
            if (performer.getActorOrganisation() != null) {
                actor = new Reference("Organisation/"+performer.getActorOrganisation().getId());
                actor.setDisplay(performer.getActorOrganisation().getName());
            } else if (performer.getActorPractioner() != null) {
                actor = new Reference("Practitioner/"+performer.getActorPractioner().getId());
                actor.setDisplay(performer.getActorPractioner().getNames().get(0).getDisplayName());
            }

            Procedure.ProcedurePerformerComponent performerComponent = procedure.addPerformer();
            performerComponent.setActor(actor);
            if (performer.getOnBehalfOrganisation() != null) {
                performerComponent.setOnBehalfOf(new Reference("Organisation/"+performer.getOnBehalfOrganisation().getId()));
            }
        }

        return procedure;

    }
}
