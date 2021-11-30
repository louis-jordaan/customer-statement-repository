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
Windows: mvnw clean install
Linux: ./mvnw clean install

## REST API / Web application



## Console application


## Postman 

Import the **customer-statement-processor.postman-tests-collection.json** (located in the root of the repository) into Postman to get a collection of  requests that can be used to interact with the application when it is started as a web application.
  - **Get Token**: Authenticate with the application to get a JWT token, that will be required for the other requests below. Grab the token from the response header and set it on the parent container so that the requests below can inherit it.
  - **Upload Statement**: Upload an XML or CSV file in the predefined format for validation by the statement processor. Look at the records.csv and records.xml files in the test resources directory for examples of what the expect CSV and XML structure is. If the request is successful, the application will respond with an execution id, which can be used to obtain the status, details and results of the processing request. **Note: the maximum allowed file size is 100MB. This can be changed in the application config.**
  - **Get Status**: Get the status of the processing request, i.e. whether it is running, completed, failed, etc. Processing is done asynchronously when the application runs in batch mode. 
  - **Get Details**: Get some details (including status) of a processing request.
  - **Download Results**: Downloads the result of a processing request if the application has completed processing the input file.

### Note:
Java version used during development and testing: 
  openjdk version "11.0.5" 2019-10-15 LTS
  OpenJDK Runtime Environment 18.9 (build 11.0.5+10-LTS)
  OpenJDK 64-Bit Server VM 18.9 (build 11.0.5+10-LTS, mixed mode)
