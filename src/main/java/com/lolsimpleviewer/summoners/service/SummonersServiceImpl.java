package com.lolsimpleviewer.summoners.service;

import com.lolsimpleviewer.summoners.entity.Summoners;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SummonersServiceImpl implements SummonersService {

    @Value("${API.key}")
    private String key;

    @Override
    public Summoners getDetail(String name) {

        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/")
                .path(name.replaceAll(" ", "%20"))
                .queryParam("api_key", key);

        // 추후 DTO로 변환
        // restTemplate.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

        return restTemplate.getForObject(builder.toUriString(), Summoners.class);
    }
}
