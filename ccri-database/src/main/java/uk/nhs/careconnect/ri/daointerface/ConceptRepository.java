package uk.nhs.careconnect.ri.daointerface;

import ca.uhn.fhir.rest.api.server.RequestDetails;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Duration;
import org.hl7.fhir.dstu3.model.Quantity;
import uk.nhs.careconnect.ri.entity.Terminology.CodeSystemEntity;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptDesignation;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptParentChildLink;

public interface ConceptRepository {

    ConceptEntity findCode(Coding code);

    ConceptEntity findCode(Duration duration);

    ConceptEntity findAddCode(Coding code);

    ConceptEntity findAddCode(Quantity quantity);

    ConceptEntity findCode(CodeSystemEntity codeSystemUri, String code);

    ConceptEntity save(ConceptEntity conceptEntity);

    //public ConceptEntity saveTransactional(ConceptEntity conceptEntity);

    void save(ConceptParentChildLink conceptParentChildLink);

    void persistLinks(ConceptEntity conceptEntity);

    void storeNewCodeSystemVersion(CodeSystemEntity theCodeSystemVersion, RequestDetails theRequestDetails);

    ConceptDesignation save(ConceptDesignation conceptDesignation);

    Session getSession();

    public CodeSystemEntity findBySystem(String system);


    public Transaction getTransaction(Session session);

  //  public void beginTransaction(Transaction tx);

    public void commitTransaction(Transaction tx);



}
