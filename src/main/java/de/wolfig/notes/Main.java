package de.wolfig.notes;

import com.jfoenix.controls.JFXButton;
import de.wolfig.notes.fx.AddPane;
import de.wolfig.notes.fx.ListPane;
import de.wolfig.notes.fx.NotePane;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends Application {

    private static final AddPane addPane = new AddPane();
    private static final ListPane listPane = new ListPane();
    private static final NotePane notePane = new NotePane();
    private static final Scene addScene = new Scene(addPane);
    private static final Scene listScene = new Scene(listPane);
    private static final Scene noteScene = new Scene(notePane);
    private static Stage _Stage;
    private static TrayIcon trayIcon;

    public static void main(String[] args) {
        DataManager.loadNotes();
        Runtime.getRuntime().addShutdownHook(new ShutDownTask());
        launch();
    }

    private void addTrayIcon(Stage stage) {
        Platform.setImplicitExit(false);

        String pattern = "dd.MM.yy - HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

        if(!SystemTray.isSupported()) {
            return;
        }

        final PopupMenu popupMenu = new PopupMenu();
        final SystemTray tray = SystemTray.getSystemTray();
        trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(Main.class.getResource("/images/icon.png")));
        trayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getButton() == MouseEvent.BUTTON1) {
                    Platform.runLater(() -> {
                        if(stage.isShowing()) stage.hide();
                        else stage.show();
                    });
                }
            }
        });
        updateTrayIcon();

        MenuItem addItem = new MenuItem("Add new Note");
        MenuItem addClipBoardItem = new MenuItem("Add Note from Clipboard");
        MenuItem listItem = new MenuItem("List all Notes");
        String onStartUpLabel = StartUp.isOnStartUpEnable() ? "Starts with Windows" : "Starts not with Windows";
        MenuItem onStartItem = new MenuItem(onStartUpLabel);
        MenuItem exitItem = new MenuItem("Exit");

        addItem.addActionListener(e -> Platform.runLater(() -> {
            addPane.clearInputs();
            stage.setScene(addScene);
            stage.show();
        }));

        addClipBoardItem.addActionListener(e -> {
            Pattern urlPattern = Pattern.compile("\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
            ArrayList<String> types = new ArrayList<>();
            String title = "";
            String text = "";

            try {
                String clipboard = String.valueOf(Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
                Matcher matcher = urlPattern.matcher(clipboard);
                while (matcher.find()) {
                    title = getTitle(clipboard);
                    types.add("URL");
                    if(clipboard.toLowerCase().contains("github")) types.add("GitHub");
                    else if(clipboard.toLowerCase().contains("youtube") || clipboard.toLowerCase().contains("youtu.be")) types.add("YouTube");
                    else if(clipboard.toLowerCase().contains("twitter")) types.add("Twitter");
                    else if(clipboard.toLowerCase().contains("stackoverflow")) types.add("StackOverflow");
                    else if(clipboard.toLowerCase().contains("reddit")) types.add("Reddit");
                    else if(clipboard.toLowerCase().contains("instagram")) types.add("Instagram");
                    else if(clipboard.toLowerCase().contains("google")) types.add("Google");
                }
                if(types.size() == 0) types.add("Not defined");
                String date = simpleDateFormat.format(new Date());
                if(title.equals("")) title = clipboard;
                text = clipboard;
                DataManager.addNote(new Note(DataManager.getLastId(), title, date, types, text));
                listPane.updateAmount();
                updateTrayIcon();
            } catch (UnsupportedFlavorException | IOException unsupportedFlavorException) {
                unsupportedFlavorException.printStackTrace();
            }
        });

        listItem.addActionListener(e -> Platform.runLater(() -> {
            listPane.updateAmount();
            stage.setScene(listScene);
            stage.show();
        }));

        onStartItem.addActionListener(e -> Platform.runLater(() -> {
            if(!StartUp.isOnStartUpEnable()) {
                StartUp.createStartUpBatchFile();
                onStartItem.setLabel("Starts with Windows");
            }
            else {
                StartUp.deleteStartUpBatchFile();
                onStartItem.setLabel("Starts not with Windows");
            }
        }));


        exitItem.addActionListener(e -> System.exit(0));

        //Add components to pop-up menu
        popupMenu.add(addItem);
        popupMenu.add(addClipBoardItem);
        popupMenu.add(listItem);
        popupMenu.addSeparator();
        popupMenu.add(onStartItem);
        popupMenu.addSeparator();
        popupMenu.add(exitItem);

        trayIcon.setPopupMenu(popupMenu);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }

    }

    @Override
    public void start(Stage stage) {
        addTrayIcon(stage);
        _Stage = stage;



        StackPane mainPane = new StackPane();
        mainPane.setPrefSize(380, 580);
        mainPane.setPadding(new Insets(10));
        mainPane.setAlignment(Pos.CENTER);

        javafx.scene.control.Label header = new javafx.scene.control.Label("Notes");
        javafx.scene.control.Label author = new javafx.scene.control.Label("Johannes W");
        javafx.scene.control.Label version = new javafx.scene.control.Label("v1.0");
        VBox vBox = new VBox(header, author, version);

        header.getStyleClass().add("main-header");
        author.getStyleClass().add("main-author");
        version.getStyleClass().add("main-version");

        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);

        HBox hBox = new HBox();
        JFXButton addButton = new JFXButton("Add note");
        JFXButton listButton = new JFXButton("List all notes");

        addButton.getStyleClass().add("button-raised");
        addButton.setMaxWidth(100);
        listButton.getStyleClass().add("button-raised");
        listButton.setMaxWidth(100);


        addButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            addPane.clearInputs();
            stage.setScene(addScene);
            stage.show();
        });

        listButton.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_CLICKED, event -> {
            openListPane();
        });

        hBox.setSpacing(10);
        hBox.setAlignment(Pos.BOTTOM_CENTER);
        hBox.getChildren().addAll(addButton, listButton);


        mainPane.getChildren().addAll(vBox, hBox);


        final Scene scene = new Scene(mainPane, 400, 600);
        scene.getStylesheets().addAll(Main.class.getResource("/css/jfoenix-components.css").toExternalForm(),
                Main.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                Main.class.getResource("/css/jfoenix-fonts.css").toExternalForm());
        scene.setFill(Color.TRANSPARENT);
        stage.setTitle("Notes");
        stage.setScene(scene);
        stage.setAlwaysOnTop(true);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setX(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width-400);
        stage.setY(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height-600);
    }

    public static void setNotePane(Note note, boolean editable) {
        NotePane.setNote(note, editable);
        _Stage.setScene(noteScene);
        _Stage.show();
    }

    public static void openListPane() {
        listPane.updateAmount();
        _Stage.setScene(listScene);
        _Stage.show();
    }

    private static String getTitle(String url) {
        InputStream response = null;
        String title = url;
        try {
            response = new URL(url).openStream();
            String responseBody = new Scanner(response).useDelimiter("\\A").next();
            title = responseBody.substring(responseBody.indexOf("<title>") + 7, responseBody.indexOf("</title>"));

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return title;
    }

    public static void updateTrayIcon() {
        try {
            BufferedImage trayIconImage = ImageIO.read(Main.class.getResource("/images/icon.png"));
            Graphics2D graphics2D = (Graphics2D) trayIconImage.getGraphics();
            graphics2D.setStroke(new BasicStroke(1));
            graphics2D.setColor(new java.awt.Color(200, 100, 100));
            int x2 = DataManager.getNotes().size() > 9 ? 3 : 10;
            graphics2D.drawString(String.valueOf(DataManager.getNotes().size()), x2, 16);
            graphics2D.setColor(new java.awt.Color(255, 255, 255));
            int x = DataManager.getNotes().size() > 9 ? 2 : 9;
            graphics2D.drawString(String.valueOf(DataManager.getNotes().size()), x, 15);
            trayIcon.setImage(trayIconImage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
