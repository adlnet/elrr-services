Services Outline

* = done

## Person

*GET /person
- list of all people
- ifi param

*GET /person/[id]
- single person including address rels, phone, email

*POST /person
- Save new person. Included addresses might be IDs, or full address DTOs without IDs (which will save new location objects).

*PUT /person/[id]
- update and return person

*DELETE /person/[id]

#### Person-Identity
*GET /person/[id]/identity

*GET /person/[id]/identity/[id]

*POST /person/[id]/identity

*DELETE /person/[id]/identity/[id]

#### Person-Org

*GET /person/[id]/organization
- All orgs

*GET /person/[id]/organization/[id]
- returns rel if exists

*POST /person/[id]/organization/[id]
- Body only contains relationship_type
- Establishes rel, returns rel

*PUT /person/[id]/organization/[id]
- Updates rel_type
- Returns rel

*DELETE /person/[id]/organization/[id]
- Removes Rel, return success

#### Person-Phone

*GET /person/[id]/phone
- a person's phone numbers

*POST /person/[id]/phone
- Creates a new phone and adds it to the person
- Returns phone

*POST /person/[id]/phone/[id]
- no body
- associates a person with an existing phone, returns phones

*DELETE /person/[id]/phone/[id]
- Removes association

#### Person-Email

*GET /person/[id]/email
- a person's emails

*POST /person/[id]/email
- Creates a new email and adds it to the person
- Returns email

*POST /person/[id]/email/[id]
- no body
- associates a person with an existing email, returns emails

*DELETE /person/[id]/email/[id]
- Removes association

#### Person-Learning Record

*GET /person/[id]/learningrecord
- a person's learning records

*POST /person/[id]/learningrecord
- Creates a new learning record for the person
- Returns learning records

#### Person-Military Record

*GET /person/[id]/militaryrecord
- a person's military records

*POST /person/[id]/militaryrecord
- Creates a new military record for the person
- Returns military records

#### Person-Employment Record

*GET /person/[id]/employmentrecord
- a person's employment records

*POST /person/[id]/employmentrecord
- Creates a new employment record for the person
- Returns employment records

#### Person-Comp

The calls below are the same for /competency and /credential

*GET /person/[id]/competency
- list learner competencies

*GET /person/[id]/competency/[id]
- returns rel if exists

*POST /person/[id]/competency/[id] (& credential)
- may contain hasRecord

*PUT /person/[id]/competency/[id]
- Updates hasRecord
- Returns rel

?DELETE /person/[id]/competency/[id]
- Removes Rel, return success

## Learning Resource

*GET /learningresource
- All learning resources
- Might have query params

*POST /learningresource
- Create a new learning resource

*GET /learningresource/[id]
- Get one

*PUT /learningresource/[id]
- update learning resource by id.

*DELETE /learningresource/[id]
- delete learning resource by id

## Learning Record

*GET /learningrecord
- All learning records
- Might have query params

*GET /learningrecord/[id]
- Get one

*PUT /learningrecord/[id]
- update learning record by id. Cannot change learner or resource

*DELETE /learningrecord/[id]
- delete learning record by id

## Employment Record

*GET /employmentrecord
- All employment records
- Might have query params

*GET /employmentrecord/[id]
- Get one

*PUT /employmentrecord/[id]
- update employment record by id. Cannot change learner or employer

*DELETE /employmentrecord/[id]
- delete employment record by id

## Military Record

*GET /militaryrecord
- All military records
- Might have query params

*GET /militaryrecord/[id]
- Get one

*PUT /militaryrecord/[id]
- update military record by id. Cannot change learner or employer

*DELETE /militaryrecord/[id]
- delete military record by id

## Organization

*GET /organization

*GET /organization/[id]

GET /organization/[id]/person

GET /organization/[id]/facility

*POST /organization

*PUT /organization/[id]

*DELETE /organization/[id]

## Comp / Creds

*GET /competency (& /credential)

*GET /competency/[id]    

GET /competency/[id]/people

*POST /competency

*PUT /competency/[id]

*DELETE /competency/[id]

## Facility

*GET /facility

*GET /facility/[id]

*POST /facility

*PUT /facility/[id]

*DELETE /facility/[id]

## Location

*GET /location

*GET /location/[id]

*POST /location

*PUT /location/[id]

*DELETE /location/[id]

## Phone

*GET /phone

*GET /phone/[id]

*POST /phone

*PUT /phone/[id]

*DELETE /phone/[id]

## Email

*GET /email

*GET /email/[id]

*POST /email

*PUT /email/[id]

*DELETE /email/[id]
