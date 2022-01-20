package com.lolsimpleviewer.match.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class MatchDTO {
    private String matchId;
    private String[] itemImgUrls;
    private String wardImgUrl;
    private List<Map<String, String>> participants;
    private Boolean victory;
    private Long kills;
    private Long deaths;
    private Long assists;
    private Long gameDuration;
    private String summonerCast1ImgUrl;
    private String summonerCast2ImgUrl;
    private String mainSpellImgUrl;
    private String subSpellImgUrl;
    private Long goldEarned;
    private Long champLevel;
    private String championPortraitImgUrl;
}
