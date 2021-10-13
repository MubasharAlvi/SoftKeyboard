package com.example.softkeyboard.Models;

import java.io.Serializable;

public class ModelClass implements Serializable {
    private boolean state;
    private String name;
    private String translationTo;
    private String translationFrom;
    private String languageTo;
    private String languageFrom;
    private int inputDrawable;
    private int outputDrawable;
    private boolean isRight;
    private String inputLangCode;

    public String getInputLangCode() {
        return inputLangCode;
    }
    public void setInputLangCode(String inputLangCode) {
        this.inputLangCode = inputLangCode;
    }
    public String getOutputLangCode() {
        return outputLangCode;
    }
    public void setOutputLangCode(String outputLangCode) {
        this.outputLangCode = outputLangCode;
    }
    private String outputLangCode;
    public int getInputDrawable() {
        return inputDrawable;
    }
    public void setInputDrawable(int inputDrawable) { this.inputDrawable = inputDrawable; }
    public int getOutputDrawable() { return outputDrawable; }
    public void setOutputDrawable(int outputDrawable) {
        this.outputDrawable = outputDrawable;
    }
    public boolean getState() {
        return state;
    }
    public void setState(boolean state) {
        this.state = state;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getTranslationTo() {
        return translationTo;
    }
    public void setTranslationTo(String translationTo) {
        this.translationTo = translationTo;
    }
    public String getTranslationFrom() {
        return translationFrom;
    }
    public void setTranslationFrom(String translationFrom) { this.translationFrom = translationFrom; }
    public boolean isRight() { return isRight; }
    public void setRight(boolean right) { isRight = right; }
}

