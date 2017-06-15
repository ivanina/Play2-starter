package models.forms;


import models.Customer;
import play.data.validation.Constraints;

public class CustomerForm extends Customer{

    @Constraints.Required
    protected String email;

}
