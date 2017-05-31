# EBUT 2017

* [assignment](/spec/assignment_3.pdf)
* [checklist](/spec/checklist_assignment-3.pdf)

##Setup

##### Initialize Database
* install a mysql locally
* create new user and new database
* change line 100 in the build.xml to the needed file (bash or batch)
* run the ant task for creating the tables

##### Database Commands
* CREATE USER 'manager' IDENTIFIED BY 'manager'
* GRANT ALL PRIVILEGES ON * . * TO 'manager';

_______________

## Dependencies

* [Apache Commons IO 2.5](https://commons.apache.org/proper/commons-io/download_io.cgi)
* [Apache Commons FileUpload 1.3.2](https://commons.apache.org/proper/commons-fileupload/download_fileupload.cgi)
