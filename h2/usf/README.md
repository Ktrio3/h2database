# USF Testing Build instructions

## Building the h2database
The Maven repository is old and thus does not work correctly. First, move the ext_backup files to the ext directory alongside the build.sh script.

`cp ext_backup ../ext`

Run `./build.sh compile` and `./build.sh jar`

The database can then run in the background using `java -jar ./bin/h2-1.4.197.jar`

## Building the testing suite
To build manually, run:
`javac -cp "./temp/:./ext/hibernate-core-5.4.21.Final.jar:./ext/javax.persistence-api-2.2.jar:./ext/querydsl-core-4.0.1.jar:./ext/querydsl-sql-4.2.1.jar:./ext/javax.inject-1.jar:" usf/edu/seclab/h2plus/*.java -d temp/`

You can then run the tests using `java -cp "./temp/:./ext/hibernate-core-5.4.21.Final.jar:./ext/javax.persistence-api-2.2.jar:./ext/querydsl-core-4.0.1.jar:./ext/querydsl-sql-4.2.1.jar:./ext/javax.inject-1.jar:" usf.edu.seclab.h2plus.MainTest`

