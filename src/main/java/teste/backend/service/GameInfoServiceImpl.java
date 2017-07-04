package teste.backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import teste.backend.entities.Game;
import teste.backend.repository.GameRepository;

@Service
public class GameInfoServiceImpl implements GameInfoService {

	@Autowired
	private GameRepository gameRepository;

	@Override
	public void saveGame(Game game) {
		gameRepository.save(game);
	}

	@Override
	public List<Game> retrieveAllGames() {
		return gameRepository.findAll();
	}

	

}
