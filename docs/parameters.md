# Notable API Parameters

## Person

### GET /api/person

The GET /api/person endpoint supports the following optional query parameters for filtering persons:

#### Basic Filters
- **id** (UUID[]): Filter by specific person IDs
- **ifi** (String[]): Filter by IFIs (Inverse Functional Identifiers)
- **name** (String[]): Filter by Name, case-insensitive. The wildcard character `%` can be used like `% Smith`
- **emailAddress** (String[]): Filter by email addresses, case-insensitive. The wildcard character `%` can be used like `%@example.com`.
- **phoneNumber** (String[]): Filter by phone numbers, case-insensitive. Ignores all characters except digits.

#### Organization Filters  
- **associatedOrgId** (UUID[]): Filter by organization IDs via association relationships
- **employerOrgId** (UUID[]): Filter by organization IDs via employment relationships

#### Location Filters
- **locationId** (UUID[]): Filter by location IDs present in any location field - returns persons who have any of the specified location IDs in any of their address or location relationships
    - Searches across all location fields: mailing address, physical address, shipping address, billing address, on-campus address, off-campus address, temporary address, permanent student address, employment address, time of admission address, father address, mother address, guardian address, and birthplace

#### Extension Filters
- **hasExtension** (String[]): Filter for persons who have all specified extension keys present in their extensions JSON
- **extensionPath** (String[]): Filter using JSONPath expressions with the `@?` operator - returns persons where all specified paths exist in their extensions JSON
    - Example: `$."https://example.com/clr/achievementType"`
    - Example: `$."https://example.org/openbadges/evidence".url`
    - Example: `$."https://example.net/person/birthDate"`

- **extensionPathMatch** (String[]): Filter using JSONPath predicates with the `@@` operator - returns persons where all specified predicates match in their extensions JSON
    - Example: `$."https://example.com/clr/achievementType" == "Certificate"`
    - Example: `$."https://example.net/person/birthDate" > "1990-01-01"`
    - Example: `$."https://example.org/openbadges/evidence".status == "verified"`
    - Example: `$."https://example.com/clr/creditsEarned" >= 30`

#### Relation Filters
- **competencyId** (UUID[]): Filter by competency IDs via qualification relationships
- **credentialId** (UUID[]): Filter by credential IDs via qualification relationships
- **learningResourceId** (UUID[]): Filter by learning resource IDs via learning record relationships


## Location

### GET /api/location

The GET /api/location endpoint supports the following optional query parameters:

#### Basic Filters
- **id** (UUID[]): Filter by specific location IDs

#### Extension Filters
- **hasExtension** (String[]): Locations that contain all specified extension keys
- **extensionPath** (String[]): All JSONPath expressions must resolve to at least one value in the extensions JSON (uses `@?` operator semantics)
- **extensionPathMatch** (String[]): All JSONPath predicate expressions must evaluate true (uses `@@` operator semantics)


## Organization

### GET /api/organization

The GET /api/organization endpoint supports the following optional query parameters:

#### Basic Filters
- **id** (UUID[]): Filter by specific organization IDs
- **name** (String[]): Filter by organization name, case-insensitive
- **description** (String[]): Filter by organization description, case-insensitive

#### Extension Filters
- **hasExtension** (String[]): Organizations that contain all specified extension keys
- **extensionPath** (String[]): All JSONPath expressions must resolve to at least one value in the extensions JSON (uses `@?` operator semantics)
- **extensionPathMatch** (String[]): All JSONPath predicate expressions must evaluate true (uses `@@` operator semantics)


## Competency

### GET /api/competency

The GET /api/competency endpoint supports the following optional query parameters:

#### Basic Filters
- **id** (UUID[]): Filter by specific competency IDs
- **identifier** (String[]): Filter by competency identifiers
- **identifierUrl** (String[]): Filter by competency identifier URLs
- **code** (String[]): Filter by competency codes

