# Chapter 6 — Service discovery with Spring Cloud Eureka

Spring Microservices in Action, chapter 6 (Final). Four Spring Boot services plus
a Postgres database, built as container images and run with Compose.

| Module                 | Port | Purpose                                              |
|------------------------|------|------------------------------------------------------|
| `configserver`         | 8071 | Spring Cloud Config server, git-backed (this repo)   |
| `eurekaserver`         | 8070, 8072, 8073 | Eureka registry, three peer-replicated instances (`eurekaserver1..3`) |
| `licensing-service`    | 8080 | Licensing API; calls the organization service via Eureka |
| `organization-service` | 8081 | Organization API                                     |
| `docker/`              |      | `docker-compose.yml`, DB init and seed SQL           |

## Configuration

The config server reads its files from this repository on GitHub
(`configserver/src/main/resources/bootstrap.yml`, profile `git`,
`search-paths: configserver/src/main/resources/config`). Edit a file under that
folder, commit and push, then restart the affected service.

Set `spring.profiles.active` to `native` in the same file to read the config
from the classpath instead.

## Highly-available Eureka

Three instances of the same Eureka image run as peers. Each container sets
`SPRING_PROFILES_ACTIVE=peerN`, which makes the config server merge
`eureka-server.yml` with `eureka-server-peerN.yml`: the latter sets the instance
hostname (`eurekaserverN`) and points `defaultZone` at the other two peers. The
peers register with each other and replicate their registries, and the licensing
and organization services list all three peers in their `defaultZone`, so losing
one or two Eureka instances does not stop service discovery.

Dashboards: http://localhost:8070, http://localhost:8072, http://localhost:8073.
Each should list `EUREKA-SERVER` with three instances plus the two business
services, and show the other peers under "DS Replicas".

## Build

Requires JDK 11+ and Maven. Images are built with the fabric8 docker-maven-plugin,
which works with Docker or Podman.

```sh
./build.sh                      # mvn clean package docker:build (starts Podman if needed)
./build.sh clean package        # pass any other goals straight to mvn
```

## Run

```sh
cd docker
podman compose up -d            # or: docker compose up -d
podman compose logs -f
podman compose down
```

## Try it

- Eureka dashboards: http://localhost:8070, http://localhost:8072, http://localhost:8073
- Organization: http://localhost:8081/v1/organization/e6a625cc-718b-48c2-ac76-1dfdff9a531e
- License: http://localhost:8080/v1/organization/d898a142-de44-466c-8c88-9ceb2c2429d3/license/f2a9c9d4-d2c0-44fa-97fe-724d77173c62
- Config served to a service: http://localhost:8071/licensing-service/dev
