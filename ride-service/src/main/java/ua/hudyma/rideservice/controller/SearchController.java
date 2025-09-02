package ua.hudyma.rideservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.rideservice.service.SearchService;

@RestController
@RequiredArgsConstructor
@Log4j2
public class SearchController {
    private final SearchService searchService;

    public String findString (@RequestParam String str){
        return searchService.findStr (str);

    }
}
