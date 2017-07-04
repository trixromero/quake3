package teste.backend.service;

import java.util.List;

import org.springframework.data.domain.Sort.Direction;

import teste.backend.entities.Game;
import teste.backend.entities.Player;

public interface GameInfoService {
	
	void saveGame(Game game);
	
	List<Game> retrieveAllGames();
	
	Game findGameByGameNumber(Integer gameNumber);
	
	List<Game> fingGamesByPlayerName(String playerName);

	List<Game> fingGameByPlayerName(List<String> playerNames);
	
	List<Player> getRanking(Direction orderDirection);

}
