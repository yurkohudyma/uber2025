package ua.hudyma.rideservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.rideservice.service.SearchService;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/search")
public class SearchController {
    private final SearchService searchService;

    @GetMapping
    public String findString (@RequestParam String str, @RequestParam String userId){
        return searchService.findStr (str, userId);

    }
}
