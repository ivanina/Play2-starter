package controllers;


import models.Customer;
import models.forms.CustomerForm;
import play.data.Form;
import play.data.FormFactory;
import play.data.validation.ValidationError;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import services.CustomerManager;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class CustomerController extends Controller {

    @Inject
    FormFactory formFactory;

    private CustomerManager customerManager = new CustomerManager();

    public Long someLongNumeric = 1L;

    public Result getCustomer(Long id){
        Customer customer = customerManager.getCustomer( id);
        return ok( views.html.customer.render(customer) );
    }

    public Result getAllCustomers(){
        return ok( views.html.customerList.render( customerManager.getAll() ) );
    }

    public Result addCustomer(){

        Customer customer = null;

        Form<CustomerForm> form = formFactory.form(CustomerForm.class);
        Map<String,List<ValidationError>> errors =  form.bindFromRequest().errors();
        if(errors == null){
            customer = new Customer(form.bindFromRequest().get()) ;

            customer.setId(new Random().nextLong());
        }

        if(customer == null){
            return ok(views.html.customerAdd.render(form));
        }else {
            return getCustomer(customer.getId());
        }
    }

    public Result upload(){
        Http.MultipartFormData<File> body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart<File> picture = body.getFile("picture");
        if(picture == null){
            return badRequest("picture has not upload");
        }else{
            return ok("ok<br/>"+picture.getFilename()+"<br/>"+picture.getContentType()).as("text/html");
        }

    }
}
