package com.lolsimpleviewer.summoners.service;

import com.lolsimpleviewer.summoners.dto.SummonersDTO;
import com.lolsimpleviewer.summoners.entity.Summoners;

public interface SummonersService {

    Summoners getDetail(String name);

//    default Summoners dtoToEntity(SummonersDTO dto) {
//        return Summoners.builder()
//            .id(dto.getId())
//            .accountId(dto.getAccountId())
//            .puuid(dto.getPuuid())
//            .name(dto.getName())
//            .profileIconId(dto.getProfileIconId())
//            .revisionDate(dto.getRevisionDate())
//            .summonerLevel(dto.getSummonerLevel()).build();
//    }
//
//    default SummonersDTO entityToDto(Summoners entity) {
//        return SummonersDTO.builder()
//            .id(entity.getId())
//            .accountId(entity.getAccountId())
//            .puuid(entity.getPuuid())
//            .name(entity.getName())
//            .profileIconId(entity.getProfileIconId())
//            .revisionDate(entity.getRevisionDate())
//            .summonerLevel(entity.getSummonerLevel()).build();
//    }
}
