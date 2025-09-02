package ua.hudyma.searchservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.searchservice.service.AdminSearchService;

@RestController
@RequiredArgsConstructor
@Log4j2
public class AdminSearchController {
    private final AdminSearchService adminSearchService;
}
