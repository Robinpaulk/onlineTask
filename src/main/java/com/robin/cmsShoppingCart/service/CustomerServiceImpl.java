package com.robin.cmsShoppingCart.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.robin.cmsShoppingCart.models.Customer;
import com.robin.cmsShoppingCart.models.CustomerRepo;

@Service
public class CustomerServiceImpl implements CustomerServices {

	@Autowired
	private CustomerRepo customerRepo;

	@Override
	public List<Customer> getAllCustomer() {

		return customerRepo.findAll();
	}

	@Override
	public void saveCustomer(Customer customer) {
		this.customerRepo.save(customer);
	}

	@Override
	public Customer getCustomerById(int id) {
		Optional<Customer> optional = customerRepo.findById(id);
		Customer customer = null;
		if (optional.isPresent()) {
			customer = optional.get();
		} else {
			throw new ArithmeticException("Customer not found for id:" + id);
		}
		return customer;
	}

	@Override
	public void deleteCustomer(int id) {
		this.customerRepo.deleteById(id);
	}

}
