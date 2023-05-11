package org.charess.training.domain.security;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import java.time.LocalDateTime;

@MappedSuperclass
public class Audit extends ID{

    @Column(name = "created")
    private LocalDateTime created;

    @Column(name = "creator")
    private Integer creator;

    @Column(name = "edited")
    private LocalDateTime edited;

    @Column(name = "editor")
    private Integer editor;

    @Column(name = "editor_name", length = 255)
    private String editorName;

    public Audit() {
    }

    public Audit(Audit audit) {
        this.created = audit.created;
        this.creator = audit.creator;
        this.edited = audit.edited;
        this.editor = audit.editor;
        this.editorName = audit.editorName;
    }

    public Audit(LocalDateTime created, Integer creator, LocalDateTime edited, Integer editor, String editorName) {
        this.created = created;
        this.creator = creator;
        this.edited = edited;
        this.editor = editor;
        this.editorName = editorName;
    }

    public Audit(LocalDateTime created, Integer creator, String editorName) {
        this.created = created;
        this.creator = creator;
        this.editorName = editorName;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public LocalDateTime getEdited() {
        return edited;
    }

    public void setEdited(LocalDateTime edited) {
        this.edited = edited;
    }

    public Integer getEditor() {
        return editor;
    }

    public void setEditor(Integer editor) {
        this.editor = editor;
    }


    public String getEditorName() {
        return this.editorName;
    }

    public void setEditorName(String editorName) {
        this.editorName = editorName;
    }
}
