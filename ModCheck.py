import wget
from os import listdir, mkdir, path, remove, rmdir
from os.path import isfile, join
import shutil
import json


file = wget.download("https://github.com/pistacium/LegalMods/releases/download/mods/LegalMods.json")
with open("LegalMods.json") as f:
    mods = json.load(f)
remove("LegalMods.json")


new = input("\nWould you like to create a new settings file ModCheckSettings.txt? (y/n) ")
print()
if new.lower()=="y" or new.lower=="yes":
    with open('ModCheckSettings.txt', mode='w') as f:
        lines = ['pathToMods : Replace everything after the colon with the path to .minecraft\\mods. You may add multiple paths separated by spaces']
        for mod in mods:
            line = mod+" : True"
            lines.append(line)
        f.writelines('\n'.join(lines))

    print("Edit the settings file by adding your directories and changing whether you are using certain mods or not. Default is set to True, meaning it will use all legal mods.")
    done = input("Confirm that you are done configuring your settings by pressing ENTER.")
    print()
    

else:
    with open('ModCheckSettings.txt') as f:
        lines = f.readlines()
        lines = lines[1:]
        mods1 = mods.keys()
        mods2 = []
        for line in lines:
            line = line.split()[0]
            mods2.append(line)
        b1 = list(set(mods1).difference(mods2)) #Does our list of legal mods have more entries (i.e. mod got legalized)
        b2 = list(set(mods2).difference(mods1)) #Does our settings have too many mods          (i.e. mod got banned)
    with open('ModCheckSettings.txt', 'a') as f:
        for legalized in b1:
            print(legalized, "has gotten legalized!")
            default = input("Would you like to use this mod, or not? (y/n) ")
            print()
            if default.lower()=='y' or default.lower()=='yes':
                line = "\n"+legalized+" : True\n"
                f.write(line)
            else:
                line = "\n"+legalized+" : False\n"
                f.write(line)
        for banned in b2:
            print(banned, "has been banned (maybe, or one of your settings is mislabeled). Please make sure to remove this mod from your directory and your settings.")

dirs = []
sets = {}
with open('ModCheckSettings.txt') as f:
    lines = f.readlines()
    dirs = lines[0][13:]
    dirs = dirs.split()
    for line in lines[1:]:
        i = line.find(':')
        name = line[:i-1]
        val = line[i+2:-1]
        sets[name] = val
        
dl = {}
for mod in mods:
    #direc = path.abspath(__file__)+'\\tmp'
    if not (path.exists("tmp")):
        mkdir('tmp')
    #print(mod, sets[mod])
    if sets[mod]=="True" or sets[mod]=="Tru":
        file = wget.download(mods[mod], out="tmp")
        dl[mod] = file
    
for d in dirs:
    files = [f for f in listdir(d) if isfile(join(d, f))]
    for file in files:
        remove(d+'\\'+file)

    for mod in dl:
        shutil.copy(dl[mod], d)

        dl[mod] = dl[mod][4:]

    new = sorted(dl.values())
    old = sorted(files)

    addeds = list(set(new).difference(old))
    removeds = list(set(old).difference(new)))
    for added in addeds:
        print(added, "was added.")
    for removed in removeds:
        print(removed, "was removed.")
    
    shutil.rmtree("tmp")
print()
egg = input("\nWe should be done here, press ENTER to leave. if it didnt work talk to meera#8941.")
    
            


    




