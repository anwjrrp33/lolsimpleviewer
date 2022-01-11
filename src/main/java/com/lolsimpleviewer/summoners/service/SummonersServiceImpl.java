package com.lolsimpleviewer.summoners.service;

import com.lolsimpleviewer.summoners.entity.Summoners;
import com.lolsimpleviewer.summoners.repository.SummonersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SummonersServiceImpl implements SummonersService {

    @Value("${API.key}")
    private String key;

    private final SummonersRepository summonersRepository;

    @Override
    public Summoners getDetail(String name) {

        Summoners summoners = summonersRepository.findByName(name);

        if(summoners == null) {
            RestTemplate restTemplate = new RestTemplate();

            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(
                    "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/")
                .path(name.replaceAll(" ", "%20"))
                .queryParam("api_key", key);

            summoners = restTemplate.getForObject(builder.toUriString(), Summoners.class);
            summonersRepository.insert(summoners);
        }

        return summoners;
    }
}
