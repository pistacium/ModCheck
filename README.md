# ModCheck
Keep up to date with your legal speedrunning mods!


Use by running executable or python file directly. Windows only support.
Currently only has 1.16 support, but more may come depending on how much people bug me for it.

Settings file is of yaml format. 
Under "directories", put all your .minecraft/mods folder paths each on new lines. Each line should look like "- C:\path\to\.minecraft\mods"
Under "useMod" each legal mod will be listed and either "true" or "false". If you want to use/update a certain mod, flag it to true. Otherwise, flag it to false.

MAKE SURE to not leave any files you want to keep in your mods folder. 
ALL OTHER FILES IN MODS FOLDER WILL BE DELETED. 
The exception to this is the dynamic-fps mod, since there is no Github page for it and also no updates for it, so it will not be tracked.
