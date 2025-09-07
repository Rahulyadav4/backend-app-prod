package taskmanager;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller 
{
	@GetMapping("/control")
	public String control()
	{
		return "working";
	}

}
