# EBUT 2017

* [assignment](/spec/assignment_3.pdf)
* [checklist](/spec/checklist_assignment-3.pdf)

## To do

**mirko**:
* xslt transformation bmecat to xhtml


**basti**:
* import -> check wellformed / valid
* import -> check if supplier exists
* import -> check if articles exists

* all export functions for different roles (because of the different prices)
* export -> xml (all / selective)
* export -> xhtml 
_______________

##Database 

##### Initialize Database
* install a mysql locally
* create new user and new database
* change line 100 in the build.xml to the needed file (bash or batch)
* run the ant task for creating the tables

##### Database Commands
* CREATE USER 'manager' IDENTIFIED BY 'manager'
* GRANT ALL PRIVILEGES ON * . * TO 'manager';

##### SQL Data
* additional suppliers are available in /install/ebus_add.sql


_______________

## Dependencies

* [Apache Commons IO 2.5](https://commons.apache.org/proper/commons-io/download_io.cgi)
* [Apache Commons FileUpload 1.3.2](https://commons.apache.org/proper/commons-fileupload/download_fileupload.cgi)

_______________


## Checklist

##### General:
* Navigation
* User Roles
* Usability
* Comments in code


##### Import 
* ~~Web Upload (incl. No file chosen: User error message)~~
* Validation: Not WF XML -> User Feedback (Error Message)
* Validation: Not Valid XML -> User Feedback (Error Message)
* Supplier not in DB -> User Feedback (Error Message)
* Import
  * Valid XML
  * DB ok
  * Supplier in DB
  * Import successful?
* Update Semantic
  * User Feedback (Status Messages)
  
##### Export
* ~~Export XML > Web Download~~
* ~~Export XHTML > Web Download~~
* XML Export > XML all
* XML Export > XML selective
* Export XML > Valid XML Export (Schema validation)
* Re-import of Exported XML File
* Export XHTML > Valid XHTML (W3C Validator)
