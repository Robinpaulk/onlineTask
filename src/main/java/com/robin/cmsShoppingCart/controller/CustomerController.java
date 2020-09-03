package com.robin.cmsShoppingCart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.robin.cmsShoppingCart.models.Customer;
import com.robin.cmsShoppingCart.models.CustomerRepo;
import com.robin.cmsShoppingCart.service.CustomerServices;



@Controller
public class CustomerController {
	@Autowired
	private CustomerServices customerService;
	@Autowired
	private CustomerRepo customerRepo;
	//Display all customer from services
	//send data to the view
	@RequestMapping("/")
	public String homePage(Model model) {
		model.addAttribute("listCustomer", customerService.getAllCustomer());
		return "home";
	}
	
	@GetMapping("/showNewCustomerForm")
	public String showNewCustomer(Model model) {
		Customer customer = new Customer();
		model.addAttribute("customer", customer);
		return "newform";
	}
	
	@PostMapping("/saveCustomer")
	public String saveCustomer(@ModelAttribute("Customer") Customer customer) {
		customerService.saveCustomer(customer);
		return "redirect:/";
	}
	
	@GetMapping("/edit/{id}")
	public String editCustomerForm(@PathVariable(value = "id") int id, Model model) {
		Customer customer = customerService.getCustomerById(id);
		model.addAttribute("customer", customer);
		return "editForm";
	}
	
	@GetMapping("/delete/{id}")
	public String deleteCustomer(@PathVariable(value ="id") int id) {
		this.customerService.deleteCustomer(id);
		return "redirect:/";
	}
}
