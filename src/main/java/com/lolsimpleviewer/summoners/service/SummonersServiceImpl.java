package com.lolsimpleviewer.summoners.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.lolsimpleviewer.league.entity.League;
import com.lolsimpleviewer.summoners.dto.SummonersDTO;
import com.lolsimpleviewer.summoners.entity.Summoners;
import com.lolsimpleviewer.summoners.repository.SummonersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SummonersServiceImpl implements SummonersService {

    @Value("${API.key}")
    private String key;

    private final SummonersRepository summonersRepository;

    @Override
    public SummonersDTO getDetail(String name) {
        Summoners summoners = summonersRepository.findByNameIgnoreCase(name);

        RestTemplate restTemplate = new RestTemplate();

        UriComponents builder;

        if(summoners == null) {
            builder = UriComponentsBuilder.fromHttpUrl("https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/")
                    .path(name)
                    .queryParam("api_key", key)
                    .encode(StandardCharsets.UTF_8)
                    .build();

            summoners = restTemplate.getForObject(builder.toUri(), Summoners.class);
            summonersRepository.insert(summoners);
        }

        builder = UriComponentsBuilder.fromHttpUrl("https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/")
            .path(summoners.getId())
            .queryParam("api_key", key)
            .build();

        List<League> leagueList = restTemplate.getForObject(builder.toUri(), new ArrayList<League>().getClass());

        League league = new League();

        for (League l: leagueList) {
            if("RANKED_SOLO_5x5".equals(league.getQueueType())) {
                league = l;
            }
        }

        SummonersDTO summonersDTO = SummonersDTO.builder()
            .summonerName(summoners.getName())
            .profileIconUrl("")
            .summonerLevel(summoners.getSummonerLevel())
            .queueType("")
            .tier("")
            .tierImgUrl("")
            .rank("")
            .leaguePoints(0L)
            .wins(0L)
            .losses(0L)
            .winRatio(0L).build();

        return summonersDTO;
    }
}
