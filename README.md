# customer-statement-repository

Spring Boot and Spring Batch application

Accepts input files (in XML or CSV format) containing customer statement records, and then performs the following validations:
  - Checks that all transaction references are unique
  - Checks that the end balance after each transaction record is correct (i.e. start balance + mutation = end balance)
  - Performs a basic IBAN validation check on the bank account number

There are 2 possible modes for the application to run:
  - REST API running on web container
  - Console application

## REST API / Web application



## Console application


### Note:
Java version used during development and testing: 
  openjdk version "11.0.5" 2019-10-15 LTS
  OpenJDK Runtime Environment 18.9 (build 11.0.5+10-LTS)
  OpenJDK 64-Bit Server VM 18.9 (build 11.0.5+10-LTS, mixed mode)
