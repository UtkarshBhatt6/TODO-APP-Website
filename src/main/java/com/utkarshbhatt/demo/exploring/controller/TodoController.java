package com.utkarshbhatt.demo.exploring.controller;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.validation.Valid;
import javax.xml.crypto.Data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.utkarshbhatt.demo.exploring.service.TodoService;
import com.utkarshbhatt.demo.exploring.model.Todo;

@Controller

public class TodoController {
	@Autowired
	TodoService service;
	@InitBinder
	 public void initBinder(WebDataBinder binder) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		binder.registerCustomEditor(Date.class, new CustomDateEditor(
				dateFormat, false));
	}
	@RequestMapping(value="/list-todos",method=RequestMethod.GET)
     public String showTodos(ModelMap model) {
		String name=getLoggedinUserName(model);
		model.put("todos",service.retrieveTodos(name));
    	    return "list-todos";
     }
	private String getLoggedinUserName(ModelMap model) {
		Object principal=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(principal instanceof UserDetails) {
			return ((UserDetails)principal).getUsername();
		}
		return principal.toString();
	}
	
	@RequestMapping(value="/add-todo",method=RequestMethod.GET)
    public String showAddTodoPage(ModelMap model) {
		model.addAttribute("todo", new Todo(0, getLoggedinUserName(model), "Default Desc",
				new Date(), false));
	    return "todo";
 } 
 
	@RequestMapping(value="/delete-todo",method=RequestMethod.GET)
    public String deleteTodo(@RequestParam int id) {
		service.deleteTodo(id);
	    return "redirect:/list-todos";
 } 
	@RequestMapping(value="/update-todo",method=RequestMethod.GET)
    public String showUpdateTodo(@RequestParam int id,ModelMap model) {
		Todo todo=service.retrieveTodo(id);
		model.put("todo", todo);
	    return "todo";
 } 
	@RequestMapping(value="/update-todo",method=RequestMethod.POST)
    public String updateTodo(ModelMap model,@Valid Todo todo,BindingResult result ) {
		
		if(result.hasErrors()) {
			return "todo";
		}
		todo.setUser(getLoggedinUserName(model));
		service.updateTodo(todo); 
		 return "redirect:/list-todos";
 } 	
 
	@RequestMapping(value="/add-todo",method=RequestMethod.POST)
  public String addTodo(ModelMap model,@Valid Todo todo,BindingResult result ) {
		if(result.hasErrors()) {
			return "todo";
		}
		service.addTodo(getLoggedinUserName(model),todo.getDesc(),todo.getTargetDate(), false);
	    return "redirect:/list-todos";
}

	
}
