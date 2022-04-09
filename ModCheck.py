import wget
from os import listdir, mkdir, path, remove, rmdir
from os.path import isfile, join
import shutil
import json
import yaml

exists = isfile('legalMods.yaml')
if exists:
    remove('legalMods.yaml')

file = wget.download("https://github.com/pistacium/LegalMods/releases/download/mods/legalMods.yaml")
with open("legalMods.yaml") as f:
    mods = yaml.safe_load(f)
    mods = mods['1.16']
remove("LegalMods.yaml")

new = input("\nWould you like to create a new settings file ModCheckSettings.yaml? (y/n) ")

file = 'ModCheckSettings.yaml'
exists = isfile(file)

print()
if not exists or new.lower()=='y' or new.lower()=='yes':
    if not exists and (new.lower()=='n' or new.lower=='no'):
        tmp = input("Settings file not found, making new one. Press ENTER to continue.")
        print()
    with open('ModCheckSettings.yaml', 'w', encoding='utf8') as f:
        data = {
            'useMod' : {},
            'directories' : [
                'Replace with path to .minecraft//mods',
                'Add multiple directories as such'                
            ] 
        }
        for mod in mods:
            data['useMod'][mod] = True
        yaml.dump(data, f, default_flow_style=False, allow_unicode=True)

    print("Edit the new settings file by adding your directories and changing whether you are using certain mods or not.")
    print("Default is set to True, meaning it will use all legal mods.")
    done = input("Confirm that you are done configuring your settings by pressing ENTER.")
    print()
    

else:
    with open('ModCheckSettings.yaml', 'r', encoding='utf8') as f:
        datal = yaml.safe_load(f)
        #lines = f.readlines()
        #lines = lines[1:]
        mods1 = mods.keys()
        mods2 = datal['useMod'].keys()
        #print(mods2)
        b1 = list(set(mods1).difference(mods2)) #Does our list of legal mods have more entries (i.e. mod got legalized)
        b2 = list(set(mods2).difference(mods1)) #Does our settings have too many mods          (i.e. mod got banned)
    with open('ModCheckSettings.yaml', 'w', encoding='utf8') as f:
        for legalized in b1:
            print(legalized, "has gotten legalized!")
            default = input("Would you like to use this mod, or not? (y/n) ")
            print()
            if default.lower()=='y' or default.lower()=='yes':
                datal['useMod'][legalized] = True
            else:
                datal['useMod'][legalized] = False
        for banned in b2:
            print(banned, "has been banned (maybe, or one of your settings is mislabeled). Please make sure to remove this mod from your directory and your settings.")

        yaml.dump(datal, f, default_flow_style=False, allow_unicode=True)

        
dirs = []
sets = {}
with open('ModCheckSettings.yaml', encoding='utf8') as f:
    data = yaml.safe_load(f)    
dirs = data['directories']
sets = data['useMod']

print('All files that are not marked as true (besides dynamic-fps) will be REMOVED from your mods folder.')
print('Make sure that any other files in your mods folders which you would like to keep are saved elsewhere.')
tmp = input("Press ENTER to continue.")
print()
baddirs=[]
for d in dirs:
    if not (path.exists(d)):
        baddirs.append(d)

if baddirs != []:
    for d in baddirs:
        print("The directory,")
        print(d)
        print("does not exist. Remove or fix this directory from your settings.")
        print()
    tmp = input("Press ENTER to leave program.")
    exit()

if not (path.exists("tmp")):
    mkdir('tmp') 
dl = {}
for mod in mods:
    if sets[mod]:
        file = wget.download(mods[mod], out="tmp")
        dl[mod] = file
names={}
for d in dirs:
    files = [f for f in listdir(d) if (isfile(join(d, f)) and f!='dynamic-menu-fps-0.1.jar')]
    for file in files:
        remove(d+'\\'+file)

    for mod in dl:
        shutil.copy(dl[mod], d)

        names[mod] = dl[mod][4:]

    new = sorted(names.values())
    old = sorted(files)

    addeds = list(set(new).difference(old))
    removeds = list(set(old).difference(new))
    print()
    for added in addeds:
        print(added, "was added.")
    for removed in removeds:
        print(removed, "was removed.")
    
shutil.rmtree("tmp")
print()
egg = input("\nWe should be done here, press ENTER to leave. if it didnt work talk to meera#8941.")
    
            


    




