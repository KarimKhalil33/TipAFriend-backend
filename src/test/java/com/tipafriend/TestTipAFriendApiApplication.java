package com.tipafriend;

import org.springframework.boot.SpringApplication;

public class TestTipAFriendApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(TipAFriendApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
