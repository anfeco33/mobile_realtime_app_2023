package com.example.qlsv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Student {
    private String id;
    private String name;
    private String gender;
    private String age;
    private String studentClass;
    private String email;
    private List<String> certificates;

    public Student() {
    }

    public Student(String id, String name, String gender, String age, String studentClass, String email, List<String> certificates) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.studentClass = studentClass;
        this.email = email;
        this.certificates = certificates;
    }
    public Student(String id, String name, String gender, String age, String studentClass, String email) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.studentClass = studentClass;
        this.email = email;
        this.certificates = new ArrayList<>();
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
