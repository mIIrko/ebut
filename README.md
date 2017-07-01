# EBUT 2017

* [assignment 3](/spec/assignment_3.pdf)
* [checklist  3](/spec/checklist_assignment-3.pdf)
_______________

## Database 

##### Initialize Database
* install a mysql locally
* create new user and new database
* change line 100 in the build.xml to the needed file (bash or batch)
* run the ant task for creating the tables

##### Database Commands
* CREATE USER 'manager' IDENTIFIED BY 'manager'
* GRANT ALL PRIVILEGES ON * . * TO 'manager';

##### SQL Data
* additional supplier are available in /install/ebus_add.sql


_______________

## Dependencies

* [Apache Commons IO 2.5](https://commons.apache.org/proper/commons-io/download_io.cgi)
* [Apache Commons FileUpload 1.3.2](https://commons.apache.org/proper/commons-fileupload/download_fileupload.cgi)
* [Jersey Multipart 1.19](https://mvnrepository.com/artifact/com.sun.jersey.contribs/jersey-multipart/1.19)
_______________

## Test cases for REST Service

###### Import
* no file selected
* not wellformed
* not valid
* supplier not in db
* article already exists (whats the pk?)

###### Export
* xhtml
* xml
* for each: selective & all
* message, if no article found
