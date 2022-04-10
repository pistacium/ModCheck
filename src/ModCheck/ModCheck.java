package ModCheck;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;

import org.json.JSONException;
import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import org.json.JSONObject.*;

import javax.swing.*;

public class ModCheck {
    public static String getName(String url){
        return url.split("/")[8];
    } //converts github url to file name
    public static void downloadFile(URL url, String fileName) throws IOException {
        //downloads file
        File directory = new File("tmp");
        //FileUtils.copyURLToFile(url, new File(directory, fileName));

        InputStream stream = url.openStream();
        ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(null, "downloading...", stream);

        FileUtils.copyInputStreamToFile(pmis, new File(directory, fileName));

    }
    public static void createSettingsFile(Map<String,String> urls) throws JSONException {
        //Create scratch settings file
        JSONObject settings = new JSONObject();
        List<String> direcex = new ArrayList<String>();
        direcex.add("C:\\Add\\Path\\To\\.minecraft\\mods");
        direcex.add("D:\\Add\\Multiple\\directories");
        settings.put("directories", direcex);
        JSONObject useMod = new JSONObject();
        for(String name : urls.keySet()){
            useMod.put(name, true);
        }
        settings.put("useMod", useMod);
        settings.put("verbose", true);


        updateSettingsFile(settings);

    }
    public static void updateSettingsFile(JSONObject setsobj){
        //remake settings file given the proper JSONObject
        try (FileWriter setfile = new FileWriter("ModCheckSettings.json")){
            org.json.JSONObject json = new org.json.JSONObject(setsobj);
            setfile.write(json.toString(4));
            setfile.flush();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Map<String,String> urls = new LinkedHashMap<>();
        Map<String, Boolean> useMod = new LinkedHashMap<>();
        //List<String> names = new ArrayList<>();

        //----------------------------Download list of legal mods-------------------------------------------------------
        String seturl = "https://github.com/pistacium/LegalMods/releases/download/mods/legalMods.json";
        File modslist = new File("legalMods.json");

        //If an old copy of legalmods.json still exists, delete it
        if(modslist.exists()){
            modslist.delete();
        }
        FileUtils.copyURLToFile(new URL(seturl), modslist); //actually download it


        //Read the legalmods file as a JSON
        JSONObject legalMods =  (JSONObject) new JSONParser().parse(new FileReader("legalMods.json"));
        JSONObject mods = (JSONObject) legalMods.get("1.16");


        for(Object entry : mods.entrySet()){
            //convert the legalMods JSON to a java Map
            urls.put((String) ((Map.Entry) entry).getKey(), (String) ((Map.Entry) entry).getValue());
        }

        //-----------------------------------Settings file--------------------------------------------------------------
        File sets = new File("ModCheckSettings.json");
        Scanner reader = new Scanner(System.in);
        String yn = null;
        JSONObject setsobj;
        boolean verbose;
        if(sets.exists()) {
            setsobj = (JSONObject) new JSONParser().parse(new FileReader("ModCheckSettings.json"));
            if(setsobj.containsKey("verbose")){
                verbose = (boolean) setsobj.get("verbose");
            }
            else{
                setsobj.put("verbose", true);
                verbose = true;
            }

            if (verbose) {
                //If old version of settings exist, ask if user would like to keep using it or make a new one
                System.out.println("Would you like to use your currently existing ModCheckSettings.json? (y/n) ");
                yn = reader.nextLine();
                if (Objects.equals(yn, "y") || Objects.equals(yn, "yes")) {
                    //If they want to use existing settings file
                    System.out.println("Using existing file.");
                } else {
                    //If they wanna start from scratch, do that
                    createSettingsFile(urls);
                    System.out.println("Edit the new settings file by adding your directories and changing whether you are using certain mods or not.");
                    System.out.println("Default is set to True, meaning it will use all legal mods.");
                    System.out.println("Confirm that you are done configuring your settings by pressing ENTER.");
                    reader.nextLine();
                }
            }
        }
        else{
            //If they dont have a settings file already, then make one
            createSettingsFile(urls);
            System.out.println("A new ModCheckSettings.json has been created.");
            System.out.println("Edit the file by adding relevant directories and changing whether you are using certain mods or not.");
            System.out.println("Default is set to True, meaning it will use all legal mods.");
            System.out.println("Confirm that you are done configuring your settings by pressing ENTER.");
            reader.nextLine();
            setsobj =  (JSONObject) new JSONParser().parse(new FileReader("ModCheckSettings.json"));
            verbose = false;
        }
        //-----------------------------------Converting JSON to java stuff----------------------------------------------
        JSONArray direcobj = (JSONArray) setsobj.get("directories");
        List<String> direcs = new ArrayList<>();
        boolean baddirec = false;

        //Converting JSON directories to a Java list
        for(Object entry : direcobj){
            direcs.add(entry.toString());
            File f = new File(entry.toString());
            if(!f.exists()){
                //If a directory doesnt exist yell at user to stop being stupid
                //yell at them even if they have verbosity off
                System.out.println("The directory "+entry+" does not exist.");
                baddirec = true;
            }
        }

        if(baddirec){
            //more yelling at user
            //yell at them even if they have verbosity off
            System.out.println("Please fix these directory issues and then relaunch. Press ENTER to end the program.");
            reader.nextLine();
            System.exit(69);
        }
        JSONObject useModObj = (JSONObject) setsobj.get("useMod");

        //Convert useMod JSON to Java Map
        for(Object entry : useModObj.entrySet()){
            useMod.put((String) ((Map.Entry) entry).getKey(), (Boolean) ((Map.Entry) entry).getValue());
        }

        //---------------------updating settings if legal mods have changed---------------------------------------------
        for(Object mod : urls.keySet()){
            //looping through list of legal mods
            if((!useMod.containsKey(mod))){
                //if list of legal mods has more mods than user's settings, update their settings
                String use;
                if(verbose) {
                    System.out.println(mod + " has been legalized. Would you like to use it? (y/n)");
                    use = reader.nextLine();
                }
                else{
                    use = "y";
                }
                if(Objects.equals(use, "y") || Objects.equals(use, "yes")){
                    //if user wants to use this new mod, they will say y and it will be set to true
                    useMod.put(mod.toString(), true);
                    ((JSONObject)setsobj.get("useMod")).put(mod.toString(), true);
                }
                else{
                    //if user doesnt want to use this new mod, they will say n and it will be set to false
                    useMod.put(mod.toString(), false);
                    ((JSONObject)setsobj.get("useMod")).put(mod.toString(), false);
                }
            }
        }
        for(Object mod : useMod.keySet()){
            //looping through settings
            if((!urls.containsKey(mod)) && (Objects.equals(yn, "y") || Objects.equals(yn, "yes"))){
                //if user has a setting for a mod which isnt legal, remove said setting
                if(verbose) {System.out.println(mod + " has been banned. It will be removed from the settings file.");}
                useMod.remove(mod);
                ((JSONObject) setsobj.get("useMod")).remove(mod);

            }
        }

        updateSettingsFile(setsobj); //update settings file after these changes
        //--------------------------------------Messing with files------------------------------------------------------
        if(verbose) {
            System.out.println("\nAll files which aren't set to true will be REMOVED from your mods folder.");
            System.out.println("Make sure any other files in your mods folders which you would like to keep are saved elsewhere.");
            System.out.println("Press ENTER to continue.");
            reader.nextLine();
        }

        File tmpdir = new File("tmp");
        if(tmpdir.exists()){
            //if a tmp directory exists for some reason, ask to delete it
            //hopefully no ones leaving any directories named tmp that arent actually only there temporarily
            //but people are stupid so idfk
            if(verbose) {
                System.out.println("Directory ./tmp already exists, press ENTER to delete ./tmp and continue program.");
                reader.nextLine();
            }
            FileUtils.deleteDirectory(tmpdir);
        }
        if(verbose) {
            System.out.println("Downloading files...");
            System.out.println("Removed/Added file names will be shown\n");
        }

        boolean edited = false;

        for(String mod : urls.keySet()){
            //looping through list of legal mods
            if(useMod.get(mod)){
                //if user has set this mod to true, download it to ./tmp
                downloadFile(new URL(urls.get(mod)), getName(urls.get(mod)));
            }
            else if(!useMod.get(mod)){
                //if user has set this mod to false, delete it from all instance directories if it exists
                for(String dir : direcs){
                    String name = getName(urls.get(mod));
                    File f = new File(dir, name);
                    if(f.exists()){
                        f.delete();
                        if(verbose){System.out.println("Removed file: "+name);}
                        edited=true;
                    }

                }
            }
        }


        for(String dir : direcs){
            //looping through all instance directories / mods folders
            //reminder, barring folders the ./tmp file should have the same files as the mods folders
            //this block does that

            File dir1 = new File(dir);

            if(dir1.getName().equals(".minecraft")){
                dir1 = new File(dir1, "mods");
            }
            File[] filelist = tmpdir.listFiles();             //list of files we just downloaded into ./tmp
            FileFilter filefilter = new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    if(pathname.isFile()){
                        return true;
                    }
                    else{
                        return false;
                    }
                }
            };
            File[] dllist = dir1.listFiles(filefilter);      //list of files in instance directory / mods folder
            for(int i = 0 ; i < dllist.length; i++){
                //looping through files (and just files) in mods folder
                File f = new File(tmpdir, dllist[i].getName());
                if (!f.exists() && !(dllist[i].getName().equals("dynamic-menu-fps-0.1.jar"))){
                    //if the file in the mods folder is NOT in the ./tmp folder, delete it
                    dllist[i].delete();
                    if(verbose){System.out.println("Removed file: "+dllist[i].getName());}
                    edited=true;
                }
            }
            for(int i = 0 ; i< filelist.length; i++) {
                //looping through files in ./tmp
                File f = new File(dir1, filelist[i].getName());
                if (!f.exists()) {
                    //if this file does not already exist in the mods folder, copy it to there from ./tmp
                    f.createNewFile();
                    Files.copy(filelist[i].toPath(), f.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    if(verbose){System.out.println("Added file: "+filelist[i].getName());}
                    edited=true;
                }
            }
        }
        if(!edited){
            System.out.println("\nNothing needed to be changed, you are up to date with your mods :)\nI'm proud of you.");
        }

        FileUtils.deleteDirectory(tmpdir);   //delete ./tmp we are done with her
        modslist.delete();                   //delete the list of legal mods .json file

        if(verbose) {
            System.out.println("\n\nWe should be done here, press ENTER to leave the program.");
            System.out.println("Talk to meera#8941 if somethin funky happens.");
            reader.nextLine();
        }
    }

}
