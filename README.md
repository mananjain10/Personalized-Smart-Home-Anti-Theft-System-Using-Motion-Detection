# Personalized-Smart-Home-Anti-Theft-System-Using-Motion-Detection
This Project is a Smart Home Surveillance system which will be activated by the home owner when he/she is not present in the property.Once activated by user, if there is any malicious activity/motion , home owner will be alerted.And if the intruder tries to steal any object the owner will receive image of the object missing. Means of alert will be an email which will be sent on the registered email id .The system will show us whether the person in the environment is known or unknown. This project is an initiative towards smart and easy surveillance which is cost and time efficient.

![](header.png)

## Details of Hardware/Software Requirement

#### Hardware requirements: <br />
1.System : Pentium IV 2.4 GHz.  <br />
2.Ram : 2 Gb  <br />
3.Hard Disk : 40 Gb  <br />
4.Input device : Camera.  <br />
5.Output device : VGA and High Resolution Monitor <br />


#### Software requirements:  <br />
1.Operating System : Windows Family <br />
2.Language : JAVA <br />
3.Java Version : JDK 1.6 above <br />
4.Tool : Eclipse <br />
5.Database : MYSQL/navicat<br />

## Installation

1. Download the repository on your machine.<br/>
2. Make sure you have Java Version : JDK 1.6 or above installed on your machine. For downloading [click here](https://www.oracle.com/in/java/technologies/javase-downloads.html).
3. Download and install eclipse. For downloading [click here](https://www.eclipse.org/downloads/).
4. Open a new workspace.
5. Run Main.java which is present under src\userrecognition\Main.java
6. Add a new user if not added yet and let the program train.
7. After adding a user choose your preferred algorithm and start the camera.

## Usage example

A few motivating and useful examples of how your product can be used. Spice this up with code blocks and potentially more screenshots.

_For more examples and usage, please refer to the [Wiki][wiki]._

## Development setup

#### Install OpenCV
First of all you should download the OpenCV library (version 3.x) from [here](https://opencv.org/releases/).

Then, extract the downloaded OpenCV file in a location of your choice. Once you get the folder opencv put in wherever you prefer.

Now the only two things that you will need are: the opencv-3xx.jar file located at \opencv\build\java and the opencv_java3xx.dll library located at \opencv\build\java\x64 (for 64-bit systems) or \opencv\build\java\x86 (for 32-bit systems). The 3xx suffix of each file is a shortcut for the current OpenCV version, e.g., it will be 300 for OpenCV 3.0 and 330 for OpenCV 3.3.

#### Set up OpenCV for Java in Eclipse<br/>
Open Eclipse and select a workspace of your choice. Create a User Library, ready to be used on all your next projects: go to Window > Preferences....


From the menu navigate under Java > Build Path > User Libraries and choose New.... Enter a name for the library (e.g., opencv) and select the newly created user library. Choose Add External JARs..., browse to select opencv-3xx.jar from your computer. After adding the jar, extend it, select Native library location and press Edit....


Select External Folder... and browse to select the folder containing the OpenCV libraries (e.g., C:\opencv\build\java\x64 under Windows).

In case of MacOS, if you installed OpenCV without Homebrew, you need to create a soft link with .dylib extension for the .so file. E.g., from the terminal, type: ln -s libopencv_java300.so libopencv_java300.dylib




## Meta

Your Name – [@YourTwitter](https://twitter.com/dbader_org) – YourEmail@example.com

Distributed under the XYZ license. See ``LICENSE`` for more information.

[https://github.com/yourname/github-link](https://github.com/dbader/)

## Contributing

1. Fork it (<https://github.com/yourname/yourproject/fork>)
2. Create your feature branch (`git checkout -b feature/fooBar`)
3. Commit your changes (`git commit -am 'Add some fooBar'`)
4. Push to the branch (`git push origin feature/fooBar`)
5. Create a new Pull Request

<!-- Markdown link & img dfn's -->
[npm-image]: https://img.shields.io/npm/v/datadog-metrics.svg?style=flat-square
[npm-url]: https://npmjs.org/package/datadog-metrics
[npm-downloads]: https://img.shields.io/npm/dm/datadog-metrics.svg?style=flat-square
[travis-image]: https://img.shields.io/travis/dbader/node-datadog-metrics/master.svg?style=flat-square
[travis-url]: https://travis-ci.org/dbader/node-datadog-metrics
[wiki]: https://github.com/yourname/yourproject/wiki
