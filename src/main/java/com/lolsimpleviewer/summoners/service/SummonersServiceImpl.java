package com.lolsimpleviewer.summoners.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolsimpleviewer.league.entity.League;
import com.lolsimpleviewer.match.dto.MatchDTO;
import com.lolsimpleviewer.summoners.dto.SummonersDTO;
import com.lolsimpleviewer.summoners.entity.Summoners;
import com.lolsimpleviewer.summoners.repository.SummonersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

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
                .queryParam("count", 5)
                .queryParam("api_key", key)
                .build();

        ArrayList matchArr = restTemplate.getForObject(builder.toUri(), ArrayList.class);
        ArrayList<JsonNode> matchList = new ArrayList<JsonNode>();
        List<MatchDTO> matchDTOList = new ArrayList<>();

        for(int i = 0; i < matchArr.size(); i++) {
            builder = UriComponentsBuilder.fromHttpUrl("https://asia.api.riotgames.com/lol/match/v5/matches/")
                    .path(matchArr.get(i).toString())
                    .queryParam("api_key", key)
                    .build();

            JsonNode jsonNode = restTemplate.getForEntity(builder.toUri(), JsonNode.class).getBody();
            matchList.add(jsonNode);

            // System.out.println(jsonNode.get("info").findValues("participants").get(0));

            MatchDTO matchDTO = MatchDTO.builder()
                    .matchId(matchArr.get(i).toString())
                    .itemImgUrls(new String[] {
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + jsonNode.get("info").get("participants").get("item0") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + jsonNode.get("info").get("participants").get("item1") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + jsonNode.get("info").get("participants").get("item2") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + jsonNode.get("info").get("participants").get("item3") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + jsonNode.get("info").get("participants").get("item4") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + jsonNode.get("info").get("participants").get("item5") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + jsonNode.get("info").get("participants").get("item6") + ".png"
                    })
                    //.victory(Boolean.parseBoolean(jsonNode.get("info").get("participants").get("win").toString()))
                    //.kills(jsonNode.get("info").get("participants").get("kills").longValue())
                    .build();
        }

        return summonersDTO;
    }
}
