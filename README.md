# Course Registration Application

## 1. Project Overview

This project is a Java 17 course-registration application built with Maven.
It uses a multi-module Maven structure, JUnit 5 tests, a Jenkins CI pipeline,
Docker image creation, and a Minikube Kubernetes deployment exercise.

The assigned change request, Question Set F, adds an in-memory audit log
to record successful student registrations.

## 2. Setup and Execution Instructions

### Prerequisites

- Java 17
- Maven
- Git
- Docker
- Jenkins configured with JDK 17 and Maven tools
- Minikube and kubectl for the Kubernetes exercise

### Build and Test

Run these commands from the repository root:

```bash
mvn clean test
mvn clean verify
mvn package
```

The `verify` command compiles the project, runs the tests, and executes
the applicable verification lifecycle phases.

### Docker Image

Build the JAR before building the Docker image:

```bash
mvn clean verify
docker build -t student-starter:latest .
docker images student-starter
docker run --rm student-starter:latest
```

The current Dockerfile copies the generated JAR into an image based on
Java 17 and executes `java -version`. It verifies the Java runtime but
does not launch a registration web service.

### Kubernetes Deployment

Apply the Kubernetes manifests:

```bash
kubectl apply -f Task6-Minikube/deployment.yaml
kubectl apply -f Task6-Minikube/service.yaml
```

Check the Deployment, Pods, Service, and endpoints:

```bash
kubectl rollout status deployment/nginx-deployment
kubectl get deployments
kubectl get pods -o wide
kubectl get services
kubectl get endpoints nginx-service
```

Access the Nginx Service using:

```bash
minikube service nginx-service --url
```

The URL printed by Minikube can be opened in a browser to verify the
Nginx welcome page.

### Scaling and Self-Healing

Scale the Deployment to three replicas:

```bash
kubectl scale deployment nginx-deployment --replicas=3
kubectl get deployments
kubectl get pods -o wide
```

Delete one of the actual Pod names returned by `kubectl get pods`:

```bash
kubectl delete pod <actual-pod-name>
kubectl get pods -w
```

Wait for the replacement Pod to become ready, then press Ctrl+C.
Verify the final state:

```bash
kubectl get deployments
kubectl get pods -o wide
```

## 3. Git Evidence

The repository history includes commits relating to the following work:

- `Add successful registration audit log`
- `Complete modularization, capacity fix, and audit log`
- `Update Jenkins declarative pipeline`
- `Add Docker build to Jenkins pipeline`
- `Add Task 6 Kubernetes deployment and service manifests`

The repository's main branch and the current feature branch should be
verified using:

```bash
git branch
git log --oneline -5
git status
```

The documentation changes for this submission are maintained on the
`feature/task7-documentation` branch.

## 4. Maven Configuration and Build Evidence

### Parent-Child POM Structure

The root `pom.xml` defines the parent Maven project:

- Group ID: `com.university`
- Artifact ID: `course-registration`
- Version: `1.0-SNAPSHOT`
- Packaging: `pom`

It declares `course-registration-app` as a module and centralizes
shared properties, JUnit version management, and plugin configuration.

The child `course-registration-app/pom.xml` inherits from the parent,
uses JAR packaging, and declares JUnit Jupiter as a test dependency.

Java compilation targets release 17. The configured Maven plugins include
the Maven Compiler Plugin and Maven Surefire Plugin.

### Build and Test Results

A local `mvn clean verify` run completed successfully.

Observed results:

- `RegistrationServiceTest`: 12 tests
- `CourseTest`: 3 tests
- Total: 15 tests
- Failures: 0
- Errors: 0
- Skipped: 0
- Maven result: `BUILD SUCCESS`

The generated JAR was:

`course-registration-app/target/course-registration-app-1.0-SNAPSHOT.jar`

These results represent the local Maven run. Jenkins results should be
documented separately after inspecting the relevant Jenkins build.

## 5. Jenkins Pipeline

The Jenkins pipeline contains these stages:

