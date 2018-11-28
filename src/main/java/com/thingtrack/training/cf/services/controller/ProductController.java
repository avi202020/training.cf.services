package com.thingtrack.training.cf.services.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.http.ResponseEntity;

import javax.validation.Valid;
import java.util.List;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import com.thingtrack.training.cf.services.domain.Product;
import com.thingtrack.training.cf.services.exception.ResourceNotFoundException;
import com.thingtrack.training.cf.services.repository.ProductRepository;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api")
@Api("Set of endpoints for Creating, Retrieving, Updating and Deleting of Products.")
public class ProductController {
    @Autowired
    ProductRepository productRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @GetMapping("/products")
    @ApiOperation("Returns list of all Products in the system.")
    public List<Product> getAllProducts() {
    	logger.info("Get all products");
    	
        List<Product> products = productRepository.findAll();

        return products;
    }

    @GetMapping("/products/{id}")
    @ApiOperation("Returns a specific product by their identifier. 404 if does not exist.")
    public Product getProductById(@PathVariable(value = "id") Long productId) {
    	logger.info("Get product by Id: " + productId);
    	
        return productRepository.findById(productId)
               .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
    }

    @PostMapping("/products")
    @ApiOperation("Creates a new product.")
    public Product createProduct(@Valid @RequestBody Product product) {
    	logger.info("Save Product with details: " + product);
    	
        return productRepository.save(product);
    }

    @PutMapping("/products/{id}")
    @ApiOperation("Updates a product by Id from the system. 404 if the person's identifier is not found.")
    public Product updateProduct(@PathVariable(value = "id") Long productId,
                                 @Valid @RequestBody Product productDetails) {

    	logger.info("Update product by Id: " + productId + " with details: " + productDetails);
    	
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));              

        product.setCode(productDetails.getCode());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setActive(productDetails.isActive());

        Product updatedProduct = productRepository.save(product);

        return updatedProduct;
    }

    @DeleteMapping("/products/{id}")
    @ApiOperation("Deletes a product by Id from the system. 404 if the person's identifier is not found.")
    public ResponseEntity<?> deleteProduct(@PathVariable(value = "id") Long productId) {
    	logger.info("Delete product by Id: " + productId);
    	
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
                
        productRepository.delete(product);

        return ResponseEntity.ok().build();
    }
}