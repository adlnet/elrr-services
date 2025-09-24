.phony: dev, clean, debug, test, test-debug, dependency-scan, lint, build, openapi

clean:
	mvn clean

dev:
	mvn spring-boot:run -D spring-boot.run.profiles=local -e

debug:
	mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000" -D spring-boot.run.profiles=local -e

test:
	mvn test

test-debug:
	mvn -Dmaven.surefire.debug="-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=localhost:8000" test

dependency-scan:
	mvn dependency-check:check

lint:
	mvn checkstyle:check

build:
	mvn clean package

openapi:
	bash scripts/generate_openapi.sh
