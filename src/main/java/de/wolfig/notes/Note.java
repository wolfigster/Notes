package de.wolfig.notes;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Note extends RecursiveTreeObject<Note> implements Serializable
{

    @SerializedName("Id")
    @Expose
    private String id;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("Date")
    @Expose
    private String date;
    @SerializedName("Types")
    @Expose
    private List<String> types = null;
    @SerializedName("Text")
    @Expose
    private String text;
    private final static long serialVersionUID = 3468328832285869820L;

    /**
     * No args constructor for use in serialization
     *
     */
    public Note() {
    }

    /**
     *
     * @param date
     * @param types
     * @param text
     * @param title
     */
    public Note(String id, String title, String date, List<String> types, String text) {
        super();
        this.id = id;
        this.title = title;
        this.date = date;
        this.types = types;
        this.text = text;
    }


    public StringProperty getObservableId() {
        return new SimpleStringProperty(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Note withId(String id) {
        this.id = id;
        return this;
    }


    public StringProperty getObservableTitle() {
        return new SimpleStringProperty(title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Note withTitle(String title) {
        this.title = title;
        return this;
    }


    public StringProperty getObservableDate() {
        return new SimpleStringProperty(date);
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Note withDate(String date) {
        this.date = date;
        return this;
    }


    public StringProperty getObservableTypes() {
        StringBuilder stringBuilder = new StringBuilder();
        for(String type : types) stringBuilder.append(type).append(", ");
        return new SimpleStringProperty(stringBuilder.toString().substring(0, stringBuilder.toString().length()-1));
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

    public Note withTypes(List<String> types) {
        this.types = types;
        return this;
    }


    public StringProperty getObservableText() {
        return new SimpleStringProperty(text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Note withText(String text) {
        this.text = text;
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(date).append(id).append(types).append(text).append(title).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Note) == false) {
            return false;
        }
        Note rhs = ((Note) other);
        return new EqualsBuilder().append(date, rhs.date).append(id, rhs.id).append(types, rhs.types).append(text, rhs.text).append(title, rhs.title).isEquals();
    }

}