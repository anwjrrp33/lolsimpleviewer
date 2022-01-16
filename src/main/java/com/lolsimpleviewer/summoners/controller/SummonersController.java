package com.lolsimpleviewer.summoners.controller;

import com.lolsimpleviewer.summoners.dto.SummonersDTO;
import com.lolsimpleviewer.summoners.entity.Summoners;
import com.lolsimpleviewer.summoners.service.SummonersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/summoners")
@Log4j2
@RequiredArgsConstructor
public class SummonersController {

    private final SummonersService summonersService;

    @GetMapping(value = "/name/{name}")
    public ResponseEntity<SummonersDTO> name(@PathVariable(value = "name") String name) throws Exception {
        return new ResponseEntity<>(summonersService.getDetail(name), HttpStatus.OK);
    }
}
