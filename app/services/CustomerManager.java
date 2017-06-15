package services;


import models.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerManager {
    public Customer getCustomer(Long id) {
        // for test
        if (id % 2 == 0)
            return new Customer(id, "John Smith", "john@smith.com");
        else
            return new Customer(id, "Adolf Albin", "adolf.albin@name.com");
    }

    public List<Customer> getAll() {
        return new ArrayList<Customer>() {{
            add(new Customer(1L, "John II ", "john@gmail.com"));
            add(new Customer(2L, "John Smith", "john@smith.com"));
            add(new Customer(3L, "Adolf Albin", "adolf.albin@name.com"));
        }};
    }
}
