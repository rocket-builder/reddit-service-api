package com.anthill.ofhelperredditmvc.controllers;

import com.anthill.ofhelperredditmvc.domain.Useragent;
import com.anthill.ofhelperredditmvc.interfaces.IRestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Useragent")
@RequestMapping("/useragent")
@RestController
public class UseragentController extends AbstractRestController<Useragent>{
    protected UseragentController(IRestService<Useragent> rest) {
        super(rest);
    }
}
