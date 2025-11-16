package controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import model.Task;
import service.TaskService;

@RestController
public class TaskController 
{
	@GetMapping("/control")
	public String control()
	{
		return "working";
	}
	
	@PostMapping
    public String create(@RequestAttribute Task task) throws Exception {
		return "working create";
    }

    // READ
    @GetMapping("/{id}")
    public Task get(@PathVariable String id) throws Exception {
    	TaskService service=new TaskService(null);
        return service.getTask(id);
    }

    // UPDATE
    @PutMapping("/{id}")
    public String update(@RequestBody Task task) throws Exception {
    	TaskService service=new TaskService(null);
        return service.updateTask(task);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable String id) {
    	TaskService service=new TaskService(null);
        return service.deleteTask(id);
    }

}