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
	private String id;
	private String accountId;
	private String puuid;
	private String name;
	private Long profileIconId;
	private Long revisionDate;
	private Long summonerLevel;
}
