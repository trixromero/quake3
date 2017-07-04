package teste.backend.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import teste.backend.entities.Game;

@Repository
public interface GameRepository extends MongoRepository<Game, Integer>{
	
}