1. **Checkout** — checks out the configured source repository.
2. **Build and Test** — runs `mvn clean verify`.
3. **Archive Artifact** — archives the generated JAR and fingerprints it.
4. **Docker Build** — builds the Docker image using the repository Dockerfile.

The Build and Test stage has a `post { always { ... } }` action that
publishes JUnit XML reports using:

`course-registration-app/target/surefire-reports/TEST-*.xml`

The `always` condition allows Jenkins to publish reports even when the
build or tests fail, provided the report files were generated.

If `mvn clean verify` fails, the shell step fails the stage. In a normal
Declarative Pipeline, later stages such as Archive Artifact and Docker
Build are skipped unless failure handling explicitly changes that behavior.

The JUnit report publisher may itself mark the build unstable or failed
depending on the test results and Jenkins configuration.

## 6. Kubernetes Deployment Evidence

The Kubernetes manifests are stored in:

- `Task6-Minikube/deployment.yaml`
- `Task6-Minikube/service.yaml`

The Deployment uses the `nginx:1.27` image and labels Pods with
`app: nginx`. The Deployment selector and the Service selector use
this same label.

The Service uses NodePort `30080`, forwarding traffic to container port 80.

### Deployment and Scaling

Observed terminal output showed the Deployment with three ready replicas:

```text
NAME               READY   UP-TO-DATE   AVAILABLE
nginx-deployment   3/3     3            3
```

The Pod list showed three Nginx Pods in `Running` state with `1/1`
readiness. Their Pod IPs included `10.244.0.3`, `10.244.0.4`,
and `10.244.0.5`.

The Service output showed:

```text
nginx-service   NodePort   10.102.197.22   <none>   80:30080/TCP
```

Minikube printed the Service URL:

`http://192.168.49.2:30080`

### Self-Healing

A running Pod was deleted with `kubectl delete pod`. The subsequent
Pod list showed a new Pod named:

`nginx-deployment-75cc477c85-7f2w9`

The output showed the replacement Pod in `Running` state with `1/1`
readiness, while the total number of Deployment Pods remained three.

The Deployment controller compares the desired replica count with the
actual state. When a Pod is deleted and the number of replicas falls
below the desired count, Kubernetes creates a replacement Pod.

Additional final verification output can be captured with:

```bash
kubectl get deployments,pods,services -o wide
kubectl get endpoints nginx-service
kubectl describe deployment nginx-deployment
```

## 7. Change Summary and Limitations

### Assigned Change Request: Question Set F

The RegistrationService maintains an in-memory audit log for successful
registrations.

Each successful registration adds one entry in this format:

`Student <studentId> registered for course <courseCode>`

Example:

`Student S301 registered for course CS501`

The audit entry is added after the registration checks pass and the
student registration and course enrollment are updated.

Rejected registrations do not create audit entries. A failed attempt
does not remove previous entries. The getter returns an unmodifiable
view of the audit log.

### Tests

The registration tests cover:

- Successful registration and audit entry creation.
- Rejected registration without an audit entry.
- Preservation of previous entries when a later attempt fails.
- Existing registration rules, including course activity, capacity,
  prerequisites, duplicate registration, and credit limits.

### Modified and Supporting Files

Relevant files include:

- `course-registration-app/src/main/java/com/university/RegistrationService.java`
- `course-registration-app/src/test/java/com/university/RegistrationServiceTest.java`
- `Jenkinsfile`
- `Dockerfile`
- `Task6-Minikube/deployment.yaml`
- `Task6-Minikube/service.yaml`
- `README.md`

Confirm the exact files included in the final change using `git status`
and `git diff --stat`.

### Known Limitations

- The audit log is stored in memory and is lost when the service instance
  is discarded or the process restarts.
- The audit log is not backed by a database or external logging service.
- The Docker image currently verifies the Java runtime; it does not
  launch a standalone registration application.
- The Kubernetes exercise deploys Nginx, not the course-registration
  application.
- The Kubernetes Service is configured as a NodePort for local testing.
