package com.wheel.wheelPicker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WheelPickerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WheelPickerApplication.class, args);
	}

}
