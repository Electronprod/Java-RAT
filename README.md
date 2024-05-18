# DISCLAIMER
>Java-RAT is for education/research purposes only. The author takes NO responsibility and/or liability for how you choose to use any of the tools/source code/any files provided. The author and anyone affiliated with will not be liable for any losses and/or damages in connection with use of ANY files provided with Java-RAT. By using Java-RAT or any files included, you understand that you are AGREEING TO USE AT YOUR OWN RISK. Once again Java-RAT and ALL files included are for EDUCATION and/or RESEARCH purposes ONLY. Java-RAT is ONLY intended to be used on your own pentesting labs, or with explicit consent from the owner of the property being tested.

# About Java RAT Project
A Remote Administration tool client.
To control it use [server application](https://github.com/Electronprod/Java-RAT-Server)

## Supported OS
Java version must be higher than 8.
- Windows (full functionality, Tested on Windows 7,10,11)
- Linux (limited functionality, Tested on Ubuntu WSL)
- MacOS (limited functionality, Not tested)

## Use this without Java installed
This app supports GraalVM native-image compilation. You can do it yourself.\
**NOTE:** This reduces functionality a little. (.wav player for example)\
**GUIDE:**
1. Download and install native-image from [this site](https://bell-sw.com/pages/downloads/native-image-kit/). (Only this native-image fork supports AWT)
2. Install and configure dependencies using [this guide](https://www.graalvm.org/latest/docs/getting-started/windows/).
3. Open a terminal in the folder where you downloaded Client.jar file and execute there this command:
   ```
   native-image -Djava.awt.headless=false --enable-http --enable-https -jar Client.jar
   ```
4. Wait for your file to be generated.

## Features
### General
>- Get main info about computer
>- Watch screen
>- Emulate keyboard
>- Taking over mouse control
>- Freeze mouse in X:0 Y:0 coordinates
### File Manager
>- Download file
>- Upload file
>- Edit file (any files with plain text and small long)
>- Run file
>- Run file and listen output
>- Create file
>- Delete File
>- Play .wav file with internal music player (native-image unsupports this function)
### TaskManager
Works only on Windows.
>- Kill process by PID
>- Kill process by Name
>- Get full process info (takes a lot of time, by default disabled. Enable with "fastmode" ChoiceBox)
### Scripting
Works only on Windows. You can execute your script.\
**Supported languages:**
>- Windows CMD (Creates .cmd file and runs it)
>- Windows BAT (Creates .bat file and runs it)
>- Powershell (Creates .ps1 file and runs it)
>- Powershell console (Executes command without creating file to bypass the restriction. Runs with `powershell.exe /c <script>`)
>- Visual Basic Script (Creates .vbs file and runs it using cscript.exe)
>- JavaScript (Creates .js file and runs it using cscript.exe)

### Console
You can run any system command in the console. You can execute built-in application commands. To view them, use the "/help" command.
