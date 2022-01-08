package com.lolsimpleviewer.summoners.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lolsimpleviewer.summoners.entity.Summoners;

public interface SummonersRepository extends JpaRepository<Summoners, Long> {

}
