package com.robin.cmsShoppingCart.models;

import org.springframework.data.jpa.repository.JpaRepository;



public interface AdminRepo extends JpaRepository<Admin, Integer>{
	
	 Admin findByUsername(String username);
}
