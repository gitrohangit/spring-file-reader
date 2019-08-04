package com.springboot.file.springfiles.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.springboot.file.springfiles.model.User;
import com.springboot.file.springfiles.service.SpringReadFileService;

@Controller
public class SpringReadFileController {

	@Autowired
	private SpringReadFileService springReadFileService;

	// container for the view
	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("user", new User());
		List<User> users = springReadFileService.findAll();
		model.addAttribute("users", users);
		return "view/users"; // return to template users.html
	}

	@PostMapping(value = "/fileupload")
	public String uploadFile(@ModelAttribute User user, RedirectAttributes redirectAttributes) {
		boolean isFlag = springReadFileService.saveDataFromUploadFile(user.getFile());
		if (isFlag) {
			redirectAttributes.addFlashAttribute("successmessage", "File Upload Successfully");
		} else {
			redirectAttributes.addFlashAttribute("errormessage", "File Upload Not Done, Please Try Again");
		}
		return "redirect:/";
	}

}
