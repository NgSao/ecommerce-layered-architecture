package com.nguyensao.ecommerce_layered_architecture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EcommerceLayeredArchitectureApplication {

	public static void main(String[] args) {
		SpringApplication.run(EcommerceLayeredArchitectureApplication.class, args);
	}

}
