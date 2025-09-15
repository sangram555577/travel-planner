package com.TripFinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TripFinderApplication {
    public static void main(String[] args) {
        SpringApplication.run(TripFinderApplication.class, args);
        System.err.println("TripFinder is running...");
    }
}
