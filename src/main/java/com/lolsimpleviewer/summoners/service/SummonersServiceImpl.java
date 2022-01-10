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

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SummonersServiceImpl implements SummonersService {

    @Value("${API.key}")
    private String key;

    private final SummonersRepository summonersRepository;

    @Override
    public Summoners getDetail(String name) {

        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/")
                .path(name.replaceAll(" ", "%20"))
                .queryParam("api_key", key);

        // 추후 DTO로 변환
        // restTemplate.exchange(builder.toUriString(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class);

        Summoners summoners = restTemplate.getForObject(builder.toUriString(), Summoners.class);

        //System.out.println(summonersRepository.findAllById(summoners.getId()).toString());

        // summonersRepository.insert(summoners);

        // List<Summoners> list = summonersRepository.findAll();

        // System.out.println(list.toString());

        return summoners;
    }
}
