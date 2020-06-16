package de.wolfig.notes;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class StartUp {

    private static File batchFile = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Roaming" + File.separator + "Microsoft" + File.separator + "Windows" + File.separator + "Start Menu" + File.separator + "Programs" + File.separator + "Startup" + File.separator + "notes.bat");
    private static File notesPath = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "Notes");

    public static void createStartUpBatchFile() {
        BufferedWriter batchWriter = null;
        String fileName = new java.io.File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
        try {
            batchWriter = new BufferedWriter(new FileWriter(batchFile));
            batchWriter.write("start javaw -Dfile.encoding=UTF8 -jar " + notesPath + File.separator + "Notes.jar\nexit 0");
            batchWriter.flush();
            batchWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteStartUpBatchFile() {
        return batchFile.delete();
    }

    public static boolean isOnStartUpEnable() {
        return batchFile.exists();
    }
}
