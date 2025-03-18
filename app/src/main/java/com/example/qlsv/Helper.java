package com.example.qlsv;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Helper implements Serializable {
    String email, password, fullname, age, phone, status, image, role;
    Map<String, String> loginHistory = new HashMap<>();
    public Helper() {
    }
    public Helper(String email, String password, String fullname, String age, String phone, String status, String role) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.age = age;
        this.phone = phone;
        this.status = status;
        this.role = role;
    }

    public Helper(String email, String password, String fullname, String age, String phone, String status, String role, String image) {
        this.email = email;
        this.password = password;
        this.fullname = fullname;
        this.age = age;
        this.phone = phone;
        this.status = status;
        this.role = role;
        this.image = image;
        this.loginHistory = new HashMap<>();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullname() {
        return fullname;
    }

    public String getAge() {
        return age;
    }

    public String getPhone() {
        return phone;
    }
    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Map<String, String> getLoginHistory() {
        return loginHistory;
    }

    public void setLoginHistory(Map<String, String> loginHistory) {
        this.loginHistory = loginHistory;
    }

    // funtion add login timestamp
    public void addLoginTimestamp(String key, String timestamp) {
        if (this.loginHistory == null) {
            this.loginHistory = new HashMap<>();
        }
        this.loginHistory.put(key, timestamp);
    }
}
