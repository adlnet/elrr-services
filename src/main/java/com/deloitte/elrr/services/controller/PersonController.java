package com.deloitte.elrr.services.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.deloitte.elrr.entity.Association;
import com.deloitte.elrr.entity.Competency;
import com.deloitte.elrr.entity.Credential;
import com.deloitte.elrr.entity.Email;
import com.deloitte.elrr.entity.EmploymentRecord;
import com.deloitte.elrr.entity.Identity;
import com.deloitte.elrr.entity.LearningRecord;
import com.deloitte.elrr.entity.LearningResource;
import com.deloitte.elrr.entity.Organization;
import com.deloitte.elrr.entity.Person;
import com.deloitte.elrr.entity.PersonalCompetency;
import com.deloitte.elrr.entity.PersonalCredential;
import com.deloitte.elrr.entity.Phone;
import com.deloitte.elrr.jpa.svc.AssociationSvc;
import com.deloitte.elrr.jpa.svc.CompetencySvc;
import com.deloitte.elrr.jpa.svc.CredentialSvc;
import com.deloitte.elrr.jpa.svc.EmailSvc;
import com.deloitte.elrr.jpa.svc.EmploymentRecordSvc;
import com.deloitte.elrr.jpa.svc.FacilitySvc;
import com.deloitte.elrr.jpa.svc.IdentitySvc;
import com.deloitte.elrr.jpa.svc.LearningRecordSvc;
import com.deloitte.elrr.jpa.svc.LearningResourceSvc;
import com.deloitte.elrr.jpa.svc.LocationSvc;
import com.deloitte.elrr.jpa.svc.OrganizationSvc;
import com.deloitte.elrr.jpa.svc.PersonSvc;
import com.deloitte.elrr.jpa.svc.PersonalCompetencySvc;
import com.deloitte.elrr.jpa.svc.PersonalCredentialSvc;
import com.deloitte.elrr.jpa.svc.PhoneSvc;
import com.deloitte.elrr.services.dto.AssociationDto;
import com.deloitte.elrr.services.dto.CompetencyDto;
import com.deloitte.elrr.services.dto.CredentialDto;
import com.deloitte.elrr.services.dto.EmailDto;
import com.deloitte.elrr.services.dto.EmploymentRecordDto;
import com.deloitte.elrr.services.dto.FacilityDto;
import com.deloitte.elrr.services.dto.IdentityDto;
import com.deloitte.elrr.services.dto.LearningRecordDto;
import com.deloitte.elrr.services.dto.LocationDto;
import com.deloitte.elrr.services.dto.PersonDto;
import com.deloitte.elrr.services.dto.PersonalQualificationDto;
import com.deloitte.elrr.services.dto.PhoneDto;
import com.deloitte.elrr.services.exception.ResourceNotFoundException;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mnelakurti
 *
 */
@CrossOrigin(origins = {
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:3001",
        "http://ec2-18-116-20-188.us-east-2.compute.amazonaws.com:5000" })
@RestController
@RequestMapping("api")
@Slf4j
public class PersonController {

    @Autowired
    private PersonSvc personSvc;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PhoneSvc phoneSvc;

    @Autowired
    private EmailSvc emailSvc;

    @Autowired
    private IdentitySvc identitySvc;

    @Autowired
    private OrganizationSvc organizationSvc;

    @Autowired
    private AssociationSvc associationSvc;

    @Autowired
    private LocationSvc locationSvc;

    @Autowired
    private FacilitySvc facilitySvc;

    @Autowired
    private EmploymentRecordSvc employmentRecordSvc;

    @Autowired
    private CompetencySvc competencySvc;

    @Autowired
    private PersonalCompetencySvc personalCompetencySvc;

    @Autowired
    private CredentialSvc credentialSvc;

    @Autowired
    private PersonalCredentialSvc personalCredentialSvc;

    @Autowired
    private LearningResourceSvc learningResourceSvc;

    @Autowired
    private LearningRecordSvc learningRecordSvc;

    //Log Line Utils
    private static final String PERSON_NOT_FOUND =
        "Person not found for this id :: ";
    private static final String REMOVING_FROM_PERSON =
        "Removing %s (id: %s) from Person with id: %s";
    private static final String ADDING_TO_PERSON =
        "Adding %s to Person with id: %s";
    private static final String GETTING_FOR_PERSON =
        "Getting %s for Person with id: %s";

