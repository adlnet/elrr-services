
# ELRR Services

This component of ELRR system houses the Learner API to allow the reading and writing of the P2997 data stored in ELRR Learner Profile. 

There are database dependencies, but there's a [repo with a docker-compose](https://github.com/adlnet/elrr-dockercompose) that resolves the db locally.

## API Inventory
The API Endpoints are documented interactively by swagger/openapi. Launch the application (locally or otherwise) and go to:

`http[s]://[host]:[port]/swagger-ui/index.html`

## Dev Requirements
- [Java JDK 17](https://www.oracle.com/java/technologies/downloads/) or later
- [Maven](https://maven.apache.org/)
- Make (optional for easy targets, otherwise just look at `Makefile` to get relevant target commands)

## Tools
- Database Client (can use postgres CLI, pgadmin, or a 3rd party client like [DBeaver](https://dbeaver.io/))
- REST Client to test endpoints (e.g. Postman, RapidAPI, or just cURL)

## Running the Application

### 1. Build the application
Using `make`:

- `make build`

or use maven:

- `mvn clean install`

### 2. Start and Configure the Database

You will need a running PostgreSQL database containing the schema in [Service Entities](https://github.com/adlnet/elrr-services-entities/blob/main/dev-resources/schema.sql).

You will also need to configure the app properties/ENV to point to that database (See **Properties and Environment Variables** below)

One option is to use the ELRR [Local Development Docker Compose](https://github.com/adlnet/elrr-dockercompose) which runs all of the appropriate dependencies with the connection details already in `application-local.properties`, but it does not seed the database with schema, so you will still need to use a DB client to run Service Entities' `schema.sql` against the `service-db` container's database.

### 3a. Running the application using the Spring Boot Maven plugin: 
This is the recommended and easiest way to run a local version of the application

- `mvn spring-boot:run -D spring-boot.run.profiles=local -e`  (Linux/MacOS)

or

- `mvn spring-boot:run -D"spring-boot.run.profiles"=local -e` (Windows)

or (if you have `make` installed)

- `make dev`

or

- `make debug` - same but with an open debug port to attach to


Note that profile is being set to `local`, this tells spring to leverage `src/main/resources/application-local.properties` which allows you to easily change system settings for your local run. See **Properties and Environment Variables** for details.

### 3b. Running the application using the Jar file
This is closer to how the application will run in a Docker Container or in production.

- `cd target/`
- `java -jar elrrservices-_.jar` (you must fill in the version number that matches the current target build)

To configure launch for this method you will set ENV variables instead of tweaking `application-local` as the jar will default to `application.properties` which accepts ENV overrides.

## Properties and Environment Variables
Configuration variables for running the application

| Property | ENV Variable | Default | Description |
| -------- | -------- | -------- | -------- |
| spring.datasource.url (partly, jdbc url)  | PGHOST | - | PostgreSQL Server Host
| spring.datasource.url (partly, jdbc url)  | PGPORT | - | PostgreSQL Server Port
| spring.datasource.url (partly, jdbc url)  | PG_DATABASE | - | PostgreSQL Database Name
| spring.datasource.username  | PG_RW_USER | - | PostgreSQL Username
| spring.datasource.password  | PG_RW_PASSWORD | - | PostgreSQL Password
| spring.jpa.properties.hibernate.default_schema  | ELRR_DB_SCHEMA | services_schema | Default PostgreSQL Schema
| server.port | ELRR_SERVER_PORT | 8092 | Port to deploy API onto
| admin.jwt.role | ELRR_ADMIN_JWT_ROLE | elrr-admin | Role expected in Admin JWT
| admin.jwt.role-key | ELRR_ADMIN_JWT_ROLE_KEY | group-simple | Key for list of roles in Admin JWT
| admin.jwt.issuer-whitelist | ELRR_ADMIN_JWT_ISSUER_WHITELIST | http://example.com | Admin JWT Issuer Whitelist
| admin.jwt.user-id-key | ELRR_ADMIN_JWT_USER_ID_KEY | preferred_username | Key to find username in Admin JWT
| api.jwt.issuer | ELRR_API_JWT_ISSUER | http://elrr.example.com | Issuer for API JWT
| api.jwt.user-id-key | ELRR_API_JWT_USER_ID_KEY | token-creator | Key to find user-id of API Key creator
| client.jwt.secret | CLIENT_JWT_SECRET | - | JWT Secret Key for Clusters
| client.admin-api-override | ELRR_ADMIN_API_OVERRIDE | false | Option wherein Admin users can access API endpoints, otherwise they can only access token management endpoints

## Dev Helpers / Notes

### Run a dependency scan
The OWASP dependency scan was disabled because of extremely long build times, but if you want to save yourself some trouble in security scanning later you can run:

`make dependency-scan`

### Linting
Linting is live and will notify you during build. To run it on its own try:

`make lint`

### Authentication
Please see the [Auth Documentation](docs/auth.md) for instructions on token usage.

### Building the OpenAPI JSON file
When making changes to the API you should rebuild [the OpenAPI description file](docs/api/openapi.json). Set up to run the application in development, then use:

`make openapi`
