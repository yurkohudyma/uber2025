package ua.hudyma.searchservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.hudyma.searchservice.service.UserSearchService;

@RestController
@RequiredArgsConstructor
@Log4j2
@RequestMapping("/search")
public class UserSearchController {
    private final UserSearchService userSearchService;

    @GetMapping
    public String findByStringValue (@RequestParam String str,
                                     @RequestParam String userId){
        return userSearchService.findString (str, userId);

    }


}
