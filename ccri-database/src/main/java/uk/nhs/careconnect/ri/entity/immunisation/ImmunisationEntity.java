package uk.nhs.careconnect.ri.entity.immunisation;

import uk.nhs.careconnect.ri.entity.BaseResource;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.encounter.EncounterEntity;
import uk.nhs.careconnect.ri.entity.location.LocationEntity;
import uk.nhs.careconnect.ri.entity.patient.PatientEntity;

import javax.persistence.*;

@Entity
@Table(name = "Immunisation")
public class ImmunisationEntity extends BaseResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="IMMUNISATION_ID")
    private Long id;

    @ManyToOne
    @JoinColumn (name = "PATIENT_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_PATIENT"))
    private PatientEntity patient;

    @ManyToOne
    @JoinColumn (name = "MEDICATION_CODE_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_VACCINE_CODE"))
    private ConceptEntity vacinationCode;

    @ManyToOne
    @JoinColumn(name="ENCOUNTER_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_ENCOUNTER"))
    private EncounterEntity encounter;

    @ManyToOne
    @JoinColumn(name="LOCATION_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_LOCATION"))
    private LocationEntity location;

    @ManyToOne
    @JoinColumn(name="STATUS_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_STATUS_CONCEPT"))
    private ConceptEntity status;

    public Long getId() {
        return id;
    }

    public ImmunisationEntity setPatient(PatientEntity patient) {
        this.patient = patient;
        return this;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public EncounterEntity getEncounter() {
        return encounter;
    }

    public ImmunisationEntity setEncounter(EncounterEntity contextEncounter) {
        this.encounter = contextEncounter;
        return this;
    }

    public ConceptEntity getVacinationCode() {
        return vacinationCode;
    }

    public ImmunisationEntity setVacinationCode(ConceptEntity vacinationCode) {
        this.vacinationCode = vacinationCode;
        return this;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public ImmunisationEntity setLocation(LocationEntity location) {
        this.location = location;
        return this;
    }

    public ConceptEntity getStatus() {
        return status;
    }

    public ImmunisationEntity setStatus(ConceptEntity status) {
        this.status = status;
        return this;
    }
}
