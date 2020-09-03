package com.robin.cmsShoppingCart.service;

import java.util.List;

import com.robin.cmsShoppingCart.models.Customer;



public interface CustomerServices {
	
	List<Customer> getAllCustomer();
	
	void saveCustomer(Customer customer);
	
	Customer getCustomerById(int id);
	
	void deleteCustomer(int id);
	
}
