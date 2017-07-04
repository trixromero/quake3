package teste.backend.service;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import teste.backend.entities.Game;
import teste.backend.entities.Player;
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

	@Override
	public Game findGameByGameNumber(Integer gameNumber) {
		return gameRepository.findByNumber(gameNumber);
	}

	@Override
	public List<Game> fingGamesByPlayerName(String playerName) {
		return gameRepository.findByPlayersName(playerName);
	}

	@Override
	public List<Game> fingGameByPlayerName(List<String> playerNames) {
		return gameRepository.findByPlayersNameIn(playerNames);
	}

	@Override
	public List<Player> getRanking(Direction orderDirection) {
		List<Game> allGames = gameRepository.findAll();

		HashSet<Player> players = new HashSet<>();
		List<Player> playersRanking = new LinkedList<>(players);

		for (Game game : allGames) {
			for (Player player : game.getPlayers()) {
				if (playersRanking.contains(player)) {
					Player playerAux = playersRanking.get(getPlayerIndex(playersRanking, player));
					playerAux.setKillsCount(playerAux.getKillsCount() + player.getKillsCount());
				} else {
					playersRanking.add(player);
				}
			}
		}

		if (orderDirection.equals(Direction.ASC)) {
			playersRanking.sort((p1, p2) -> p1.getKillsCount().compareTo(p2.getKillsCount()));
		} else {
			playersRanking.sort((p1, p2) -> p2.getKillsCount().compareTo(p1.getKillsCount()));
		}

		return playersRanking;
	}

	private int getPlayerIndex(List<Player> playersRanking, Player player) {
		int index = 0;
		for (Player p : playersRanking) {
			if (p.equals(player)) {
				return index;
			}
			index++;
		}
		return -1;
	}
	
	public void setGameRepository(GameRepository gameRepository) {
		this.gameRepository = gameRepository;
	}

}
