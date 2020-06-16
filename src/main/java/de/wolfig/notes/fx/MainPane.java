package de.wolfig.notes.fx;

import de.wolfig.notes.Main;
import javafx.geometry.Insets;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainPane extends VBox {

    public MainPane() {
        super();
        StackPane.setMargin(this, new Insets(100));
        this.setMinSize(400, 600);
        this.setPrefSize(400, 600);
        this.getStylesheets().addAll(Main.class.getResource("/css/jfoenix-components.css").toExternalForm(),
                Main.class.getResource("/css/jfoenix-design.css").toExternalForm(),
                Main.class.getResource("/css/jfoenix-fonts.css").toExternalForm());
    }
}
