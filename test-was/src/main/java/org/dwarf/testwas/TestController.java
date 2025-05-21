package org.dwarf.testwas;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {


    @GetMapping("/test")
    public String test(@RequestParam boolean isError) {
        if (isError) {
            throw new IllegalArgumentException("에러 테스트");
        }
        return "test";
    }

}
