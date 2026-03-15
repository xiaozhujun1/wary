package com.wary;

public class ModifiedText {
    private int line;
    private String text;
    public ModifiedText(int line, String text) {
        this.line = line;
        this.text = text;
    }
    public int getLine() {
        return line;
    }
    public String getText() {
        return text;
    }
    public void setLine(int line) {
        this.line = line;
    }
    public void setText(String text) {
        this.text = text;
    }
}
