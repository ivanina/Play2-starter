package models;


import models.forms.CustomerForm;

import java.util.List;

public class Customer {
    protected Long id;
    protected String name;
    protected String email;
    protected List<String> socials;

    public Customer() {}
    public Customer(CustomerForm form) {
        setName(form.getName());
        setEmail(form.getEmail());
    }

    public Customer(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getSocials() {
        return socials;
    }

    public void setSocials(List<String> socials) {
        this.socials = socials;
    }
}
