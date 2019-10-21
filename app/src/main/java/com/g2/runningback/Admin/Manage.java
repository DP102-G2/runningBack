package com.g2.runningback.Admin;

import java.io.Serializable;

public class Manage implements Serializable {

    private String emp_no;
    private String emp_name;
    private String emp_pw;
    private String emp_id;
    private String job_no;
    private String job_name;

    public Manage(String emp_no, String emp_name, String emp_id,String emp_pw, String job_no) {
        this.emp_no = emp_no;
        this.emp_name = emp_name;
        this.emp_pw = emp_pw;
        this.emp_id = emp_id;
        this.job_no = job_no;
    }

    public String getJob_name() {
        return job_name;
    }

    public void setJob_name(String job_name) {
        this.job_name = job_name;
    }

    public String getEmp_no() {
        return emp_no;
    }

    public void setEmp_no(String emp_no) {
        this.emp_no = emp_no;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_pw() {
        return emp_pw;
    }

    public void setEmp_pw(String emp_pw) {
        this.emp_pw = emp_pw;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getJob_no() {
        return job_no;
    }

    public void setJob_no(String job_no) {
        this.job_no = job_no;
    }
}
