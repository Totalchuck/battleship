package com.codeoftheweb.salvo;


import org.springframework.data.rest.core.annotation.RepositoryRestResource;



import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@RepositoryRestResource
public interface ScoreRepository extends JpaRepository<Score, Long> {
}
