package de.wolfig.notes.fx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import de.wolfig.notes.DataManager;
import de.wolfig.notes.Main;
import de.wolfig.notes.Note;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotePane extends MainPane {

    private static JFXTextField titleTextField = new JFXTextField();
    private static JFXChipView<String> typeView = new JFXChipView<>();
    private static JFXTextArea textArea = new JFXTextArea();
    private static JFXButton deleteButton = new JFXButton("Delete Note");
    private static JFXButton saveButton = new JFXButton("Save Note");
    private static JFXButton previousButton = new JFXButton("Previous");
    private static JFXButton nextButton = new JFXButton("Next");

    private static FlowPane controlPane = new FlowPane();

    private static Note currentNote = null;
    private static boolean _editable = false;

    public NotePane() {
        super();

        String pattern = "dd.MM.yy - HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        //layout
        StackPane titlePane = new StackPane();
        titlePane.setPrefSize(350, 50);
        //titlePane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        StackPane typesPane = new StackPane();
        typesPane.setPrefSize(350, 150);
        //typesPane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        StackPane textPane = new StackPane();
        textPane.setPrefSize(350, 250);
        //textPane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 255), CornerRadii.EMPTY, Insets.EMPTY)));
        controlPane.setHgap(25);
        controlPane.setVgap(5);
        controlPane.setPadding(new Insets(5));
        controlPane.setPrefSize(350, 150);
        controlPane.setAlignment(Pos.BOTTOM_CENTER);
        //controlPane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));

        //title
        titleTextField.setDisable(true);

        //types
        typeView.setDisable(true);

        //text
        textArea.setDisable(true);

        //controls
        previousButton.getStyleClass().add("button-raised");
        previousButton.setMaxWidth(100);
        saveButton.getStyleClass().add("button-raised");
        saveButton.setMaxWidth(100);
        deleteButton.getStyleClass().add("button-raised");
        deleteButton.setMaxWidth(100);
        nextButton.getStyleClass().add("button-raised");
        nextButton.setMaxWidth(100);


        titlePane.getChildren().addAll(titleTextField);
        typesPane.getChildren().addAll(typeView);
        textPane.getChildren().addAll(textArea);
        controlPane.getChildren().addAll(previousButton, deleteButton, nextButton);
        this.getChildren().addAll(titlePane, typesPane, textPane, controlPane);
        this.setAlignment(Pos.TOP_CENTER);


        //events
        previousButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Main.setNotePane(DataManager.getPreviousNote(currentNote), _editable));
        saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            DataManager.updateNote(currentNote, new Note(currentNote.getId(), titleTextField.getText(), simpleDateFormat.format(new Date()), new ArrayList<>(typeView.getChips()), textArea.getText()));
            currentNote.setTitle(titleTextField.getText());
            ObservableList<String> oLTypes = typeView.getChips();
            currentNote.setTypes(new ArrayList<>(oLTypes));
            currentNote.setText(textArea.getText());
            currentNote.setDate(simpleDateFormat.format(new Date()));
            Main.openListPane();
        });
        deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            Note note = currentNote;
            Main.setNotePane(DataManager.getPreviousNote(currentNote), _editable);
            DataManager.removeNote(note);
            if(DataManager.getNotes().size() == 0) Main.openListPane();
        });
        nextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> Main.setNotePane(DataManager.getNextNote(currentNote), _editable));
    }

    public static void setNote(Note note, boolean editable) {
        currentNote = note;
        titleTextField.setText(note.getTitle());
        typeView.getChips().setAll(note.getTypes());
        textArea.setText(note.getText());

        _editable = editable;
        if(editable) {
            controlPane.getChildren().remove(1);
            controlPane.getChildren().add(1, saveButton);
            titleTextField.setDisable(false);
            typeView.setDisable(false);
            textArea.setDisable(false);
        } else {
            controlPane.getChildren().remove(1);
            controlPane.getChildren().add(1, deleteButton);
            titleTextField.setDisable(true);
            typeView.setDisable(true);
            textArea.setDisable(true);
        }
    }
}
