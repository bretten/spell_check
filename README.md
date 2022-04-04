# Github repo

[Click Here](https://github.com/bretten/spell_check)

# How to run

- [Docker](#Docker)
- [Maven](#Maven)
  - [Run using Maven](#run-using-maven)
  - [Create build artifact and run with Java via command line](#Create-build-artifact-and-run-with-java-via-command-line)

## Docker
Make sure [Docker Desktop](https://www.docker.com/) is installed.

From the root of this repo, run:
```
docker build . -t spell-check-bn
docker run -dp 8080:8080 --name spell-check-bn spell-check-bn
```
This will create a tag named `spell-check-bn` and run a container named `spell-check-bn`.

## Maven
Requires [Java](https://www.oracle.com/java/technologies/downloads/) to be installed on the target machine.

Maven build file is included in the root of the repo.

### Run using Maven
Run the project using:
```
./mvnw spring-boot:run
```

### Create build artifact and run with Java via command line
You can also generate a build artifact:
```
./mvnw clean package
```

The output file should be at `target/SpellCheck-0.0.1-SNAPSHOT.jar`. You can run the JAR using:
```
java -jar target/SpellCheck-0.0.1-SNAPSHOT.jar
```