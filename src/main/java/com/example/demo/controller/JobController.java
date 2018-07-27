package com.example.demo.controller;

import com.example.demo.service.CityKladrService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JobController {

    private CityKladrService cityKladrService;

    public JobController(CityKladrService cityKladrService) {
        this.cityKladrService = cityKladrService;
    }

    @RequestMapping("/work")
    public void doJob(){
        System.out.println("start job");
        cityKladrService.makeKladrCalls();
    }
}
