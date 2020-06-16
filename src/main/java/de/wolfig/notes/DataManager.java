package de.wolfig.notes;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Scanner;

public class DataManager {

    private static File notesPath = new File(System.getProperty("user.home") + File.separator + "AppData" + File.separator + "Local" + File.separator + "Notes");
    private static File notesFile = new File(notesPath.getPath() + File.separator + "Notes.json");

    private static Gson gson = new Gson();
    private static FileWriter fileWriter;

    private static ObservableList<Note> notes = FXCollections.observableArrayList();
    private static TreeItem<Note> notesItem = new TreeItem<>();

    public static void loadNotes() {
        ArrayList<Note> tempNotes;
        initialize();
        Type listOfNoteObject = new TypeToken<ArrayList<Note>>() {}.getType();
        if(notesFile.exists()) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(notesFile));
                if (br.readLine() != null) {
                    try {
                        tempNotes = gson.fromJson(new Scanner(notesFile).useDelimiter("\\Z").next(), listOfNoteObject);
                        notes.setAll(tempNotes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        notesItem.setExpanded(true);
        notes.forEach(note -> notesItem.getChildren().add(new TreeItem<>(note)));
    }

    public static void saveNotes() {
        try {
            fileWriter = new FileWriter(notesFile);
            fileWriter.write(gson.toJson(notes).replaceAll(",\"children\":\\[],\"groupedColumn\":\\{},\"groupedValue\":\\{}", ""));
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void initialize() {
        if(!notesPath.exists()) notesPath.mkdir();
        String path = new File(".").getAbsolutePath();
        path = new File(".").getAbsolutePath().substring(0, path.length()-1);
        String fileName = new java.io.File(Main.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
        if(!path.contains(notesPath.getAbsolutePath())) {
            try {
                Files.copy(Paths.get(path + File.separator + fileName), Paths.get(notesPath + File.separator + "Notes.jar"), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ObservableList<Note> getNotes() {
        return notes;
    }

    public static void setNotes(ObservableList<Note> notes) {
        DataManager.notes = notes;
    }

    public static void addNote(Note note) {
        notes.add(note);
    }

    public static void removeNote(Note note) {
        notes.remove(note);
    }

    public static Note getPreviousNote(Note note) {
        return notes.get(notes.indexOf(note)-1 >= 0 ? notes.indexOf(note)-1 : notes.size()-1);
    }

    public static Note getNextNote(Note note) {
        return notes.get(notes.indexOf(note)+1 < notes.size() ? notes.indexOf(note)+1 : 0);
    }

    public static TreeItem<Note> getNoteItems() {
        return notesItem;
    }

    public static void updateNote(Note currentNote, Note note) {
        notes.set(notes.indexOf(currentNote), note);
    }

    public static String getLastId() {
        return notes.size() != 0 ? String.valueOf(Integer.parseInt(notes.get(notes.size()-1).getId())+1) : String.valueOf(0);
    }
}
