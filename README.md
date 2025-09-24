
# ELRR Services
An API to allow the reading and writing of the P2997 data stored in ELRR Learner Profile. 

There are database dependencies, but there's a [repo with a docker-compose](https://github.com/US-ELRR/elrrdockercompose/) that resolves the db locally.

## API
The API Endpoints are documented interactively by swagger/openapi. Launch the application (locally or otherwise) and go to:

`http[s]://[host]:[port]/swagger-ui/index.html`

## Dependencies
- Java 17
- [Maven](https://maven.apache.org/)
- [Docker](https://www.docker.com/products/docker-desktop/)
- [PostgreSQL](https://www.postgresql.org/download/) (using docker is easiest here for local dev)
- Make (optional for easy targets, otherwise just look at `Makefile` to get relevant target commands)

## Tools
- SQL client or Terminal (to interrogate db)
- REST Client to test endpoints

## Build the application
`make build`

## Run a dependency scan
The OWASP dependency scan was disabled because of extremely long build times, but if you want to save yourself some trouble in security scanning later you can run:

`make dependency-scan`

## Linting
Linting is live and will notify you during build. To run it on its own try:

`make lint`

## Running the application locally
There are make targets for a number of run modes:

- `make dev` - local run mode using application-local.properties config
- `make debug` - same but with an open debug port to attach to

Please see the [Auth Documentation](docs/auth.md) for instructions on token usage.

## Building the OpenAPI JSON file
When making changes to the API you should rebuild [the OpenAPI description file](docs/api/openapi.json). Set up to run the application in development, then use:

`make openapi`
