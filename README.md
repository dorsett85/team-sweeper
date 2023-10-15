# team-sweeper

### Getting Started

Software requirements

1. Java 17
2. Mysql 8
3. Node 18
4. Yarn

Create a database named `team_sweeper` with a user `clayton` that has all
privileges. Run all the mysql scripts in `src/db` folder.

Startup

```shell
# Install npm packages and build static files
yarn
yarn build

# Build and start the web server
./gradlew bootRun

# For Gradle builds in production run
./gradlw clean build
java -jar build/libs/team-sweeper-0.0.1-SNAPSHOT.jar
```