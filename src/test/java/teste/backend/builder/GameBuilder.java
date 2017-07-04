package teste.backend.builder;

import java.util.Set;

import teste.backend.entities.Game;
import teste.backend.entities.Player;

public class GameBuilder {

	public static final String DEFAULT_ID = "00x1";
	public static final Integer DEFAULT_TOTAL_KILLS = 1;
	public static final Integer DEFAULT_NUMBER = 1;

	private String id;

	private Integer number;

	private Integer totalKills;

	private Set<Player> players;

	public static GameBuilder aGame() {
		return new GameBuilder();
	}

	public GameBuilder withId(String id) {
		this.id = id;
		return this;
	}

	public GameBuilder withNumber(Integer number) {
		this.number = number;
		return this;
	}

	public GameBuilder withKillsCount(Integer totalKills) {
		this.totalKills = totalKills;
		return this;
	}

	public GameBuilder withPlayers(Set<Player> players) {
		this.players = players;
		return this;
	}

	public Game build() {
		Game player = new Game(this.number, this.totalKills, this.players);
		return player;
	}

	public static Game buildDefaultGame() {
		return new Game(DEFAULT_NUMBER, DEFAULT_TOTAL_KILLS, null);
	}

}
