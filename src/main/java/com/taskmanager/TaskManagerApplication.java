package com.taskmanager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TaskManagerApplication {

	public static void main(String[] args) 
	{
		SpringApplication.run(TaskManagerApplication.class, args);
		System.out.println(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
	}

}