    /**
     * Get all persons with optional filters.
     *
     * @param filters Optional filters for person search
     * @return ResponseEntity<List<PersonDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person")
    public ResponseEntity<List<PersonDto>> getAllPersons(
            @ModelAttribute final Person.Filter filters) {
        log.info("getting PersonDto with filters - id: {}, ifi: {}, "
                + "associatedOrgId: {}, employerOrgId: {}, "
                + "hasExtension: {}, extensionPath: {}, "
                + "extensionPathMatch: {}, name: {}, locationId: {}, "
                + "emailAddress: {}, phoneNumber: {}, competencyId: {}, "
                + "credentialId: {}, learningResourceId: {}",
                filters.getId(),
                filters.getIfi(),
                filters.getAssociatedOrgId(),
                filters.getEmployerOrgId(),
                filters.getHasExtension(),
                filters.getExtensionPath(),
                filters.getExtensionPathMatch(),
                filters.getName(),
                filters.getLocationId(),
                filters.getEmailAddress(),
                filters.getPhoneNumber(),
                filters.getCompetencyId(),
                filters.getCredentialId(),
                filters.getLearningResourceId());

        List<Person> persons = personSvc.findPersonsWithFilters(filters);

        List<PersonDto> personDtoList = persons.stream()
                .map(person -> mapper.map(person, PersonDto.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(personDtoList);
    }

    /**
     *
     * @param personId
     * @return ResponseEntity<PersonDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{id}")
    public ResponseEntity<PersonDto> getPersonById(
            @PathVariable(value = "id") final UUID personId)
            throws ResourceNotFoundException {
        log.info("getting  Person:.........");
        log.info("getting Person id:........." + personId);
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        PersonDto personDto = mapper.map(person, PersonDto.class);
        return ResponseEntity.ok().body(personDto);
    }

    /**
     *
     * @param personDto
     * @return ResponseEntity<PersonDto>
     */
    @PreAuthorize("hasPermission('person', 'CREATE')")
    @PostMapping("/person")
    public ResponseEntity<PersonDto> createPerson(
            @Valid @RequestBody final PersonDto personDto) {
        Person person = mapper.map(personDto, Person.class);
        PersonDto response = mapper.map(personSvc.save(person),
                PersonDto.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     *
     * @param personId
     * @param personDto
     * @return ResponseEntity<PersonDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'UPDATE')")
    @PutMapping("/person/{id}")
    public ResponseEntity<PersonDto> updatePerson(
            @PathVariable(value = "id") final UUID personId,
            @Valid @RequestBody final PersonDto personDto)
            throws ResourceNotFoundException {
        log.info("Updating  personId:.........");
        log.info("Updating personId id:........." + personId);
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                    PERSON_NOT_FOUND + personId));
        log.info("Update Person:........." + personDto);
        // Assigning values from request
        mapper.map(personDto, person);
        // Reset Id / Primary key from query parameter
        person.setId(personId);
        log.info("Update Person:........." + person);
        return ResponseEntity
                .ok(mapper.map(personSvc.save(person), PersonDto.class));

    }

    /**
     *
     * @param personId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'DELETE')")
    @DeleteMapping("/person/{id}")
    public ResponseEntity<HttpStatus> deletePerson(
            @PathVariable(value = "id") final UUID personId)
            throws ResourceNotFoundException {
        log.info("Deleting  Person:.........");
        log.info("Deleting Person id:........." + personId);
        personSvc.get(personId).orElseThrow(() -> new ResourceNotFoundException(
            PERSON_NOT_FOUND + personId));
        personSvc.delete(personId);
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }

    /*
     * IDENTITY
     */

    private static final String LOG_ID = "Identity";

    /**
     * Get Identities for person.
     *
     * @param personId
     * @return ResponseEntity<List<IdentityDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{personId}/identity")
    public ResponseEntity<List<IdentityDto>> getIdentities(
            @PathVariable(value = "personId") final UUID personId)
            throws ResourceNotFoundException {
        log.info(String.format(GETTING_FOR_PERSON, "identities", personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        return ResponseEntity.ok(person.getIdentities().stream()
                .map(p -> mapper.map(p, IdentityDto.class))
                .collect(Collectors.toList()));
    }


    /**
     * Add Identity to person.
     *
     * @param personId person to add identity to
     * @param identityDto identity object
     * @return ResponseEntity<List<IdentityDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'UPDATE')")
    @PostMapping("/person/{personId}/identity")
    public ResponseEntity<List<IdentityDto>> addIdentityToPerson(
            @PathVariable(value = "personId") final UUID personId,
            @Valid @RequestBody final IdentityDto identityDto)
            throws ResourceNotFoundException {
        log.info(String.format(ADDING_TO_PERSON, LOG_ID, personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        Identity identity = mapper.map(identityDto, Identity.class);
        identity.setPerson(person);
        identitySvc.save(identity);
        person.getIdentities().add(identity);
        personSvc.save(person);
        return ResponseEntity.ok(person.getIdentities().stream()
                .map(p -> mapper.map(p, IdentityDto.class))
                .collect(Collectors.toList()));
    }


    /**
     * Deleted Identity by person and identity ID.
     *
     * @param personId ID of person
     * @param identityId ID of identity
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
     @PreAuthorize("hasPermission('person', 'UPDATE')")
    @DeleteMapping("/person/{personId}/identity/{identityId}")
    public ResponseEntity<HttpStatus> deleteIdentity(
            @PathVariable(value = "personId") final UUID personId,
            @PathVariable(value = "identityId") final UUID identityId)
            throws ResourceNotFoundException {
        log.info(String.format(REMOVING_FROM_PERSON, LOG_ID, identityId,
            personId));
        Identity identity = identitySvc.get(identityId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Identity not found for this id :: " + identityId));
        if (!identity.getPerson().getId().equals(personId))
            throw new ResourceNotFoundException(
                    "Person does not match identity.");
        identitySvc.delete(identityId);
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }

    /*
     * Phones
     */

    private static final String LOG_PHONE = "Phone";

    /**
     * Get Phones for a Person.
     *
     * @param personId Person to get phones for
     * @return ResponseEntity<List<PhoneDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{personId}/phone")
    public ResponseEntity<List<PhoneDto>> getPhones(
            @PathVariable(value = "personId") final UUID personId)
            throws ResourceNotFoundException {
        log.info(String.format(GETTING_FOR_PERSON, "phones", personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        return ResponseEntity.ok(person.getPhoneNumbers().stream()
                .map(p -> mapper.map(p, PhoneDto.class))
                .collect(Collectors.toList()));
    }


    /**
     * Add phone to person.
     *
     * @param personId
     * @param phoneDto
     * @return ResponseEntity<List<PhoneDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/phone', 'ASSOCIATE') and "
        + "hasPermission('phone', 'CREATE')")
    @PostMapping("/person/{personId}/phone")
    public ResponseEntity<List<PhoneDto>> addPhoneToPerson(
            @PathVariable(value = "personId") final UUID personId,
            @Valid @RequestBody final PhoneDto phoneDto)
            throws ResourceNotFoundException {
        log.info(String.format(ADDING_TO_PERSON, LOG_PHONE, personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        Phone phone = phoneSvc.save(mapper.map(phoneDto, Phone.class));
        person.getPhoneNumbers().add(phone);
        personSvc.save(person);
        return ResponseEntity.ok(person.getPhoneNumbers().stream()
                .map(p -> mapper.map(p, PhoneDto.class))
                .collect(Collectors.toList()));
    }

    /**
     * associate existing phone with person.
     *
     * @param personId
     * @param phoneId
     * @return ResponseEntity<List<PhoneDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/phone', 'ASSOCIATE')")
    @PostMapping("/person/{personId}/phone/{phoneId}")
    public ResponseEntity<List<PhoneDto>> associatePhoneWithPerson(
            @PathVariable(value = "personId") final UUID personId,
            @PathVariable(value = "phoneId") final UUID phoneId)
            throws ResourceNotFoundException {
        log.info(String.format(ADDING_TO_PERSON, LOG_PHONE, personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        Phone phone = phoneSvc.get(phoneId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Phone not found for this id :: " + personId));
        person.getPhoneNumbers().add(phone);
        personSvc.save(person);
        return ResponseEntity.ok(person.getPhoneNumbers().stream()
                .map(p -> mapper.map(p, PhoneDto.class))
                .collect(Collectors.toList()));
    }

    /**
     * Remove phone from person.
     *
     * @param personId
     * @param phoneId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/phone', 'DISASSOCIATE')")
    @DeleteMapping("/person/{personId}/phone/{phoneId}")
    public ResponseEntity<HttpStatus> removePhoneFromPerson(
            @PathVariable(value = "personId") final UUID personId,
            @PathVariable(value = "phoneId") final UUID phoneId)
            throws ResourceNotFoundException {
        log.info(String.format(REMOVING_FROM_PERSON, LOG_PHONE, phoneId,
            personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        person.setPhoneNumbers(person.getPhoneNumbers().stream()
                .filter(p -> !p.getId().equals(phoneId))
                .collect(Collectors.toSet()));
        personSvc.save(person);
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }

    /*
     * Emails
     */

    /**
     * Get emails for person.
     *
     * @param personId
     * @return ResponseEntity<List<EmailDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{personId}/email")
    public ResponseEntity<List<EmailDto>> getEmails(
            @PathVariable(value = "personId") final UUID personId)
            throws ResourceNotFoundException {
        log.info(String.format(GETTING_FOR_PERSON, "emails", personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        return ResponseEntity.ok(person.getEmailAddresses().stream()
                .map(p -> mapper.map(p, EmailDto.class))
                .collect(Collectors.toList()));
    }

    /**
     * Add email to person.
     *
     * @param personId
     * @param emailDto
     * @return ResponseEntity<List<EmailDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/email', 'ASSOCIATE') and "
        + "hasPermission('email', 'CREATE')")
    @PostMapping("/person/{personId}/email")
    public ResponseEntity<List<EmailDto>> addEmailToPerson(
            @PathVariable(value = "personId") final UUID personId,
            @Valid @RequestBody final EmailDto emailDto)
            throws ResourceNotFoundException {
        log.info(String.format(ADDING_TO_PERSON, "Email", personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        Email email = emailSvc.save(mapper.map(emailDto, Email.class));
        person.getEmailAddresses().add(email);
        personSvc.save(person);
        return ResponseEntity.ok(person.getEmailAddresses().stream()
                .map(p -> mapper.map(p, EmailDto.class))
                .collect(Collectors.toList()));
    }

    /**
     * Associate existin email with person.
     *
     * @param personId
     * @param emailId
     * @return ResponseEntity<List<EmailDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/email', 'ASSOCIATE')")
    @PostMapping("/person/{personId}/email/{emailId}")
    public ResponseEntity<List<EmailDto>> associateEmailWithPerson(
            @PathVariable(value = "personId") final UUID personId,
            @PathVariable(value = "emailId") final UUID emailId)
            throws ResourceNotFoundException {
        log.info(String.format(REMOVING_FROM_PERSON, "Email", emailId,
            personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        Email email = emailSvc.get(emailId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Email not found for this id :: " + personId));
        person.getEmailAddresses().add(email);
        personSvc.save(person);
        return ResponseEntity.ok(person.getEmailAddresses().stream()
                .map(p -> mapper.map(p, EmailDto.class))
                .collect(Collectors.toList()));
    }

    /**
     * Remove email from person.
     *
     * @param personId
     * @param emailId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/email', 'DISASSOCIATE')")
    @DeleteMapping("/person/{personId}/email/{emailId}")
    public ResponseEntity<HttpStatus> removeEmailFromPerson(
            @PathVariable(value = "personId") final UUID personId,
            @PathVariable(value = "emailId") final UUID emailId)
            throws ResourceNotFoundException {
        log.info(String.format(REMOVING_FROM_PERSON, "email", emailId,
            personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        person.setEmailAddresses(person.getEmailAddresses().stream()
                .filter(p -> !p.getId().equals(emailId))
                .collect(Collectors.toSet()));
        personSvc.save(person);
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }

    /*
     * COMPETENCY
     */

    private static final String LOG_COMP = "Competency";

    /**
     * Get Competencies of person.
     *
     * @param personId
     * @return ResponseEntity<List<PersonalQualificationDto<CompetencyDto>>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{personId}/competency")
    public ResponseEntity<List<PersonalQualificationDto<CompetencyDto>>>
            getCompetencies(@PathVariable(value = "personId")
            final UUID personId)
            throws ResourceNotFoundException {
        log.info(String.format(GETTING_FOR_PERSON, "competencies", personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));

        return ResponseEntity.ok(person.getCompetencies().stream()
                .map(c -> new PersonalQualificationDto<CompetencyDto>(
                        mapper.map(c.getCompetency(), CompetencyDto.class),
                        c.getHasRecord()))
                .collect(Collectors.toList()));
    }

    /**
     * Get Competency of person.
     *
     * @param personId
     * @param competencyId
     * @return ResponseEntity<PersonalQualificationDto<CompetencyDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{personId}/competency/{competencyId}")
    public ResponseEntity<PersonalQualificationDto<CompetencyDto>>
            getCompetency(
                @PathVariable(value = "personId") final UUID personId,
                @PathVariable(value = "competencyId") final UUID competencyId)
            throws ResourceNotFoundException {
        log.info(String.format(GETTING_FOR_PERSON, "competencies", personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        PersonalCompetency pc = person.getCompetencies().stream()
                .filter(c -> c.getCompetency().getId().equals(competencyId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Competency not found this id :: " + personId));
        return ResponseEntity.ok(new PersonalQualificationDto<CompetencyDto>(
                mapper.map(pc.getCompetency(), CompetencyDto.class),
                pc.getHasRecord()));
    }

    /**
     * Associate Competency with person.
     *
     * @param personId
     * @param competencyId
     * @param compDto
     * @return ResponseEntity<List<PersonalQualificationDto<CompetencyDto>>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/competency', 'ASSOCIATE')")
    @PostMapping("/person/{personId}/competency/{competencyId}")
    public ResponseEntity<List<PersonalQualificationDto<CompetencyDto>>>
            associateCompetency(
                @PathVariable(value = "personId") final UUID personId,
                @PathVariable(value = "competencyId") final UUID competencyId,
                @Valid @RequestBody
                final PersonalQualificationDto<CompetencyDto> compDto)
            throws ResourceNotFoundException {
        log.info(String.format(ADDING_TO_PERSON, LOG_ID, personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        Competency comp = competencySvc.get(competencyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Competency not found for this id :: " + competencyId));
        PersonalCompetency pc = (PersonalCompetency) personalCompetencySvc.save(
                new PersonalCompetency(person, comp, compDto.getHasRecord()));
        person.getCompetencies().add(pc);
        personSvc.save(person);
        return ResponseEntity.ok(person.getCompetencies().stream()
                .map(c -> new PersonalQualificationDto<CompetencyDto>(
                        mapper.map(c.getCompetency(), CompetencyDto.class),
                        c.getHasRecord()))
                .collect(Collectors.toList()));
    }

    /**
     * Update Personal Competency.
     *
     * @param personId
     * @param competencyId
     * @param compDto
     * @return ResponseEntity<List<PersonalQualificationDto<CompetencyDto>>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/competency', 'ASSOCIATE')")
    @PutMapping("/person/{personId}/competency/{competencyId}")
    public ResponseEntity<List<PersonalQualificationDto<CompetencyDto>>>
            updateCompetencyAssociation(
                @PathVariable(value = "personId") final UUID personId,
                @PathVariable(value = "competencyId") final UUID competencyId,
                @Valid @RequestBody
                final PersonalQualificationDto<CompetencyDto> compDto)
                throws ResourceNotFoundException {
        log.info(String.format(ADDING_TO_PERSON, LOG_COMP, personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        PersonalCompetency pc = person.getCompetencies().stream()
                .filter(c -> c.getCompetency().getId().equals(competencyId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Competency not found this id :: " + personId));
        pc.setHasRecord(compDto.getHasRecord());
        personalCompetencySvc.save(pc);
        return ResponseEntity.ok(person.getCompetencies().stream()
                .map(c -> new PersonalQualificationDto<CompetencyDto>(
                        mapper.map(c.getCompetency(), CompetencyDto.class),
                        c.getHasRecord()))
                .collect(Collectors.toList()));
    }

    /**
     * Delete Personal Competency.
     *
     * @param personId
     * @param competencyId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/competency', 'DISASSOCIATE')")
    @DeleteMapping("/person/{personId}/competency/{competencyId}")
    public ResponseEntity<HttpStatus> deleteCompetencyAssociation(
            @PathVariable(value = "personId") final UUID personId,
            @PathVariable(value = "competencyId") final UUID competencyId)
            throws ResourceNotFoundException {
        log.info(String.format(REMOVING_FROM_PERSON, LOG_COMP, competencyId,
            personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        PersonalCompetency pc = person.getCompetencies().stream()
                .filter(c -> c.getCompetency().getId().equals(competencyId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Person->Competency not found for this competency id :: "
                    + personId));
        person.getCompetencies().remove(pc);
        personalCompetencySvc.delete(pc.getId());
        personSvc.save(person);
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }

    /*
     * CREDENTIAL
     */

    /**
     * Get Credentials of person.
     *
     * @param personId
     * @return ResponseEntity<List<PersonalQualificationDto<CredentialDto>>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{personId}/credential")
    public ResponseEntity<List<PersonalQualificationDto<CredentialDto>>>
            getCredentials(
                @PathVariable(value = "personId") final UUID personId)
            throws ResourceNotFoundException {
        log.info(String.format(GETTING_FOR_PERSON, "credentials", personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));

        return ResponseEntity.ok(person.getCredentials().stream()
                .map(c -> new PersonalQualificationDto<CredentialDto>(
                        mapper.map(c.getCredential(), CredentialDto.class),
                        c.getHasRecord()))
                .collect(Collectors.toList()));
    }

    /**
     * Get Personal Credential.
     *
     * @param personId
     * @param credentialId
     * @return ResponseEntity<PersonalQualificationDto<CredentialDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{personId}/credential/{credentialId}")
    public ResponseEntity<PersonalQualificationDto<CredentialDto>>
            getCredential(
                @PathVariable(value = "personId") final UUID personId,
                @PathVariable(value = "credentialId") final UUID credentialId)
            throws ResourceNotFoundException {
        log.info(String.format(GETTING_FOR_PERSON, "credentials", personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        PersonalCredential pc = person.getCredentials().stream()
                .filter(c -> c.getCredential().getId().equals(credentialId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credential not found this id :: " + personId));
        return ResponseEntity.ok(new PersonalQualificationDto<CredentialDto>(
                mapper.map(pc.getCredential(), CredentialDto.class),
                pc.getHasRecord()));
    }

    /**
     * Associate Credential to person.
     *
     * @param personId
     * @param credentialId
     * @param credDto Personal Credential details
     * @return ResponseEntity<List<PersonalQualificationDto<CredentialDto>>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/credential', 'ASSOCIATE')")
    @PostMapping("/person/{personId}/credential/{credentialId}")
    public ResponseEntity<List<PersonalQualificationDto<CredentialDto>>>
            associateCredential(
                @PathVariable(value = "personId") final UUID personId,
                @PathVariable(value = "credentialId") final UUID credentialId,
                @Valid @RequestBody
                final PersonalQualificationDto<CredentialDto> credDto)
            throws ResourceNotFoundException {
        log.info(String.format(ADDING_TO_PERSON, "Credential", personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        Credential comp = credentialSvc.get(credentialId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credential not found for this id :: " + credentialId));
        PersonalCredential pc = (PersonalCredential) personalCredentialSvc.save(
                new PersonalCredential(person, comp, credDto.getHasRecord()));
        person.getCredentials().add(pc);
        personSvc.save(person);
        return ResponseEntity.ok(person.getCredentials().stream()
                .map(c -> new PersonalQualificationDto<CredentialDto>(
                        mapper.map(c.getCredential(), CredentialDto.class),
                        c.getHasRecord()))
                .collect(Collectors.toList()));
    }

    /**
     * Update Personal Credential.
     *
     * @param personId
     * @param credentialId
     * @param credDto Personal Credential details
     * @return ResponseEntity<List<PersonalQualificationDto<CredentialDto>>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/credential', 'ASSOCIATE')")
    @PutMapping("/person/{personId}/credential/{credentialId}")
    public ResponseEntity<List<PersonalQualificationDto<CredentialDto>>>
            updateCredentialAssociation(
                @PathVariable(value = "personId") final UUID personId,
                @PathVariable(value = "credentialId") final UUID credentialId,
                @Valid @RequestBody
                final PersonalQualificationDto<CredentialDto> credDto)
            throws ResourceNotFoundException {
        log.info(String.format(ADDING_TO_PERSON, LOG_COMP, personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        PersonalCredential pc = person.getCredentials().stream()
                .filter(c -> c.getCredential().getId().equals(credentialId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Credential not found this id :: " + personId));
        pc.setHasRecord(credDto.getHasRecord());
        personalCredentialSvc.save(pc);
        return ResponseEntity.ok(person.getCredentials().stream()
                .map(c -> new PersonalQualificationDto<CredentialDto>(
                        mapper.map(c.getCredential(), CredentialDto.class),
                        c.getHasRecord()))
                .collect(Collectors.toList()));
    }

    /**
     * Delete Personal Credential.
     *
     * @param personId
     * @param credentialId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/credential', 'DISASSOCIATE')")
    @DeleteMapping("/person/{personId}/credential/{credentialId}")
    public ResponseEntity<HttpStatus> deleteCredentialAssociation(
            @PathVariable(value = "personId") final UUID personId,
            @PathVariable(value = "credentialId") final UUID credentialId)
            throws ResourceNotFoundException {
        log.info(String.format(REMOVING_FROM_PERSON, "Credential", credentialId,
            personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        PersonalCredential pc = person.getCredentials().stream()
                .filter(c -> c.getCredential().getId().equals(credentialId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Person->Credential not found for this credential id :: "
                    + personId));
        person.getCredentials().remove(pc);
        personalCredentialSvc.delete(pc.getId());
        personSvc.save(person);
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }

    /*
     * LEARNING RECORD
     */

    /**
     * Get Learning Records for Person.
     *
     * @param personId
     * @return ResponseEntity<List<LearningRecordDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{personId}/learningrecord")
    public ResponseEntity<List<LearningRecordDto>> getLearningRecords(
            @PathVariable(value = "personId") final UUID personId)
            throws ResourceNotFoundException {
        log.info(String.format(GETTING_FOR_PERSON, "LearningRecords",
            personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));

        return ResponseEntity.ok(person.getLearningRecords().stream()
                .map(rec -> mapper.map(rec, LearningRecordDto.class))
                .collect(Collectors.toList()));
    }

    /**
     * Add Learning Record.
     *
     * @param personId
     * @param learningRecordDto Learning Record Details
     * @return ResponseEntity<List<LearningRecordDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('learningrecord', 'CREATE')")
    @PostMapping("/person/{personId}/learningrecord")
    public ResponseEntity<List<LearningRecordDto>> addLearningRecord(
            @PathVariable(value = "personId") final UUID personId,
            @Valid @RequestBody final LearningRecordDto learningRecordDto)
            throws ResourceNotFoundException {
        log.info(String.format(ADDING_TO_PERSON, "LearningRecords", personId));

        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        UUID learningResourceId = learningRecordDto.getLearningResource()
                .getId();
        LearningResource resource = learningResourceSvc.get(learningResourceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Learning Resource not found for this id :: "
                                + learningResourceId));
        LearningRecord learningRecord = mapper.map(learningRecordDto,
                LearningRecord.class);
        learningRecord.setPerson(person);
        learningRecord.setLearningResource(resource);
        learningRecordSvc.save(learningRecord);
        person.getLearningRecords().add(learningRecord);
        personSvc.save(person);
        return ResponseEntity.ok(person.getLearningRecords().stream()
                .map(rec -> mapper.map(rec, LearningRecordDto.class))
                .collect(Collectors.toList()));
    }

    /*
     * Employment Record
     */

    /**
     * Get Employment Records for Person.
     *
     * @param personId
     * @return ResponseEntity<List<EmploymentRecordDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{personId}/employmentrecord")
    public ResponseEntity<List<EmploymentRecordDto>> getEmploymentRecords(
            @PathVariable(value = "personId") final UUID personId)
            throws ResourceNotFoundException {
        log.info(String.format(GETTING_FOR_PERSON, "EmploymentRecords",
            personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));

        return ResponseEntity.ok(person.getEmploymentRecords().stream()
                .map(rec -> mapper.map(rec, EmploymentRecordDto.class))
                .collect(Collectors.toList()));
    }

    /**
     * Add Employment Record.
     *
     * @param personId
     * @param employmentRecordDto Employment Record Details
     * @return ResponseEntity<List<EmploymentRecordDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('employmentrecord', 'CREATE')")
    @PostMapping("/person/{personId}/employmentrecord")
    public ResponseEntity<List<EmploymentRecordDto>> addEmploymentRecord(
            @PathVariable(value = "personId") final UUID personId,
            @Valid @RequestBody final EmploymentRecordDto employmentRecordDto)
            throws ResourceNotFoundException {
        log.info(String.format(ADDING_TO_PERSON, "EmploymentRecord", personId));
        Person employee = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        UUID orgId = employmentRecordDto.getEmployerOrganization().getId();
        Organization organization = organizationSvc.get(orgId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Org not found for::" + orgId));

        log.info("Update EmploymentRecord:........." + employmentRecordDto);
        EmploymentRecord employmentRecord = mapper.map(employmentRecordDto,
                EmploymentRecord.class);

        employmentRecord.setEmployee(employee);
        employmentRecord.setEmployerOrganization(organization);

        // Facility and Location
        LocationDto newLocation = employmentRecordDto.getEmploymentLocation();
        employmentRecord.setEmploymentLocation(
                (newLocation != null && newLocation.getId() != null)
                        ? locationSvc.get(newLocation.getId())
                                .orElseThrow(
                                        () -> new ResourceNotFoundException(
                                            "Location not found for this id :: "
                                            + newLocation.getId()))
                        : null);
        FacilityDto newFacility = employmentRecordDto.getEmploymentFacility();
        employmentRecord.setEmploymentFacility(
                (newFacility != null && newFacility.getId() != null)
                        ? facilitySvc.get(newFacility.getId())
                                .orElseThrow(
                                        () -> new ResourceNotFoundException(
                                            "Facility not found for this id :: "
                                            + newFacility.getId()))
                        : null);

        // Role Competencies and Credentials
        if (employmentRecordDto.getCompetencies() != null)
            employmentRecord.setCompetencies(
                    employmentRecordDto.getCompetencies().stream()
                            .map(c -> competencySvc.get(c.getId()).orElse(null))
                            .collect(Collectors.toSet()));
        if (employmentRecordDto.getCredentials() != null)
            employmentRecord.setCredentials(
                    employmentRecordDto.getCredentials().stream()
                            .map(c -> credentialSvc.get(c.getId()).orElse(null))
                            .collect(Collectors.toSet()));

        employmentRecordSvc.save(employmentRecord);
        employee.getEmploymentRecords().add(employmentRecord);
        personSvc.save(employee);
        return ResponseEntity.ok(employee.getEmploymentRecords().stream()
                .map(rec -> mapper.map(rec, EmploymentRecordDto.class))
                .collect(Collectors.toList()));
    }

    /*
     * ORGANIZATION
     */

    /**
     * Get Organizations for Person.
     *
     * @param personId
     * @return ResponseEntity<List<AssociationDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{personId}/organization")
    public ResponseEntity<List<AssociationDto>> getOrganizationsByPerson(
            @PathVariable(value = "personId") final UUID personId)
            throws ResourceNotFoundException {
        log.info(String.format(GETTING_FOR_PERSON, "orgs", personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));

        return ResponseEntity.ok(person.getAssociations().stream()
                .map(assoc -> mapper.map(assoc, AssociationDto.class))
                .collect(Collectors.toList()));
    }

    /**
     * Get Organization by Person.
     *
     * @param personId
     * @param organizationId
     * @return ResponseEntity<AssociationDto>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person', 'READ')")
    @GetMapping("/person/{personId}/organization/{organizationId}")
    public ResponseEntity<AssociationDto> getAssociationByPersonAndOrg(
            @PathVariable(value = "personId") final UUID personId,
            @PathVariable(value = "organizationId") final UUID organizationId)
            throws ResourceNotFoundException {
        log.info("Getting association for person with id: " + personId
            + " and org with id: " + organizationId);
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        Association association = person.getAssociations().stream()
                .filter(assoc -> assoc.getOrganization().getId()
                        .equals(organizationId))
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No Association Exists for orgId::" + organizationId));
        return ResponseEntity.ok(mapper.map(association, AssociationDto.class));
    }

    /**
     * Associate Organization to Person.
     *
     * @param personId
     * @param organizationId
     * @param associationDto Details of association
     * @return ResponseEntity<List<AssociationDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/organization', 'ASSOCIATE')")
    @PostMapping("/person/{personId}/organization/{organizationId}")
    public ResponseEntity<List<AssociationDto>> associateOrg(
            @PathVariable(value = "personId") final UUID personId,
            @PathVariable(value = "organizationId") final UUID organizationId,
            @Valid @RequestBody final AssociationDto associationDto)
            throws ResourceNotFoundException {
        log.info(String.format(ADDING_TO_PERSON, "Org", personId));
        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        Organization organization = organizationSvc.get(organizationId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Organization not found for this id :: "
                                        + organizationId));

        Association association = person.getAssociations().stream()
                .filter(assoc -> assoc.getOrganization().getId()
                        .equals(organizationId))
                .findAny()
                .orElse(new Association());
        association.setOrganization(organization);
        association.setPerson(person);
        association.setAssociationType(associationDto.getAssociationType());

        person.getAssociations().add(association);

        associationSvc.save(association);
        personSvc.save(person);
        return ResponseEntity.ok(person.getAssociations().stream()
                .map(assoc -> mapper.map(assoc, AssociationDto.class))
                .collect(Collectors.toList()));
    }

    /**
     * Update Association of Organization to Person.
     *
     * @param personId
     * @param organizationId
     * @param associationDto Details of association
     * @return ResponseEntity<List<AssociationDto>>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/organization', 'ASSOCIATE')")
    @PutMapping("/person/{personId}/organization/{organizationId}")
    public ResponseEntity<List<AssociationDto>> updateOrgAssociation(
            @PathVariable(value = "personId") final UUID personId,
            @PathVariable(value = "organizationId") final UUID organizationId,
            @Valid @RequestBody final AssociationDto associationDto)
            throws ResourceNotFoundException {
        log.info("Updating Organization association to Person with id:......"
                + personId);

        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));
        organizationSvc.get(organizationId).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Organization not found for this id :: "
                                + organizationId));

        Association association = person.getAssociations().stream()
                .filter(assoc -> assoc.getOrganization().getId()
                        .equals(organizationId))
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No association found for org"));
        association.setAssociationType(associationDto.getAssociationType());

        associationSvc.save(association);
        personSvc.save(person);
        return ResponseEntity.ok(person.getAssociations().stream()
                .map(assoc -> mapper.map(assoc, AssociationDto.class))
                .collect(Collectors.toList()));
    }

    /**
     * Dissassociate Organization to Person.
     *
     * @param personId
     * @param organizationId
     * @return ResponseEntity<HttpStatus>
     * @throws ResourceNotFoundException
     */
    @PreAuthorize("hasPermission('person/organization', 'DISASSOCIATE')")
    @DeleteMapping("/person/{personId}/organization/{organizationId}")
    public ResponseEntity<HttpStatus> deleteOrgAssociation(
            @PathVariable(value = "personId") final UUID personId,
            @PathVariable(value = "organizationId") final UUID organizationId)
            throws ResourceNotFoundException {
        log.info("Deleting Organization association to Person with id:......"
                + personId);

        Person person = personSvc.get(personId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PERSON_NOT_FOUND + personId));

        Association association = person.getAssociations().stream()
                .filter(assoc -> assoc.getOrganization().getId()
                        .equals(organizationId))
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No association found for org"));
        person.getAssociations().remove(association);
        associationSvc.delete(association.getId());
        personSvc.save(person);
        return new ResponseEntity<HttpStatus>(HttpStatus.NO_CONTENT);
    }
}
