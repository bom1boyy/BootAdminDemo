package com.netand.eddy.bootadmindemo;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAdminServer
public class BootAdminDemoApplication {

	public static void main( String[] args ) {
		SpringApplication.run( BootAdminDemoApplication.class, args );
	}
}
