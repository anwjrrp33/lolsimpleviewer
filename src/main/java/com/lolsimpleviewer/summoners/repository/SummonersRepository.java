package com.lolsimpleviewer.summoners.repository;

import com.lolsimpleviewer.summoners.entity.Summoners;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SummonersRepository extends MongoRepository<Summoners, String> {

    Summoners findAllById(String id);
}
