package org.xander.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.xander.model.Car;
import org.xander.service.CarService;

@Controller
public class CarController {
    @Autowired
    CarService carService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView initialPage() {
        ModelAndView modelAndView = new ModelAndView("all");

        modelAndView.addObject("cars", carService.getAll());

        return modelAndView;
    }

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public ModelAndView showAddForm() {
        return new ModelAndView("add_form", "car", new Car());
    }
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addContact(@ModelAttribute("car") Car car) {
        if(car.getId() == null) carService.add(car);
        else carService.update(car);

        return "redirect:/";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public ModelAndView showEditForm(@RequestParam(required = true) String id) {
        return new ModelAndView("add_form", "car", carService.get(id));
    }

    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteContact(@RequestParam(required = true) String id) {
        carService.remove(id);

        return "redirect:/";
    }
}
