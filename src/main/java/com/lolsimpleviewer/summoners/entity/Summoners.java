package com.lolsimpleviewer.summoners.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.math.BigInteger;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Summoners {
    private String id;
    private String accountId;
    private String puuid;
    private String name;
    private Long profileIconId;
    private Long revisionDate;
    private Long summonerLevel;
}
