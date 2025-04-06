
# Bitwig Performance Twister (Bitwig Extension) 
 Bitwig Extension to use the midi controller "Midi Fighter Twister" with the DAW Bitwig. 

Directly download the latest release: [download zip file](https://github.com/markram71/Bitwig-Performance-Twister/releases/latest)

Please read the [user documentation ](docs/README.md) with the installation instructions.


## Compiling
Source code for Bitwig Performance Twister is available here on github. Feel free to use it for your own projects (according to the license agreement). 

### Requirements

* JDK 17
* Bitwig Studio Version 5.1
* Bitwig Controller API 18
* Maven 3.9.5 


### Build and install

1. Follow the installation instructions for each of the above requirements.
2. You might want to install an IDE like Eclipse or Visual Studio Code
3. Run `mvn install`.

### Instruction on MacOs
In order to get Bitwig Performance Twister to run on MacOs, starting from zero, you should follow these steps. I am writing this to remind my self to get started on a new machine. 

1. install MS Visual Code
2. install Homebrew. Don't forget to include brew in the path. Description for that is on the homepage of homebrew
3. use homebrew to install maven
4. install git, install gitHub Desktop
5. use gitHub desktop to clone Bitwig Performance Twister, when cloning it into the document folder, make sure that the documents folder is not synced with icloud. Check this in Apple system preferences under icloud
6. open the cloned folder in MS-Visual Code.
7. open a terminal in code and check the java version (java -version) and the Maven version (mvn -version)
8. in MS-Code, open the explorer, go down to MAVEN, open Bitwig Performance Twister and Lifecycle
9. Check the POM file, maybe the location where I copy the jar file to Bitwig might need to be adjusted
10. In the MAVEN area, see under 8. run the "install" lifecycle option 
11. Add a shortcut in Bitwig to open the Controller Console. Use will use Command + K
12. Add a task to MS Visual code to run the maven install command. Then add a keyboard shortcut to run this task


