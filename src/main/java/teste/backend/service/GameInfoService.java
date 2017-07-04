package teste.backend.service;

import java.util.List;

import teste.backend.entities.Game;

public interface GameInfoService {
	
	void saveGame(Game game);

	List<Game> retrieveAllGames();
	

}
