package com.example.softkeyboard.Models;

public  class Languages {

    String country_name;
    String countrycode;
    int flag;


    public Languages() {
    }

    public Languages(String country_name, String countrycode, int flag) {
        this.country_name = country_name;
        this.countrycode = countrycode;
        this.flag = flag;
    }

    public String getCountry_name() {
        return country_name;
    }

    public void setCountry_name(String country_name) {
        this.country_name = country_name;
    }

    public String getCountrycode() {
        return countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }


}
