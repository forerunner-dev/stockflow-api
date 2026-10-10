package br.com.stockflow.controllers;


import br.com.stockflow.model.Greeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GeetingController {


    private static final String template ="Ola, %s!";
    private final AtomicLong counter = new AtomicLong();


    // http://localhost:8080/saudacao?name=
    @RequestMapping("/saudacao")
    public Greeting greeting (@RequestParam(value="name" , defaultValue = "Mundo")
                                  String name){

        return new  Greeting(counter.incrementAndGet(), String.format(template, name));
    }

}
