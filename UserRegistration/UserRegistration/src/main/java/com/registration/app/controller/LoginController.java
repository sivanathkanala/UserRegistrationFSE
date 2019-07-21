package com.registration.app.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.registration.app.exception.ServiceException;
import com.registration.app.model.User;
import com.registration.app.service.UserService;

@Controller
public class LoginController {

	private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

	@Autowired
	private UserService userService;

	@RequestMapping(value="/login", method = RequestMethod.GET)
	public ModelAndView login( HttpSession session) throws Exception{
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@RequestMapping(value="/", method = RequestMethod.GET)
	public ModelAndView index() throws Exception{
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("login");
		return modelAndView;
	}

	@RequestMapping(value="/admin/accountupdate", method = RequestMethod.GET)
	public ModelAndView accountUpdate(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("user", user);
		modelAndView.setViewName("admin/accountupdate");
		logger.info("Loading the account Update page...");
		return modelAndView;
	}

	@RequestMapping(value="/registration", method = RequestMethod.GET)
	public ModelAndView registration(HttpSession session){
		ModelAndView modelAndView = new ModelAndView();
		User user = new User();
		modelAndView.addObject("user", user);
		modelAndView.setViewName("registration");
		return modelAndView;
	}

	@RequestMapping(value = "/admin/update", method = RequestMethod.POST)
	public ModelAndView updateUser(@Valid User user, BindingResult bindingResult,HttpSession session) throws Exception {
		ModelAndView modelAndView = new ModelAndView();

		String captcha = (String) session.getAttribute("captcha");

		if(null!=captcha) {
			if(!(user.getCaptcha().equals(captcha))){
				bindingResult.rejectValue("captcha", "error.captcha","Invalid captcha.");
			}
		}  

		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("admin/accountupdate");
		} else {
			try {
				userService.saveUser(user);
			}catch (ServiceException e) {

			}
			modelAndView.addObject("successMessage", "User has been updated successfully");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("admin/home");
			logger.info("User updated successfully to DB.");
		}
		return modelAndView;
	}

	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	public ModelAndView createNewUser(@Valid User user, BindingResult bindingResult,HttpSession session) throws Exception {
		ModelAndView modelAndView = new ModelAndView();
		User userExists = userService.findUserByEmail(user.getEmail());
		if (userExists != null) {
			bindingResult.rejectValue("email", "error.user","There is already a user registered with the email provided");
		}

		String captcha = (String) session.getAttribute("captcha");
		if(null!=captcha) {
			if(!(user.getCaptcha().equals(captcha))){
				bindingResult.rejectValue("captcha", "error.captcha","Invalid captcha.");
			}
		}  

		if (bindingResult.hasErrors()) {
			modelAndView.setViewName("registration");
		} else {
			try {
				userService.saveUser(user);
			}catch (ServiceException e) {
				return modelAndView.addObject("errorMessage", "Error while inserting record.");
			}
			modelAndView.addObject("successMessage", "User has been registered successfully");
			modelAndView.addObject("user", new User());
			modelAndView.setViewName("registration");
			logger.info("User register successfully to DB.");
		}
		return modelAndView;
	}

	@RequestMapping(value="/admin/home", method = RequestMethod.GET)
	public ModelAndView home(){
		ModelAndView modelAndView = new ModelAndView();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		modelAndView.addObject("userName", "Welcome " + user.getName() + " " + user.getLastName()+ " (" + user.getEmail() + ")");
		modelAndView.addObject("adminMessage","Content Available Only for Users with Admin Role");

		logger.info("User is redirect to the homepage.");
		modelAndView.setViewName("admin/home");
		return modelAndView;
	}

	/*@RequestMapping(value="/captcha", method = RequestMethod.GET)
    public ModelAndView captcha() throws Exception{
    	ModelAndView modelAndView = new ModelAndView();
    	captchImage.processRequest();
    	 return modelAndView;
    }*/

	/*@RequestMapping("/default")
	public String defaultAfterLogin(HttpServletRequest request) {
		System.out.println(request.g);
		if (request.isUserInRole("ROLE_ADMIN")) {
			return "redirect:/admin/home";
		}else if(request.isUserInRole("SUPER_ADMIN")) {
			return "redirect:/superadmin/home";
		}
		return "redirect:/user/";
	}*/
}
