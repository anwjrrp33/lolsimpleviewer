package com.lolsimpleviewer.summoners.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsimpleviewer.league.entity.League;
import com.lolsimpleviewer.summoners.dto.SummonersDTO;
import com.lolsimpleviewer.summoners.entity.Summoners;
import com.lolsimpleviewer.summoners.repository.SummonersRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.methods.HttpPost;
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        ArrayList leagueArr = restTemplate.getForObject(builder.toUri(), ArrayList.class);
        List<League> leagueList = new ObjectMapper().convertValue(leagueArr, new TypeReference<List<League>>() {});

        League league = new League();

        for (League l: leagueList) {
            if("RANKED_SOLO_5x5".equals(l.getQueueType())) {
                league = l;
            }
        }

        builder = UriComponentsBuilder.fromHttpUrl("https://ddragon.leagueoflegends.com/realms/na.json").build();

        Map naMap = restTemplate.getForObject(builder.toUri(), HashMap.class);

        SummonersDTO summonersDTO = SummonersDTO.builder()
            .summonerName(summoners.getName())
            .profileIconUrl(naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("profileicon") + "/img/profileicon/" + summoners.getProfileIconId() + ".png")
            .summonerLevel(summoners.getSummonerLevel())
            .queueType(league.getQueueType())
            .tier(league.getTier())
            .tierImgUrl("")
            .rank(league.getRank())
            .leaguePoints(league.getLeaguePoints())
            .wins(league.getWins())
            .losses(league.getLosses())
            .winRatio(league.getWins() == null || league.getLosses() == null ? null : league.getWins() * 100 / (league.getWins() + league.getLosses())).build();

        builder = UriComponentsBuilder.fromHttpUrl("https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/")
                .path(summoners.getPuuid() + "/ids")
                .queryParam("start", 0)
                .queryParam("count", 20)
                .queryParam("api_key", key)
                .build();

        ArrayList matchArr = restTemplate.getForObject(builder.toUri(), ArrayList.class);
        ArrayList matchList = new ArrayList();

        builder = UriComponentsBuilder.fromHttpUrl("https://asia.api.riotgames.com/lol/match/v5/matches/")
                .path(matchArr.get(0).toString())
                .queryParam("api_key", key)
                .build();

        try {
            // ResponseEntity<Object[]> responseEntity = restTemplate.getForEntity(builder.toUri(), Object[].class);
            restTemplate.exchange(builder.toUri(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), String.class).getBody();
        } catch (Exception ex) {
            System.out.println(ex);
        }


        return summonersDTO;
    }
}
