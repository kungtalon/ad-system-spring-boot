package org.talon.ad.controller;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.talon.ad.exception.AdException;
import org.talon.ad.service.ICreativeService;
import org.talon.ad.vo.CreativeRequest;
import org.talon.ad.vo.CreativeResponse;

/**
 * Created by Zelong
 * On 2022/5/2
 **/
@Slf4j
@RestController
public class CreativeController {

    private final ICreativeService creativeService;
    private final Gson gson;

    @Autowired
    public CreativeController(ICreativeService creativeService) {
        this.creativeService = creativeService;
        this.gson = new Gson();
    }

    @PostMapping("/create/creative")
    public CreativeResponse createCreative(
            @RequestBody CreativeRequest request
            ) throws AdException {
        log.info("ad-sponsor: createCreative -> {}", gson.toJson(request));
        return creativeService.createCreative(request);
    }
}
