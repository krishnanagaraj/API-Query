API-Query
=========

This service is tested on Windows 8, installed with git version 1.9.0, and Apache Maven 3.0.4

Pre requisite for running the service
-------------------------------------

1) A system should have a stable internet connection

2) git should be installed on your system. If it's not installed, refer :

https://help.github.com/articles/set-up-git
You should have a read access to clone the public repository from github.

3) Apache maven should be installed on your system. If it's not installed, refer :

 http://maven.apache.org/download.cgi
 
Setting up the project
----------------------

It's a GoEuro service. To run the project, follow the instructions

1) First clone the repository in your local system
   git clone https://github.com/krishnanagaraj/API-Query.git
   
2) Run the following command

   sudo mvn clean; sudo mvn package; sudo mvn jetty:run
   
Testing the service
-------------------

We need to use the command line for testing.

Following are sample request and output:

java -jar GoEuroTest.jar India

Successfully created locations.csv file. File can be found here: C:\..\..

For any assistance please reach out to me at
--------------------------------------------

krishna.nagaraj@yahoo.com