#### Extension Filters
- **hasExtension** (String[]): Competencies that contain all specified extension keys
- **extensionPath** (String[]): All JSONPath expressions must resolve to at least one value in the extensions JSON (uses `@?` operator semantics)
- **extensionPathMatch** (String[]): All JSONPath predicate expressions must evaluate true (uses `@@` operator semantics)


## Credential

### GET /api/credential

The GET /api/credential endpoint supports the following optional query parameters:

#### Basic Filters
- **id** (UUID[]): Filter by specific credential IDs
- **identifier** (String[]): Filter by credential identifiers
- **identifierUrl** (String[]): Filter by competency identifier URLs
- **code** (String[]): Filter by credential codes

#### Extension Filters
- **hasExtension** (String[]): Credentials that contain all specified extension keys
- **extensionPath** (String[]): All JSONPath expressions must resolve to at least one value in the extensions JSON
- **extensionPathMatch** (String[]): All JSONPath predicate expressions must evaluate true


## Employment Record

### GET /api/employmentrecord

The GET /api/employmentrecord endpoint supports the following optional query parameters:

#### Basic Filters
- **id** (UUID[]): Filter by specific employment record IDs
- **position** (String[]): Filter by position, case-insensitive
- **positionTitle** (String[]): Filter by position title, case-insensitive
- **positionDescription** (String[]): Filter by position description, case-insensitive

#### Organization Filters
- **employerOrgId** (UUID[]): Filter by specific employer organization IDs

#### Extension Filters
- **hasExtension** (String[]): Employment records that contain all specified extension keys
- **extensionPath** (String[]): All JSONPath expressions must resolve to at least one value in the extensions JSON
- **extensionPathMatch** (String[]): All JSONPath predicate expressions must evaluate true


## Facility

### GET /api/facility

The GET /api/facility endpoint supports the following optional query parameters:

#### Basic Filters
- **id** (UUID[]): Filter by specific facility IDs

#### Extension Filters
- **hasExtension** (String[]): Facilities that contain all specified extension keys
- **extensionPath** (String[]): All JSONPath expressions must resolve to at least one value in the extensions JSON
- **extensionPathMatch** (String[]): All JSONPath predicate expressions must evaluate true


## Goal

### GET /api/goal

The GET /api/goal endpoint supports the following optional query parameters:

#### Basic Filters
- **id** (UUID[]): Filter by specific goal IDs

#### Extension Filters
- **hasExtension** (String[]): Goals that contain all specified extension keys
- **extensionPath** (String[]): All JSONPath expressions must resolve to at least one value in the extensions JSON
- **extensionPathMatch** (String[]): All JSONPath predicate expressions must evaluate true


## Learning Record

### GET /api/learningrecord

The GET /api/learningrecord endpoint supports the following optional query parameters:

#### Basic Filters
- **id** (UUID[]): Filter by specific learning record IDs
- **recordStatus** (String[]): Filter by specific record statuses

#### Learning Resource Filters
- **learningResourceId** (UUID[]): Filter by specific learning resource IDs

#### Extension Filters
- **hasExtension** (String[]): Learning records that contain all specified extension keys
- **extensionPath** (String[]): All JSONPath expressions must resolve to at least one value in the extensions JSON
- **extensionPathMatch** (String[]): All JSONPath predicate expressions must evaluate true

## Learning Resource

### GET /api/learningresource

The GET /api/learningresource endpoint supports the following optional query parameters:

#### Basic Filters
- **id** (UUID[]): Filter by specific learning resource IDs
- **iri** (String[]): Filter by specific IRIs
- **title** (String[]): Filter by title, case-insensitive
- **subjectMatter** (String[]): Filter by subject matter, case-insensitive

#### Extension Filters
- **hasExtension** (String[]): Learning resources that contain all specified extension keys
- **extensionPath** (String[]): All JSONPath expressions must resolve to at least one value in the extensions JSON
- **extensionPathMatch** (String[]): All JSONPath predicate expressions must evaluate true