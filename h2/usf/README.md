# USF Testing Build instructions

## Building the h2database
Run `./build.sh compile` and `./build.sh jar`

The database can then run in the background using `java -jar ./bin/h2-1.4.197.jar`

## Building the testing suite
The Maven repository is old and thus does not work correctly. To build manually, run:
`javac -cp "./temp/:./target/classes/hibernate-core-5.4.21.Final.jar:./target/classes/javax.persistence-api-2.2.jar:./target/classes/querydsl-core-4.0.1.jar:./target/classes/querydsl-sql-4.2.1.jar:./target/classes/javax.inject-1.jar:" usf/edu/seclab/h2plus/*.java -d temp/`

You can then run the tests using `java -cp "./temp/:./target/classes/hibernate-core-5.4.21.Final.jar:./target/classes/javax.persistence-api-2.2.jar:./target/classes/querydsl-core-4.0.1.jar:./target/classes/querydsl-sql-4.2.1.jar:./target/classes/javax.inject-1.jar:" usf.edu.seclab.h2plus.MainTest`

