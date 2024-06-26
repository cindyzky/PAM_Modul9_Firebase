package com.example.praktikumpamm9;

public class Note {
    private String noteId;
    private String date;
    private String title;
    private String description;

    public Note() {
    }

    public Note(String date, String title, String description) {
        this.date = date;
        this.title = title;
        this.description = description;
    }

    public String getNoteId() {
        return noteId;
    }
    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
