spring-xd-integration tests
===========================

* git clone git@github.com:SpringSource/spring-shell.git
* cd spring-shell
* ./mvn clean package install
* cd ..
* git clone git@github.com:parikhkc/spring-xd-integration-tests.git
* cd spring-xd-integration-tests
* ./gradlew cleanEclipse eclipse
* Import the project as Existing Project in STS
* Start xd-singlenode
* Run XDShellTest as jUnit test
