package teste.backend.builder;

import teste.backend.entities.Player;

public class PlayerBuilder {

	public static final String DEFAULT_NAME = "Tito TT";
	public static final Integer DEFAULT_KILLS_COUNT = 99999;

	private String name;
	private Integer killsCount;

	public static PlayerBuilder aPlayer() {
		return new PlayerBuilder();
	}

	public PlayerBuilder withName(String name) {
		this.name = name;
		return this;
	}

	public PlayerBuilder withKillsCount(Integer killsCount) {
		this.killsCount = killsCount;
		return this;
	}

	public Player build() {
		Player player = new Player(this.name, this.killsCount);
		return player;
	}

	public static Player buildDefaultPlayer() {
		return new Player(DEFAULT_NAME, DEFAULT_KILLS_COUNT);
	}

}
