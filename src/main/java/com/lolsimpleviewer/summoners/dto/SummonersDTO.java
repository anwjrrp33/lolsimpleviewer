package com.lolsimpleviewer.summoners.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SummonersDTO {
	private String summonerName;
	private String profileIconUrl;
	private Long summonerLevel;
	private String queueType;
	private String tier;
	private String tierImgUrl;
	private String rank;
	private Long leaguePoints;
	private Long wins;
	private Long losses;
	private Long winRatio;
}
