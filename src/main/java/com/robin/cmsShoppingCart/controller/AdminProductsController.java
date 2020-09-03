package com.robin.cmsShoppingCart.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import javax.validation.Valid;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.robin.cmsShoppingCart.data.Category;
import com.robin.cmsShoppingCart.data.Product;
import com.robin.cmsShoppingCart.models.CategoryRepository;
import com.robin.cmsShoppingCart.models.ProductRepository;

@Controller
@RequestMapping("/admin/products")
public class AdminProductsController {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CategoryRepository categoryRepo;
    
    /*
     * 
     * 
     */
    @GetMapping
    public String index(Model model) {
    	//et all products from the product table
        List<Product> products = productRepo.findAll();
        //forward the products to the products view
        model.addAttribute("products", products);
        
        return "admin/products/index";
    }
    
/*
 * the form view binds data into the products, to extract product we use product in the parameters
 */
    @GetMapping("/add")
    public String add(Product product, Model model) {
    	
        //get all categories ,ie When a product is adding, the category must be chosen
        List<Category> categories = categoryRepo.findAll();
        
        model.addAttribute("categories", categories);

        return "admin/products/add";
    }
    
/*bindingResults checkes errors , if it has errors we simply return
 * RedirectAttributes is used to display the messages (success or error)
 * Model is used to store data and sent the attribute to the view
 */
    @PostMapping("/add")
    public String add(@Valid Product product, 
                        BindingResult bindingResult, 
                        MultipartFile file, 
                        RedirectAttributes redirectAttributes, 
                        Model model) throws IOException {

        List<Category> categories = categoryRepo.findAll();

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categories);
            return "admin/products/add";
        }
        
        /*
         * Code for uploading the image file 
         */
        boolean fileOK = false;
        byte[] bytes = file.getBytes();
        String filename = file.getOriginalFilename();
        Path path = Paths.get("src/main/resources/static/media/" + filename);

        if (filename.endsWith("jpg") || filename.endsWith("png") ) {
            fileOK = true;
        }

        redirectAttributes.addFlashAttribute("message", "Product added");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = product.getName().toLowerCase().replace(" ", "-");
        
        /*
         * checks the product exist or not , if product exist we simply forward a message ,
         * and product is not exist , we simply add the products to database.
         * and also checks the image file is okay
         */
        Product productExists = productRepo.findBySlug(slug);

        if (! fileOK ) {
            redirectAttributes.addFlashAttribute("message", "Image must be a jpg or a png");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        }
        else if ( productExists != null ) {
            redirectAttributes.addFlashAttribute("message", "Product exists, choose another");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        } else {
            product.setSlug(slug);
            product.setImage(filename);
            productRepo.save(product);
            
            Files.write(path, bytes); // to upload imae
        }

        return "redirect:/admin/products/add";
    }

    /*
     * code for editing the products 
     */
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {

        Product product = productRepo.getOne(id);
        List<Category> categories = categoryRepo.findAll();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);

        return "admin/products/edit";
    }
    /*
     * code for savin the edited file and producst, same as add products
     */
    @PostMapping("/edit")
    public String edit(@Valid Product product, 
                        BindingResult bindingResult, 
                        MultipartFile file, 
                        RedirectAttributes redirectAttributes, 
                        Model model) throws IOException {

        Product currentProduct = productRepo.getOne(product.getId());// fetch one record from the id we selected
        
        List<Category> categories = categoryRepo.findAll();

        if (bindingResult.hasErrors()) {
            model.addAttribute("productName", currentProduct.getName());
            model.addAttribute("categories", categories);
            return "admin/products/edit";
        }

        boolean fileOK = false;
        byte[] bytes = file.getBytes();
        String filename = file.getOriginalFilename();
        Path path = Paths.get("src/main/resources/static/media/" + filename);

        if (!file.isEmpty()) {
            if (filename.endsWith("jpg") || filename.endsWith("png") ) {
                fileOK = true;
            }
        } else {
            fileOK = true;
        }

        redirectAttributes.addFlashAttribute("message", "Product edited");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        String slug = product.getName().toLowerCase().replace(" ", "-");

        Product productExists = productRepo.findBySlugAndIdNot(slug, product.getId());

        if (! fileOK ) {
            redirectAttributes.addFlashAttribute("message", "Image must be a jpg or a png");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        }
        else if ( productExists != null ) {
            redirectAttributes.addFlashAttribute("message", "Product exists, choose another");
            redirectAttributes.addFlashAttribute("alertClass", "alert-danger");
            redirectAttributes.addFlashAttribute("product", product);
        } else {

            product.setSlug(slug);

            if (!file.isEmpty()) {
                Path path2 = Paths.get("src/main/resources/static/media/" + currentProduct.getImage());
                Files.delete(path2);
                product.setImage(filename);
                Files.write(path, bytes);
            } else {
                product.setImage(currentProduct.getImage());
            }

            productRepo.save(product);

        }

        return "redirect:/admin/products/edit/" + product.getId();
    }
    /*
     * Code for delete a product from the given id
     */
    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) throws IOException {

        Product product = productRepo.getOne(id);
        Product currentProduct = productRepo.getOne(product.getId());

        Path path2 = Paths.get("src/main/resources/static/media/" + currentProduct.getImage());
        Files.delete(path2);
        productRepo.deleteById(id);

        redirectAttributes.addFlashAttribute("message", "Product deleted");
        redirectAttributes.addFlashAttribute("alertClass", "alert-success");

        return "redirect:/admin/products";
        
    }

    
}