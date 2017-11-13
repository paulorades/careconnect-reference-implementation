package uk.nhs.careconnect.ri.entity.encounter;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hl7.fhir.dstu3.model.Encounter;
import uk.nhs.careconnect.ri.entity.BaseResource;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.location.LocationEntity;
import uk.nhs.careconnect.ri.entity.organization.OrganisationEntity;
import uk.nhs.careconnect.ri.entity.patient.PatientEntity;
import uk.nhs.careconnect.ri.entity.practitioner.PractitionerEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Encounter")
public class EncounterEntity extends BaseResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ENCOUNTER_ID")
    private Long id;

    @ManyToOne
    @JoinColumn (name = "PATIENT_ID",foreignKey= @ForeignKey(name="FK_ENCOUNTER_PATIENT"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private PatientEntity patient;


    @ManyToOne
    @JoinColumn(name="CLASS_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_ENCOUNTER_CLASS_CONCEPT_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity _class;


    @ManyToOne
    @JoinColumn(name="TYPE_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_ENCOUNTER_TYPE_CONCEPT_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity type;

    @ManyToOne
    @JoinColumn(name="PRIORITY_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_ENCOUNTER_PRIORITY_CONCEPT_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity priority;


    @Enumerated(EnumType.ORDINAL)
    Encounter.EncounterStatus status;

    @ManyToOne
    @JoinColumn(name="PARTICIPANT_PRACTITIONER_ID",foreignKey= @ForeignKey(name="FK_ENCOUNTER_PRACTITIONER_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private PractitionerEntity participant;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "periodStartDate")
    private Date periodStartDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "periodEndDate")
    private Date periodEndDate;

    @OneToMany(mappedBy="encounter", targetEntity = EncounterReason.class)
    @LazyCollection(LazyCollectionOption.TRUE)
    Set<EncounterReason> reasons = new HashSet<>();

    @OneToMany(mappedBy="encounter", targetEntity = EncounterDiagnosis.class)
    @LazyCollection(LazyCollectionOption.TRUE)
    Set<EncounterReason> diagnoses = new HashSet<>();

    @OneToMany(mappedBy="encounter", targetEntity = EncounterIdentifier.class)
    @LazyCollection(LazyCollectionOption.TRUE)
    Set<EncounterIdentifier> identifiers = new HashSet<>();

    @OneToMany(mappedBy="encounter", targetEntity = EncounterEpisode.class)
    @LazyCollection(LazyCollectionOption.TRUE)
    Set<EncounterEpisode> episodes = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="LOCATION_ID",foreignKey= @ForeignKey(name="FK_ENCOUNTER_LOCATION_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private LocationEntity location;

    @ManyToOne
    @JoinColumn(name="ORGANISATION_ID",foreignKey= @ForeignKey(name="FK_ENCOUNTER_ORGANISATION_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private OrganisationEntity serviceProvider;

    public Long getId() {
        return id;
    }

    public EncounterEntity setPatient(PatientEntity patient) {
        this.patient = patient;
        return this;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public Set<EncounterIdentifier> getIdentifiers() {
        if (identifiers == null) {
            identifiers = new HashSet<>();
        }return identifiers;
    }
    public void setIdentifiers(Set<EncounterIdentifier> identifiers) {
        this.identifiers = identifiers;
    }

    public Set<EncounterEpisode> getEpisodes() {
        return episodes;
    }
    public void setEpisodes(Set<EncounterEpisode> episodes) {
        this.episodes = episodes;
    }

    public Date getPeriodEndDate() {
        return periodEndDate;
    }

    public Date getPeriodStartDate() {
        return periodStartDate;
    }

    public EncounterEntity setPeriodEndDate(Date periodEndDate) {
        this.periodEndDate = periodEndDate;
        return this;
    }

    public EncounterEntity setPeriodStartDate(Date periodStartDate) {
        this.periodStartDate = periodStartDate;
        return this;
    }

    public EncounterEntity  setStatus(Encounter.EncounterStatus status) {
        this.status = status;
        return this;
    }

    public Encounter.EncounterStatus getStatus() {
        return status;
    }

    public ConceptEntity getType() {
        return type;
    }

    public ConceptEntity _getClass() {
        return _class;
    }

    public ConceptEntity getPriority() {
        return priority;
    }

    public OrganisationEntity getServiceProvider() {
        return serviceProvider;
    }

    public PractitionerEntity getParticipant() {
        return participant;
    }

    public Set<EncounterReason> getDiagnoses() {
        if (diagnoses == null) {
            diagnoses = new HashSet<>();
        }
        return diagnoses;
    }

    public Set<EncounterReason> getReasons() {
        if (reasons == null) {
            reasons = new HashSet<>();
        }
        return reasons;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public EncounterEntity setDiagnoses(Set<EncounterReason> diagnoses) {
        this.diagnoses = diagnoses;
        return this;
    }

    public EncounterEntity _setClass(ConceptEntity _class) {
        this._class = _class;
        return this;
    }

    public EncounterEntity setParticipant(PractitionerEntity participant) {
        this.participant = participant;
        return this;
    }

    public EncounterEntity setLocation(LocationEntity location) {
        this.location = location;
        return this;
    }

    public EncounterEntity setPriority(ConceptEntity priority) {
        this.priority = priority;
        return this;
    }

    public EncounterEntity setServiceProvider(OrganisationEntity serviceProvider) {
        this.serviceProvider = serviceProvider;
        return this;
    }

    public EncounterEntity setReasons(Set<EncounterReason> reasons) {
        this.reasons = reasons;
        return this;
    }

    public EncounterEntity setType(ConceptEntity type) {
        this.type = type;
        return this;
    }
}
