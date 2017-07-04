package teste.backend.entities;

import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.Id;

public class Game {

	@Id
	private String id;

	private Integer number;

	private Integer totalKills;

	private Set<Player> players;

	public Game() {

	}

	public Game(Integer number, Integer totalKills, Set<Player> players) {
		this.number = number;
		this.totalKills = totalKills;
		this.players = players;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getTotalKills() {
		if (totalKills == null) {
			totalKills = 0;
		}
		return totalKills;
	}

	public void setTotalKills(Integer totalKills) {
		this.totalKills = totalKills;
	}

	public Set<Player> getPlayers() {
		if (players == null) {
			players = new HashSet<Player>();
		}
		return players;
	}

	public void setPlayers(Set<Player> players) {
		this.players = players;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

}
