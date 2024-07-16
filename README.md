# Dropbox Sign Java App

Java app example using the Dropbox Sign API.

## Requirements

- Java 8 or higher
- Maven or Gradle build tool

## Dependencies

### Maven

Add the following dependencies to your `pom.xml` file:

```xml
<dependencies>
    <dependency>
        <groupId>com.dropbox.sign</groupId>
        <artifactId>dropbox-sign</artifactId>
        <version>2.0.0</version>
        <scope>compile</scope>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.13.4</version>
    </dependency>
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>3.0.1</version>
    </dependency>
    <dependency>
        <groupId>org.apache.commons</groupId>
        <artifactId>commons-io</artifactId>
        <version>1.3.2</version>
    </dependency>
</dependencies>
```
### Gradle users

Add this dependency to your project's build file:

```groovy
  dependencies {
    implementation 'com.dropbox.sign:dropbox-sign:2.0.0'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.13.4'
    implementation 'org.apache.pdfbox:pdfbox:3.0.1'
    implementation 'org.apache.commons:commons-io:1.3.2'
}
```

## Installation / Deployment

To install the API client library to your local Maven repository, simply execute:

```shell
mvn clean install
```

To deploy it to a remote Maven repository instead, configure the settings of the repository and execute:

```shell
mvn clean deploy
```

## How to use
IntelliJ IDEA
To build and run the project using IntelliJ IDEA:

- Open IntelliJ IDEA.
- Click on File > Open and select the project directory.
If using Maven, ensure that IntelliJ has recognized the pom.xml file and downloaded the dependencies. If using Gradle, ensure the same for build.gradle.
 - Set up your Dropbox Sign API_KEY and CLIENT_ID in your environment or configuration files.
 - Open the Main class file where your main method is located.
Click the Run button or right-click in the code editor and select Run 'Main'.
## Notes
- Ensure you have set up your Dropbox Sign API_KEY and CLIENT_ID in your environment or configuration files.
- Refer to the Dropbox Sign API Documentation for more details on how to use the API.
- For any issues or questions, consult the Dropbox Sign API Support.
Feel free to modify the content according to your specific project setup and requirements.
