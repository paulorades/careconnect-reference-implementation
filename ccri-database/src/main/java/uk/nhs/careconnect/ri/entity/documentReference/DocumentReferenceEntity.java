package uk.nhs.careconnect.ri.entity.documentReference;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.Enumerations;
import uk.nhs.careconnect.ri.entity.BaseResource;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.encounter.EncounterEntity;
import uk.nhs.careconnect.ri.entity.organization.OrganisationEntity;
import uk.nhs.careconnect.ri.entity.patient.PatientEntity;
import uk.nhs.careconnect.ri.entity.practitioner.PractitionerEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "DocumentReference", indexes = {



})
public class DocumentReferenceEntity extends BaseResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="DOCUMENT_REFERENCE_ID")
    private Long id;



    @OneToMany(mappedBy="documentReference", targetEntity=DocumentReferenceIdentifier.class)
    private Set<DocumentReferenceIdentifier> identifiers = new HashSet<>();

    @Enumerated(EnumType.ORDINAL)
    private Enumerations.DocumentReferenceStatus status;

    @Enumerated(EnumType.ORDINAL)
    private DocumentReference.ReferredDocumentStatus docStatus;

    @ManyToOne
    @JoinColumn(name="TYPE_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_DOCUMENT_REFERENCE_TYPE_CONCEPT_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity type;

    @ManyToOne
    @JoinColumn(name="CLASS_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_DOCUMENT_REFERENCE_CLASS_CONCEPT_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity _class;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "indexed")
    private Date indexed;

    @ManyToOne
    @LazyCollection(LazyCollectionOption.TRUE)
    @JoinColumn (name = "PATIENT_ID",foreignKey= @ForeignKey(name="FK_DOCUMENT_REFERENCE_PATIENT_ID"))
    private PatientEntity patient;
    // subject

    @OneToMany(mappedBy="documentReference", targetEntity=DocumentReferenceAuthor.class)
    private Set<DocumentReferenceAuthor> authors = new HashSet<>();

    @ManyToOne
    @JoinColumn(name="AUTHENTICATOR_PRACTITIONER_ID",foreignKey= @ForeignKey(name="FK_DOCUMENT_REFERENCE_AUTHENTICATOR_PRACTITIONER"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private PractitionerEntity authenticatorPractitioner;

    @ManyToOne
    @JoinColumn(name="AUTHENTICATOR_ORGANISATION_ID",foreignKey= @ForeignKey(name="FK_DOCUMENT_REFERENCE_AUTHENTICATOR_ORGANISATION"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private OrganisationEntity authenticatorOrganisation;

    @ManyToOne
    @JoinColumn(name="CUSTODIAN_ORGANISATION_ID",foreignKey= @ForeignKey(name="FK_DOCUMENT_REFERENCE_CUSTODIAN_ORGANISATION"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private OrganisationEntity custodian;

    @ManyToOne
    @JoinColumn(name="FORMAT_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_DOCUMENT_REFERENCE_FORMAT_CONCEPT_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity contentFormat;

    @ManyToOne
    @JoinColumn(name="CONTEXT_ENCOUNTER_ID",foreignKey= @ForeignKey(name="FK_DOCUMENT_REFERENCE_ENCOUNTER_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private EncounterEntity contextEncounter;

    @ManyToOne
    @JoinColumn(name="PRACTICE_SETTING_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_DOCUMENT_REFERENCE_PRACTICE_SETTING_CONCEPT_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity contextPracticeSetting;



    public Long getId() {
        return id;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DocumentReferenceEntity setIdentifiers(Set<DocumentReferenceIdentifier> identifiers) {
        this.identifiers = identifiers;
        return this;
    }


}
