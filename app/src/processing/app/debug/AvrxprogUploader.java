package processing.app.debug;

import processing.app.Base;
import processing.app.Preferences;
import processing.app.Serial;
import processing.app.SerialException;
import processing.app.windows.Registry;
import processing.app.windows.Registry.REGISTRY_ROOT_KEY;
import static processing.app.I18n._;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import javax.swing.*;

import gnu.io.*;

public class AvrxprogUploader extends Uploader
{
    public AvrxprogUploader()
    {
    }
    public boolean uploadUsingPreferences(String buildPath, String className, boolean usingProgrammer) throws RunnerException, SerialException
    {
        Map<String, String> boardPreferences  = Base.getBoardPreferences();
    
        // if no protocol is specified for this board, assume it lacks a 
        // bootloader and upload using the selected programmer.
        if(usingProgrammer || boardPreferences.get("upload.protocol") == null)
        {
            return false;
        }
            
        return uploadViaBootloader(buildPath, className);  
    }
    private boolean uploadViaBootloader(String buildPath, String className)
        throws RunnerException, SerialException
    {
        Map<String, String> boardPreferences  = Base.getBoardPreferences();
        List commandDownloader                = new ArrayList();   

        commandDownloader.add("-d" + boardPreferences.get("build.mcu"));
        commandDownloader.add("-c" + Preferences.get("serial.port"));
        commandDownloader.add("-if" + buildPath + File.separator + className + ".hex");
        commandDownloader.add("-R");
        commandDownloader.add("-e");
        commandDownloader.add("-pf");
        commandDownloader.add("-vf");
        
        return avrxprog(commandDownloader);
    }
    public boolean burnBootloader() throws RunnerException
    {
        String programmer               = Preferences.get("programmer");
        Target target                   = Base.getTarget();
    
        if (programmer.indexOf(":") != -1)
        {
            target                      = Base.targetsTable.get(programmer.substring(0, programmer.indexOf(":")));
            programmer                  = programmer.substring(programmer.indexOf(":") + 1);
        }
        
        return burnBootloader(getProgrammerCommands(target, programmer));
    } 
    private Collection getProgrammerCommands(Target target, String programmer)
    {
        Map<String, String> programmerPreferences = target.getProgrammers().get(programmer);
        List params                     = new ArrayList();
//        params.add("-c" + programmerPreferences.get("protocol"));
//    
//        if ("usb".equals(programmerPreferences.get("communication")))
//        {
//            params.add("-Pusb");
//        }
//        else if ("serial".equals(programmerPreferences.get("communication")))
//        {
//            params.add("-P" + (Base.isWindows() ? "\\\\.\\" : "") + Preferences.get("serial.port"));
//            
//            if (programmerPreferences.get("speed") != null)
//            {
//                params.add("-b" + Integer.parseInt(programmerPreferences.get("speed")));
//            }
//        }
//        
//        // XXX: add support for specifying the port address for parallel
//        // programmers, although avrdude has a default that works in most cases.
//        if (programmerPreferences.get("force") != null && programmerPreferences.get("force").toLowerCase().equals("true"))
//            params.add("-F");
//        
//        if (programmerPreferences.get("delay") != null)
//            params.add("-i" + programmerPreferences.get("delay"));
        return params;
    }
    protected boolean burnBootloader(Collection params) throws RunnerException
    {
//        Map<String, String> boardPreferences    = Base.getBoardPreferences();
//        List fuses                              = new ArrayList();
//      
//        fuses.add("-e"); // erase the chip
//        if (boardPreferences.get("bootloader.unlock_bits") != null)
//            fuses.add("-Ulock:w:" + boardPreferences.get("bootloader.unlock_bits") + ":m");
//    
//        if (boardPreferences.get("bootloader.extended_fuses") != null)
//            fuses.add("-Uefuse:w:" + boardPreferences.get("bootloader.extended_fuses") + ":m");
//    
//        fuses.add("-Uhfuse:w:" + boardPreferences.get("bootloader.high_fuses") + ":m");
//        fuses.add("-Ulfuse:w:" + boardPreferences.get("bootloader.low_fuses") + ":m");
//    
//        if (!avrdude(params, fuses))
//            return false;
//    
//        try
//        {
//            Thread.sleep(1000);
//        }
//        catch (InterruptedException e) {}
//    
//        Target t;
//        List bootloader                                     = new ArrayList();
//        String bootloaderPath                               = boardPreferences.get("bootloader.path");
//    
//        if (bootloaderPath != null)
//        {
//            if (bootloaderPath.indexOf(':') == -1)
//            {
//                t                                           = Base.getTarget(); // the current target (associated with the board)
//            }
//            else
//            {
//                String targetName                           = bootloaderPath.substring(0, bootloaderPath.indexOf(':'));
//                t                                           = Base.targetsTable.get(targetName);
//                bootloaderPath                              = bootloaderPath.substring(bootloaderPath.indexOf(':') + 1);
//            }
//    
//            File bootloadersFile                            = new File(t.getFolder(), "bootloaders");
//            File bootloaderFile                             = new File(bootloadersFile, bootloaderPath);
//            bootloaderPath                                  = bootloaderFile.getAbsolutePath();
//    
//            bootloader.add("-Uflash:w:" + bootloaderPath + File.separator + boardPreferences.get("bootloader.file") + ":i");
//        }
//        
//        if (boardPreferences.get("bootloader.lock_bits") != null)
//            bootloader.add("-Ulock:w:" + boardPreferences.get("bootloader.lock_bits") + ":m");
//    
//        if (bootloader.size() > 0)
//            return avrxprog(params, bootloader);
      
        return false;
    }
    public boolean avrxprog(Collection p1, Collection p2) throws RunnerException
    {
        ArrayList p                                         = new ArrayList(p1);
        p.addAll(p2);
        
        return avrxprog(p);
    }
    public boolean avrxprog(Collection params) throws RunnerException
    {
        List commandDownloader                              = new ArrayList();
        String avrxprog_loc                                 = null;
        
        try
        {
            // Try access 64 bit register region
            avrxprog_loc                                    = Registry.getStringValueEx(Registry.REGISTRY_ROOT_KEY.LOCAL_MACHINE, "SOFTWARE\\AVRxPROG", "InstallDir", true);
            
            // Try access 32 bit register region
            if(avrxprog_loc == null)
                avrxprog_loc                                = Registry.getStringValueEx(Registry.REGISTRY_ROOT_KEY.LOCAL_MACHINE, "SOFTWARE\\AVRxPROG", "InstallDir", false);
            
            if(avrxprog_loc == null)
              throw new RunnerException("AVRxPROG was not detected! Please, re-install the application and try again.");
            
            commandDownloader.add(avrxprog_loc + "avrxprog");
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RunnerException(e.getMessage());
        }
        
        commandDownloader.addAll(params);
    
        return executeUploadCommand(commandDownloader);
    }
}
