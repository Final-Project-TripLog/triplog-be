package com.ssafy.triplog.common;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
//@RequiredArgsConstructor
class HomeController {
    @Operation(summary = "home", description = "/로 들어오는 get요청 처리")
    @GetMapping("/")
    public String home() {
        log.info("home");
        return "home";
    }
}
