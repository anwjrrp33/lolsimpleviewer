package com.lolsimpleviewer.summoners.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(collection = "Summoners")
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Summoners {
    @Id
    private String id;
    private String accountId;
    private String puuid;
    private String name;
    private Long profileIconId;
    private Long revisionDate;
    private Long summonerLevel;
}
