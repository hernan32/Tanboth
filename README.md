# Tanboth 2.0

Bot for the game called "Tanoth" (by GameForge) made in Java.
 
##### Functions:
* Auto Quest
* Auto Add Stats
* Auto Dungeon (Soon)
* Auto Illusion Cave (Soon)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites:

Java 11 or Higher. Download an appropriate JDK for your operating system. 
```
https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html
```
Once installed, you can use the `java` command from your command line. 
```
$ java -version
java version "11.0.4" 2019-07-16 LTS
Java(TM) SE Runtime Environment 18.9 (build 11.0.4+10-LTS)
Java HotSpot(TM) 64-Bit Server VM 18.9 (build 11.0.4+10-LTS, mixed mode)
```
You need to set the `JAVA_HOME` environment variable to a JDK 11 installation directory.
```
https://www.baeldung.com/java-home-on-windows-7-8-10-mac-os-x-linux
```

## Deployment
Soon


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [JavaFX](https://openjfx.io/) - GUI
* [Jsoup](https://jsoup.org/) - Game parsing
* [Apache Commons Lang](https://jsoup.org/) - Game parsing supplement/helper
* [MinLog](https://github.com/EsotericSoftware/minlog) - Java logging
* [AWT/SystemTray](https://gist.github.com/jewelsea/e231e89e8d36ef4e5d8a) - System Tray (AWT) to control a JavaFX application
* [Java HTTP Client](https://openjdk.java.net/groups/net/httpclient/intro.html) - Tanoth HTTP Protocol

## Versioning

We use [GitHub](https://github.com/) for versioning. For the versions available, see the [check this.](https://github.com/hernan32/Tanboth)

## Running

Executing JAR File with Console
```
java -jar Tanboth-1.0.jar
``` 
Executing JAR File with Console-less
```
javaw -jar Tanboth-1.0.jar
``` 
Requisites EXE File
```
JRE 1.8 - https://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
``` 

## Configuration

Add ``config.properties`` in the same folder with the following content
```
user=UserAccount
password=YourPassword
serverURL=https://s1-es.tanoth.gameforge.com/user/login/
serverNumber=1
resetTime=19:00:00
debugMode=OFF
autoSellItems=false
sellEpics=false
autoIncreaseStats=false
``` 
> Doing this way the program is going to read the file (external) that you created instead from the *JAR package* (Override). Skip this step if already was configured before compiling. 
 

## Authors

* **J. Hernán Di Bello** - *Initial work* - [GitHub:hernan32](https://github.com/hernan32/)

## Acknowledgments

* [Jewelsea](https://gist.github.com/jewelsea) - JavaFX/AWT Tray Icon / JavaFX Threading
* Emi - OOP Recommendations / Exception Management
* Ale - OOP Recommendations / Double Dispatch Implementation

## To Do List

* Auto-Start Option
* Test deployment @zero
* Bot class specification by configuration (inheritance)

## Bugs

* Tutorial/Help
