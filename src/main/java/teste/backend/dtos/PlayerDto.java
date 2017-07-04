package teste.backend.dtos;

import teste.backend.entities.Player;

public class PlayerDto {

	private String playerName;

	private Integer kills;

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Integer getKills() {
		return kills;
	}

	public void setKills(Integer kills) {
		this.kills = kills;
	}
	
	public static PlayerDto toDto(Player player){
		PlayerDto dto = new PlayerDto();
		dto.setPlayerName(player.getName());
		dto.setKills(player.getKillsCount());
		return dto;
	}
}
