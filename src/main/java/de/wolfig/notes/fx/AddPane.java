package de.wolfig.notes.fx;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RequiredFieldValidator;
import de.wolfig.notes.DataManager;
import de.wolfig.notes.Main;
import de.wolfig.notes.Note;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddPane extends MainPane {

    private JFXTextField titleTextField = new JFXTextField();
    private JFXChipView<String> typeView = new JFXChipView<>();
    private JFXTextArea textArea = new JFXTextArea();
    private JFXButton saveButton = new JFXButton("Save Note");

    public AddPane() {
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
        StackPane controlPane = new StackPane();
        controlPane.setPrefSize(350, 150);
        //controlPane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 0, 0), CornerRadii.EMPTY, Insets.EMPTY)));

        //title
        titleTextField.setPromptText("Title");
        titleTextField.getStyleClass().add("jfx-title-text-field");

        //types
        typeView.setPromptText("Types");
        typeView.getSuggestions().addAll("URL", "YouTube", "GitHub", "StackOverflow", "Reddit", "Google", "Instagram", "Twitter", "ToDo");

        //text
        textArea.setPromptText("Note");
        textArea.setLabelFloat(false);
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("Please add some notes!");
//        FontIcon warnIcon = new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
//        warnIcon.getStyleClass().add("error");
//        validator.setIcon(warnIcon);
        textArea.getValidators().add(validator);
        textArea.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!newVal) {
                textArea.validate();
            }
        });

        //controls
        saveButton.getStyleClass().add("button-raised");
        saveButton.setLayoutX(100);


        titlePane.getChildren().addAll(titleTextField);
        typesPane.getChildren().addAll(typeView);
        textPane.getChildren().addAll(textArea);
        controlPane.getChildren().addAll(saveButton);
        this.getChildren().addAll(titlePane, typesPane, textPane, controlPane);
        this.setAlignment(Pos.TOP_CENTER);


        //events
        saveButton.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            String text = textArea.getText();
            if(!text.equals("")) {
                String date = simpleDateFormat.format(new Date());
                String title = !titleTextField.getText().equals("") ? titleTextField.getText() : text;//.substring(0, 40) + "...";
                ObservableList<String> oLTypes = typeView.getChips();
                ArrayList<String> types = new ArrayList<>(oLTypes);
                DataManager.addNote(new Note(DataManager.getLastId(), title, date, types, text));

                clearInputs();
                Main.openListPane();
                Main.updateTrayIcon();
            }
        });
    }

    public void clearInputs() {
        titleTextField.setText("");
        typeView.getChips().setAll();
        textArea.setText("");
    }
}
