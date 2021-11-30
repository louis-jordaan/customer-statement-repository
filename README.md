# customer-statement-repository

Spring Boot and Spring Batch application

Accepts input files (in XML or CSV format) containing customer statement records, and then performs the following validations:
  - Checks that all transaction references are unique
  - Checks that the end balance after each transaction record is correct (i.e. start balance + mutation = end balance)
  - Performs a basic IBAN validation check on the bank account number

There are 2 possible modes for the application to run:
  - REST API running on web container secured with JWT
  - Console application

Maven wrapper is included in the repository. To compile the source, execute the following commands...

**Windows:** mvnw clean install 

**Linux:** ./mvnw clean install 


## REST API / Web application

The application requires the **JWT_TEST_USER** and **JWT_TEST_PASSWORD** environment variables to be set in order to start up as a web application.
You can start it any of the following commands (remember to replace the question marks with values)
  
**Windows:** mvnw spring-boot:run -Dspring-boot.run.arguments="--JWT_TEST_USER=? --JWT_TEST_PASSWORD=?"

**Linux:** ./mvnw spring-boot:run -Dspring-boot.run.arguments="--JWT_TEST_USER=? --JWT_TEST_PASSWORD=?"

## Console application

To launch the application in console mode, you need to pass "-cm" or "--console-mode" as an argument.
You then also, as a minimum, need to provide an input file that must be processed. The file must meet the same requirements as described in the Postman "Upload Statement" paragraph below. To specify an input file you must pass a "-i" or "--input-file" argument.

**Windows:** mvnw spring-boot:run -Dspring-boot.run.arguments="-cm -i ?"

**Linux:** ./mvnw spring-boot:run -Dspring-boot.run.arguments="-cm -i ?"

Available arguments:
Short Version | Long Version | Description
--- | --- | ---
-cm | --console-mode | Start application in console mode.
-i | --input-file | Path to the input file.
-o | --output-file | Path for the output file, the results will be written to this file.
-a | --async | The application will process the input file asynchronously when this flag is passed.


## Environment variables
Name | Default Value | Description
--- | --- | ---
MULTI_PART_MAX_FILE_SIZE | 100MB | Maximum filesize accepted by the REST API.
MULTI_PART_MAX_REQ_SIZE | 100MB | Maximum multi-part request accepted by the REST API.
PROC_VAL_STORAGE_DIR | /tmp | Directory where the application will save files that were uploaded, and the output files of processing.
JWT_AUTH_ISSUER | Customer Statement Processor | The issuer of the JWT token.
JWT_AUTH_SECRET | Refer to the application.yml configuration file in the source resources | Value of the key that must be used to sign issued JWT tokes.
JWT_AUTH_AUDIENCE | Jordaan | The audience for the JWT token.
JWT_AUTH_TTL_SECS | 600 | How long (in seconds) an issued JWT token will be valid before it expires.
JWT_TEST_USER | admin | The username for the test user ***The application currently uses an in-memory user details service for authentication. Please change this to user your own user details service**
JWT_TEST_PASSWORD | 5ZKMoY4XM | The password for the test user ***The application currently uses an in-memory user details service for authentication. Please change this to user your own user details service**
ENABLE_WEB_INTERACTIONS_LOGGING | true | Flag to indicate whether requests to and responses from the REST API should be logged (for auditing or debugging purposes).

## Postman 

Import the **customer-statement-processor.postman-tests-collection.json** (located in the root of the repository) into Postman to get a collection of  requests that can be used to interact with the application when it is started as a web application.
  - **Get Token**: Authenticate with the application to get a JWT token, that will be required for the other requests below. Grab the token from the response header and set it on the parent container so that the requests below can inherit it. Use the same values that was set for the **JWT_TEST_USER** and **JWT_TEST_PASSWORD** environment variables as the username and password in the JSON request to the REST API.
  - **Upload Statement**: Upload an XML or CSV file in the predefined format for validation by the statement processor. Look at the records.csv and records.xml files in the test resources directory for examples of what the expect CSV and XML structure is. If the request is successful, the application will respond with an execution id, which can be used to obtain the status, details and results of the processing request. **Note: the maximum allowed file size is 100MB. This can be changed in the application config.**
  - **Get Status**: Get the status of the processing request, i.e. whether it is running, completed, failed, etc. Processing is done asynchronously when the application runs in batch mode. 
  - **Get Details**: Get some details (including status) of a processing request.
  - **Download Results**: Downloads the result of a processing request if the application has completed processing the input file.

## Miscellaneous

**Spring actuator** health check URL: http://localhost:8080/actuator/health
**Swagger docs URL**: http://localhost:8080/swagger-ui.html
**OpenAPI description**: http://localhost:8080/v3/api-docs


## Note
Java version used during development and testing: 
  - openjdk version "11.0.5" 2019-10-15 LTS
  - OpenJDK Runtime Environment 18.9 (build 11.0.5+10-LTS)
  - OpenJDK 64-Bit Server VM 18.9 (build 11.0.5+10-LTS, mixed mode)
