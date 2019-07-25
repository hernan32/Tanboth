# Tanboth 1.0

Bot for the game called "Tanoth" (by GameForge) made in Java. It has the functionality to make quest automatically and shows the general state of the character.

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

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [JavaFX](https://openjfx.io/) - Used to generate GUI
* [Jsoup](https://jsoup.org/) - Game parsing
* [Apache Commons Lang](https://jsoup.org/) - Game parsing supplement/helper
* [MinLog](https://github.com/EsotericSoftware/minlog) - Java logging
* [AWT/SystemTray](https://gist.github.com/jewelsea/e231e89e8d36ef4e5d8a) - System Tray (AWT) to control a JavaFX application
* [Java HTTP Client](https://openjdk.java.net/groups/net/httpclient/intro.html) - Tanoth HTTP Protocol
* [GitHub](https://github.com/) - Hosting/Versioning

## Versioning

We use [GitHub](https://github.com/) for versioning. For the versions available, see the [tags on this repository](https://github.com/hernan32/Tanboth). 

## Authors

* **J. Hernán Di Bello** - *Initial work* - [GitHub:hernan32](https://github.com/hernan32/)

## Acknowledgments

* [Jewelsea](https://gist.github.com/jewelsea) - JavaFX/AWT Tray Icon / JavaFX Threading
* Emi - OOP Recommendations / Exception Management
* Ale - OOP Recommendations / Double Dispatch Implementation