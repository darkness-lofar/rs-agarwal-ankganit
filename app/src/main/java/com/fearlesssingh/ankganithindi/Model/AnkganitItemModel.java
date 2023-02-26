package com.fearlesssingh.ankganithindi.Model;


public class AnkganitItemModel {


    String chapterNameHindi,chapterNameEnglish,chapterNumber, button;

    public AnkganitItemModel(String chapterNumber,String chapterNameHindi, String chapterNameEnglish, String button) {
        this.chapterNameHindi = chapterNameHindi;
        this.chapterNameEnglish = chapterNameEnglish;
        this.chapterNumber = chapterNumber;
        this.button = button;
    }

    public String getButton() {
        return button;
    }

    public void setButton(String button) {
        this.button = button;
    }

    public String getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(String chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getChapterNameHindi() {
        return chapterNameHindi;
    }

    public void setChapterNameHindi(String chapterNameHindi) {
        this.chapterNameHindi = chapterNameHindi;
    }

    public String getChapterNameEnglish() {
        return chapterNameEnglish;
    }

    public void setChapterNameEnglish(String chapterNameEnglish) {
        this.chapterNameEnglish = chapterNameEnglish;
    }
}
