package com.example.smartcampus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import ajava.beans.XMLDecoder;
import ajava.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;

public class Student {

    public String ID;
    public String name;
    public ArrayList<Course> myCourses = new ArrayList<Course>();

    public Student (){

    }

    public Student (String id, String n, ArrayList<Course> c){
        ID = id;
        name = n;
        myCourses = c;
    }

    public String getID() {
        return ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Course> getMyCourses() {
        return myCourses;
    }
    public void setMyCourses(ArrayList<Course> myCourses) {
        this.myCourses = myCourses;
    }

    public void  WriteXML() throws FileNotFoundException
    {
        XMLEncoder e = new XMLEncoder( new BufferedOutputStream(new FileOutputStream("data.xml")));
        e.writeObject(this);
        e.close();
    }

    public Student ReadXML() throws FileNotFoundException
    {
        XMLDecoder d = new XMLDecoder(new FileInputStream("data.xml"));
        Student s = (Student) d.readObject();
        d.close();
        return s;
    }

}
