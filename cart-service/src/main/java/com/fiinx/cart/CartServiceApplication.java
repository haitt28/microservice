package com.fiinx.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication(scanBasePackages = {
    "com.fiinx.cart",
    "com.fiinx.common"
})
@EnableJpaAuditing
@EnableCaching
public class CartServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(CartServiceApplication.class, args);
    }
}
