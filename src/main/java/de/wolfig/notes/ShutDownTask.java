package de.wolfig.notes;

public class ShutDownTask extends Thread {

    @Override
    public void run() {
        DataManager.saveNotes();
    }
}
