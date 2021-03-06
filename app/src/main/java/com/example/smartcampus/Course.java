package com.example.smartcampus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Course {

    public String code;
    public String name;
    //public ArrayList<String> SID = new ArrayList<String>();
    public ArrayList<Calendar> start = new ArrayList<Calendar>();
    public ArrayList<Calendar> end = new ArrayList<Calendar>();
    public double xMin;
    public double xMax;
    public double yMin;
    public double yMax;

    public Course(){

    }
    public Course(String c, String n, ArrayList<Calendar> s, ArrayList<Calendar> e,
                  double xMin, double xMax, double yMin, double yMax){
        code=c;
        name=n;
        start=s;
        end=e;
        this.xMax = xMax;
        this.xMin = xMin;
        this.yMax = yMax;
        this.yMin = yMin;
    }

    public ArrayList<Calendar> getStart() {
        return start;
    }
    public void setStart(ArrayList<Calendar> start) {
        this.start = start;
    }

    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Calendar> getEnd() {
        return end;
    }
    public void setEnd(ArrayList<Calendar> end) {
        this.end = end;
    }

    public double getxMin() {
        return xMin;
    }
    public void setxMin(double xMin) {
        this.xMin = xMin;
    }

    public double getyMax() {
        return yMax;
    }
    public void setyMax(double yMax) {
        this.yMax = yMax;
    }

    public double getxMax() {
        return xMax;
    }
    public void setxMax(double xMax) {
        this.xMax = xMax;
    }

    public double getyMin() {
        return yMin;
    }
    public void setyMin(double yMin) {
        this.yMin = yMin;
    }

    public String toString(){
        String s="";

        s += "Code: " + code + ", name: " + name + "\nStarts at: ";

        SimpleDateFormat DateFor = new SimpleDateFormat("E HH:mm");
        for (int i=0; i<start.size(); i++) {
            String stringDate = DateFor.format(start.get(i).getTime());
            s += stringDate + " ";
        }

        return s;
    }

}
