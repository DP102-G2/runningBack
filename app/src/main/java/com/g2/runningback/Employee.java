package com.g2.runningback;

import java.io.Serializable;

public class Employee implements Serializable {

    private String emp_no;
    private String emp_name;
    private String emp_pw;
    private String emp_id;
    private String emp_job;

    public Employee(String emp_no, String emp_name, String emp_pw, String emp_id, String emp_job) {
        this.emp_no = emp_no;
        this.emp_name = emp_name;
        this.emp_pw = emp_pw;
        this.emp_id = emp_id;
        this.emp_job = emp_job;
    }

    @Override
    public boolean equals(Object obj) {
        return this.emp_id == ((Employee) obj).emp_id;
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

    public String getEmp_job() {
        return emp_job;
    }

    public void setEmp_job(String emp_job) {
        this.emp_job = emp_job;
    }
}
