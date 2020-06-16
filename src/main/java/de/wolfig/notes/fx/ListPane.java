package de.wolfig.notes.fx;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.wolfig.notes.DataManager;
import de.wolfig.notes.Main;
import de.wolfig.notes.Note;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static de.wolfig.notes.Main.updateTrayIcon;

public class ListPane extends MainPane {

    public static JFXTreeTableView<Note> notesTable = addNotesTable();
    JFXTextField filterField = new JFXTextField();
    Label amountLabel = new Label();
    JFXListView<Label> list = new JFXListView<>();
    JFXPopup popup = new JFXPopup();

    Label openLabel = new Label("Open Item");
    Label editLabel = new Label("Edit Item");
    Label deleteLabel = new Label("Delete Item");

    public ListPane() {
        super();
        list.getItems().add(openLabel);
        list.getItems().add(editLabel);
        list.getItems().add(deleteLabel);
        popup.setPopupContent(list);



        //layout
        FlowPane controlPane = new FlowPane();
        controlPane.setPadding(new Insets(12.5));
        controlPane.setHgap(25);
        controlPane.setPrefSize(350, 50);
        //titlePane.setBackground(new Background(new BackgroundFill(Color.rgb(0, 255, 0), CornerRadii.EMPTY, Insets.EMPTY)));
        StackPane tablePane = new StackPane();
        tablePane.setPrefSize(350, 550);
        //typesPane.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 0), CornerRadii.EMPTY, Insets.EMPTY)));


        filterField.textProperty().addListener((o, oldVal, newVal) -> {
            notesTable.setPredicate(userProp -> {
                final Note note = userProp.getValue();
                return note.getObservableTitle().get().contains(newVal)
                        || note.getObservableDate().get().contains(newVal)
                        || note.getObservableTypes().get().contains(newVal);
            });
        });
        filterField.setPrefWidth(300);

        //amountLabel.textProperty().bind(Bindings.createStringBinding(() -> String.valueOf(notesTable.getCurrentItemsCount()), notesTable.currentItemsCountProperty()));
        amountLabel.setText(String.valueOf(DataManager.getNotes().size()));
        amountLabel.setPrefWidth(50);

        controlPane.getChildren().addAll(filterField, amountLabel);
        tablePane.getChildren().addAll(notesTable);


        notesTable.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            if(event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2 && !event.isConsumed()) {
                event.consume();
                openNotes();
                updateAmount();
            }
            if(event.getButton().equals(MouseButton.MIDDLE) && event.getClickCount() == 2 && !event.isConsumed()) {
                event.consume();
                editNote();
                updateAmount();
            }
            if(event.getButton().equals(MouseButton.SECONDARY)) {
                popup.show(notesTable, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT, 400 - (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width - MouseInfo.getPointerInfo().getLocation().x), 550 - (GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height - MouseInfo.getPointerInfo().getLocation().y));
                updateAmount();
            }
        });

        notesTable.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if(event.getCode().equals(KeyCode.DELETE)) {
                deleteNotes();
                updateAmount();
            }
        });

        openLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            openNotes();
            popup.hide();
            updateAmount();
        });

        editLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            editNote();
            popup.hide();
            updateAmount();
        });

        deleteLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            deleteNotes();
            popup.hide();
            updateAmount();
        });


        this.getChildren().addAll(controlPane, tablePane);
    }

    private void openNotes() {
        for (TreeItem<Note> noteItem : notesTable.getSelectionModel().getSelectedItems()) {
            if (noteItem.getValue().getTypes().contains("URL")) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    try {
                        Desktop.getDesktop().browse(new URI(noteItem.getValue().getText()));
                    } catch (IOException | URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Main.setNotePane(noteItem.getValue(), false);
                break;
            }
        }
    }

    private void editNote() {
        for(TreeItem<Note> noteItem : notesTable.getSelectionModel().getSelectedItems()) {
            Main.setNotePane(noteItem.getValue(), true);
            break;
        }
    }

    private void deleteNotes() {
        for(TreeItem<Note> noteItem : notesTable.getSelectionModel().getSelectedItems()) {
            DataManager.removeNote(noteItem.getValue());
        }
    }

    private static JFXTreeTableView<Note> addNotesTable() {
        JFXTreeTableColumn<Note, String> titleColumn = new JFXTreeTableColumn<>("Title");
        titleColumn.setPrefWidth(185);
        titleColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Note, String> param) -> {
            if(titleColumn.validateValue(param)) return param.getValue().getValue().getObservableTitle();
            else return titleColumn.getComputedValue(param);
        });
        JFXTreeTableColumn<Note, String> typesColumn = new JFXTreeTableColumn<>("Types");
        typesColumn.setPrefWidth(100);
        typesColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Note, String> param) -> {
            if(typesColumn.validateValue(param)) return param.getValue().getValue().getObservableTypes();
            else return typesColumn.getComputedValue(param);
        });
        JFXTreeTableColumn<Note, String> dateColumn = new JFXTreeTableColumn<>("Date");
        dateColumn.setPrefWidth(100);
        dateColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Note, String> param) -> {
            if(dateColumn.validateValue(param)) return param.getValue().getValue().getObservableDate();
            else return dateColumn.getComputedValue(param);
        });

        JFXTreeTableView<Note> notesTable = new JFXTreeTableView<>(new RecursiveTreeItem<>(DataManager.getNotes(), RecursiveTreeObject::getChildren));
        notesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        notesTable.setShowRoot(false);
        notesTable.getColumns().setAll(titleColumn, typesColumn, dateColumn);
        return notesTable;
    }

    public void updateAmount() {
        Platform.runLater(() -> {
            amountLabel.setText(String.valueOf(DataManager.getNotes().size()));
            updateTrayIcon();
        });
    }

}
