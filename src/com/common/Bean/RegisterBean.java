package com.common.Bean;

import java.io.Serializable;

/**
 * Created by etech on 1/6/16.
 */
public class RegisterBean implements Serializable {
    String parent1_name="";
    String parent2_name="";
    String email="";
    String pincode="";
    String confirmpincode="";
    String parent1_phoneno="";
    String parent2_phoneno="";

    public String getParent1_name() {
        return parent1_name;
    }

    public void setParent1_name(String parent1_name) {
        this.parent1_name = parent1_name;
    }

    public String getParent2_name() {
        return parent2_name;
    }

    public void setParent2_name(String parent2_name) {
        this.parent2_name = parent2_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getConfirmpincode() {
        return confirmpincode;
    }

    public void setConfirmpincode(String confirmpincode) {
        this.confirmpincode = confirmpincode;
    }

    public String getParent1_phoneno() {
        return parent1_phoneno;
    }

    public void setParent1_phoneno(String parent1_phoneno) {
        this.parent1_phoneno = parent1_phoneno;
    }

    public String getParent2_phoneno() {
        return parent2_phoneno;
    }

    public void setParent2_phoneno(String parent2_phoneno) {
        this.parent2_phoneno = parent2_phoneno;
    }
}
