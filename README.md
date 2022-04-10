# ModCheck
### Keep up to date with your legal speedrunning mods!  
If you are just starting out, you can use this to download all legal mods! Or, you can use this to keep up to date with all your mods by running this script periodically! I will be remotely updating the list of legal mods, so no need to download a new version of this program everytime.

## Support
Supports minecraft version 1.16.1, though other support may come if people bug me for it.  
Should support windows and anything that has a bash shell (so pretty much anything unix including mac)  

## Usage
For windows, download the windows release and unzip. Inside the folder, double click on "runModCheck.bat".
For unix, download the unix release and unzip. Inside the folder, double click on "runModCheck.sh".  

If for some reason the .bat or .sh script are not working, you may run from the command line with the command  
```"java -jar ModCheck-v2.0.jar"```  

If your verbosity is set to false, you may double click on the .jar file itself, but any printouts will not show up. If something goes wrong, you won't be able to tell.  
If you do try to run it like this, and nothing happens, set verbose to true and try running the .bat or .sh file, or from the command line. This may tell you what went wrong.

## Settings
Settings file is of json format.  
Under "directories", put all your .minecraft/mods folder paths. All paths should be in double quotes (") and separated by commas.
Under "useMod" each legal mod will be listed and either "true" or "false". If you want to use/update a certain mod, flag it to true. Otherwise, flag it to false.  

If the "verbose" setting is set to true, the program will run as normal and will require prompts.  
If it is set to false, the program will run without any prompts or printouts (except if a directory doesn't exist).  
Also, if a new mod is legalized, it will update your useMod and set it to true for the new mod (instead of asking you whether you want to use it).  
**ONLY SET VERBOSE TO FALSE IF YOU ARE SURE EVERYTHING IS WORKING.**

## Warning
**MAKE SURE** to not leave any files you want to keep in your mods folder.  
**ALL OTHER FILES IN MODS FOLDER WILL BE DELETED.**  
The exception to this is the dynamic-fps mod, since there is no Github page for it and also no updates for it, so it will not be tracked.

## Contact
For any issues, message me at meera#8941.
