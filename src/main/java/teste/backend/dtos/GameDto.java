package teste.backend.dtos;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort.Direction;

import teste.backend.entities.Game;
import teste.backend.entities.Player;

public class GameDto {

	private Integer totalKills;

	private List<String> players;

	private Map<String, Integer> kills;

	public Integer getTotalKills() {
		return totalKills;
	}

	public void setTotalKills(Integer totalKills) {
		this.totalKills = totalKills;
	}

	public List<String> getPlayers() {
		return players;
	}

	public void setPlayers(List<String> players) {
		this.players = players;
	}

	public Map<String, Integer> getKills() {
		return kills;
	}

	public void setKills(Map<String, Integer> kills) {
		this.kills = kills;
	}

	public static GameDto toDto(Game game, Direction order) {
		GameDto dto = new GameDto();
		dto.setTotalKills(game.getTotalKills());

		Comparator<Player> byKillCount = getPlayerComparator(order);
		List<Player> sortedPlayers = new ArrayList<>(game.getPlayers());
		sortedPlayers.sort(byKillCount);

		dto.setPlayers(sortedPlayers.stream().map(p -> p.getName()).collect(Collectors.toList()));
		dto.kills = new LinkedHashMap<>();

		for (Player p : sortedPlayers) {
			dto.kills.put(p.getName(), p.getKillsCount());
		}
		return dto;
	}

	private static Comparator<Player> getPlayerComparator(Direction order) {
		if (Direction.ASC.equals(order)) {
			return (p1, p2) -> p1.getKillsCount().compareTo(p2.getKillsCount());
		} else {
			return (p1, p2) -> p2.getKillsCount().compareTo(p1.getKillsCount());
		}
	}
}
