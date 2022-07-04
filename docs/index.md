# SmartCLIDE Service Creation
SmartCLIDE Service Creation Backend Component

## Preconditions to build and run Service Creation

To build and run the backend service of Service Creation, the following software is required:

- Java (at least version 8)
- Apache Maven (at least version 3.2+)
- Docker (for building and running the final image)

## How to build Service Creation

Service Creation can be built using maven with the following command:

```shell
mvn install
```

In order to build a Docker image of the service that can be deployed, the following commands can be used:

```shell
mvn install
docker build -t ${IMAGE_NAME:IMAGE_TAG} .
```

More specifically:

```shell
mvn install
docker build -t smartclide-service-creation:latest .
```

## How to run Service Creation

All the images of this component can be found [here](https://github.com/eclipse-researchlabs/smartclide-service-creation/pkgs/container/smartclide%2Fservice-creation).

You can run the backend service with the following command:

```shell
docker run smartclide-service-creation:latest
```

## How to use Service Creation

This backend service leverages the provided GitLab and Jenkins APIs in order to authenticate a User and create a new repository based on the options selected by the User. Furthermore, depending on the User’s choices, the newly created repository can be paired with a Jenkins CI/CD. 
The component creates a new Jenkins CI/CD pipeline and then performs the necessary configuration actions (webhooks, ect.) in order to complete the pairing process. As a result, the process is completed automatically, thus sparing the User from the manual use and configuration of the external tools.

The service includes two endpoints:

**Endpoint 1:** "/createStructure" -> creates a GitLab repository.    
**Request parameters:**    
**projectName** -> String    
**projVisibility** -> String    
**projDescription** -> String    
**gitLabServerURL** -> String    
**gitlabToken** -> String


**Endpoint 2:** "/createStuctureJenkins" -> creates a GitLab repository and a Jenkins pipeline and finaly configures and pairs them.    
**Request parameters:**    
**projectName** -> String    
**projVisibility** -> String    
**projDescription** -> String    
**gitLabServerURL** -> String    
**gitlabToken** -> String    
**jenkinsServerUrl** -> String    
**jenkinsUsername** -> String    
**jenkinsToken** -> String    
