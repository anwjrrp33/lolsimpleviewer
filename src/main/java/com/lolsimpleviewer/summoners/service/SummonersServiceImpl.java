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
import org.bson.json.JsonObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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

        // Spell
        builder = UriComponentsBuilder.fromHttpUrl("https://ddragon.leagueoflegends.com/cdn/" + ((Map) naMap.get("n")).get("summoner") + "/data/ko_KR/summoner.json").build();
        JsonNode summonerJson = restTemplate.getForEntity(builder.toUri(), JsonNode.class).getBody();
        List<JsonNode> spells = new ArrayList<>();
        summonerJson.get("data").elements().forEachRemaining(spells::add);
        // Rune
        builder = UriComponentsBuilder.fromHttpUrl("https://ddragon.leagueoflegends.com/cdn/" + naMap.get("v") + "/data/ko_KR/runesReforged.json").build();
        JsonNode runeJson = restTemplate.getForEntity(builder.toUri(), JsonNode.class).getBody();
        List<JsonNode> runes = new ArrayList<>();
        runeJson.elements().forEachRemaining(runes::add);

        List<JsonNode> slots = new ArrayList<>();
        for (JsonNode j: runes) {
            j.get("slots").get(0).get("runes").elements().forEachRemaining(slots::add);
        }

        for(int i = 0; i < matchArr.size(); i++) {
            builder = UriComponentsBuilder.fromHttpUrl("https://asia.api.riotgames.com/lol/match/v5/matches/")
                    .path(matchArr.get(i).toString())
                    .queryParam("api_key", key)
                    .build();

            JsonNode jsonNode = restTemplate.getForEntity(builder.toUri(), JsonNode.class).getBody();
            matchList.add(jsonNode);

            List<JsonNode> participants = new ObjectMapper().convertValue(jsonNode.get("info").get("participants"), new TypeReference<List<JsonNode>>() {});
            String summonersName = summoners.getName();
            JsonNode value = participants.stream()
                    .filter(participant -> summonersName.equals(participant.get("summonerName").toString().replaceAll("\"", "")))
                    .findAny()
                    .orElse(null);

            String summoner1Id = value.get("summoner1Id").toString();
            String summoner2Id = value.get("summoner2Id").toString();
            JsonNode spell1 = spells.stream()
                    .filter(spell -> summoner1Id.equals(spell.get("key").toString().replaceAll("\"", "")))
                    .findAny()
                    .orElse(null);
            JsonNode spell2 = spells.stream()
                    .filter(spell -> summoner2Id.equals(spell.get("key").toString().replaceAll("\"", "")))
                    .findAny()
                    .orElse(null);

            String rune1Id = value.get("perks").get("styles").get(0).get("selections").get(0).get("perk").toString();
            String rune2Id = value.get("perks").get("styles").get(1).get("style").toString();

            JsonNode rune1 = slots.stream()
                    .filter(rune -> rune1Id.equals(rune.get("id").toString().replaceAll("\"", "")))
                    .findAny()
                    .orElse(null);
            JsonNode rune2 = runes.stream()
                    .filter(rune -> rune2Id.equals(rune.get("id").toString().replaceAll("\"", "")))
                    .findAny()
                    .orElse(null);

            MatchDTO matchDTO = MatchDTO.builder()
                    .matchId(matchArr.get(i).toString())
                    .itemImgUrls(new String[] {
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + value.get("item0") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + value.get("item1") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + value.get("item2") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + value.get("item3") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + value.get("item4") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + value.get("item5") + ".png",
                            naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("item") + "/img/item/" + value.get("item6") + ".png"
                    })
                    .victory(Boolean.parseBoolean(value.get("win").toString()))
                    .kills(value.get("kills").longValue())
                    .assists(value.get("assists").longValue())
                    .gameDuration(jsonNode.get("info").get("gameDuration").longValue())
                    .summonerCast1ImgUrl(naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("summoner") + "/img/spell/" + spell1.get("image").get("full").toString().replaceAll("\"", ""))
                    .summonerCast2ImgUrl(naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("summoner") + "/img/spell/" + spell2.get("image").get("full").toString().replaceAll("\"", ""))
                    .mainSpellImgUrl(naMap.get("cdn") + "/img/" + rune1.get("icon").toString().replaceAll("\"", ""))
                    .subSpellImgUrl(naMap.get("cdn") + "/img/" + rune2.get("icon").toString().replaceAll("\"", ""))
                    .goldEarned(value.get("goldEarned").longValue())
                    .champLevel(value.get("champLevel").longValue())
                    .championPortraitImgUrl(naMap.get("cdn") + "/" + ((Map) naMap.get("n")).get("champion") + "/img/champion/" + value.get("championName").toString().replaceAll("\"", "") + ".png")
                    .build();
        }

        return summonersDTO;
    }
}
