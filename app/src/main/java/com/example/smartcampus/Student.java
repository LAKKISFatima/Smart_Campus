package com.example.smartcampus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import ajava.beans.XMLDecoder;
import ajava.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.Date;

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

    public static Student initialize() {
        ArrayList<Course> c = new ArrayList<Course>();

        ArrayList<Calendar> dstart = new ArrayList<Calendar>();
        Calendar cal1 = Calendar.getInstance(), cal2 = Calendar.getInstance();
        cal1.set(2021, 11, 29, 10, 0);
        cal2.set(2021, 0, 29, 10, 0);
        dstart.add(cal1); dstart.add(cal2);
        //dstart.add(new Date(2020, 12, 29, 10, 00));
        //dstart.add(new Date(2021, 1, 18, 10, 0));

        ArrayList<Calendar> dend = new ArrayList<Calendar>();
        Calendar cal3 = Calendar.getInstance(), cal4 = Calendar.getInstance();
        cal3.set(2021, 11, 29, 11, 0);
        cal4.set(2021, 0, 30, 9, 0);
        dend.add(cal3); dend.add(cal4);
        //dend.add(new Date(2020, 12, 29, 11, 0));
        //dend.add(new Date(2021, 1, 19, 9, 0));
        Course c1 = new Course("I3300", "DataS", dstart, dend, 33.828470, 33.829086, 35.521337, 35.523058);

        ArrayList<Calendar> dstart2 = new ArrayList<Calendar>();
        Calendar cal5 = Calendar.getInstance(), cal6 = Calendar.getInstance();
        cal5.set(2021, 0, 1, 23, 30);
        cal6.set(2021, 11, 31, 10, 0);
        dstart2.add(cal5); dstart2.add(cal6);
        //dstart2.add(new Date(2020, 12, 28, 23, 30));
        //dstart2.add(new Date(2020, 12, 31, 10, 0));

        ArrayList<Calendar> dend2 = new ArrayList<Calendar>();
        Calendar cal7 = Calendar.getInstance(), cal8 = Calendar.getInstance();
        cal7.set(2021, 0, 10, 13, 0);
        cal8.set(2021, 11, 31, 11, 0);
        dend2.add(cal7); dend2.add(cal8);
        //dend2.add(new Date(2020, 12, 29, 13, 0));
        //dend2.add(new Date(2020, 12, 31, 11, 0));
        Course c2 = new Course("E2200", "Mechanics", dstart2, dend2, 33.825023, 33.825829, 35.520459, 35.521943);

        c.add(c1);
        c.add(c2);

        return new Student("1234", "Hello", c);

    }


}
