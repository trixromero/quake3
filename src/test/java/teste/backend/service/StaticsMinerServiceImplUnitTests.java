package teste.backend.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import teste.backend.entities.Game;
import teste.backend.entities.Player;



@RunWith(SpringRunner.class)
@SpringBootTest
public class StaticsMinerServiceImplUnitTests {

	@Autowired
	private StaticsMinerServiceImpl staticsMinerService;

	@Test
	public void shouldExtracInfoAndInitANewGame() {
		staticsMinerService.extractInfo(
				"20:37 InitGame: \\sv_floodProtect\\1\\sv_maxPing\\0\\sv_minPing\\0\\sv_maxRate\\10000\\sv");
		assertThat(staticsMinerService.getCurrentGame().getTotalKills()).isEqualTo(0);
	}
	
	@Test
	public void shouldGetCorrectNamesFromLineWithWorld(){
		staticsMinerService.setCurrentGame(new Game());
		staticsMinerService.extractKillInfo("20:54 Kill: 1022 2 22: <world> killed Isgalamido by MOD_TRIGGER_HURT");
		assertThat(staticsMinerService.getCurrentGame().getPlayers().size()).isEqualTo(1);
		List<Player> playersList = staticsMinerService.getCurrentGame().getPlayers().stream().collect(Collectors.toList());
		assertThat(playersList.get(0).getName()).isEqualTo("Isgalamido");
	}
	
	@Test
	public void shouldGetCorrectNamesFromLine(){
		staticsMinerService.setCurrentGame(new Game());
		staticsMinerService.extractKillInfo("20:54 Kill: 1022 2 22: Mocinhaa killed Isgalamido by MOD_TRIGGER_HURT");
		assertThat(staticsMinerService.getCurrentGame().getPlayers().size()).isEqualTo(2);
		List<Player> playersList = staticsMinerService.getCurrentGame().getPlayers().stream().collect(Collectors.toList());
		playersList.sort((p1, p2) -> p1.getKillsCount().compareTo(p2.getKillsCount()));
		assertThat(playersList.get(0).getName()).isEqualTo("Isgalamido");
		assertThat(playersList.get(1).getName()).isEqualTo("Mocinhaa");
	}
	
	@Test
	public void shouldCalculateScoreCorrectly(){
		staticsMinerService.setCurrentGame(new Game());
		staticsMinerService.extractKillInfo("20:54 Kill: 1022 2 22: <world> killed Isgalamido by MOD_TRIGGER_HURT");
		assertThat(staticsMinerService.getCurrentGame().getPlayers().size()).isEqualTo(1);
		List<Player> playersList = staticsMinerService.getCurrentGame().getPlayers().stream().collect(Collectors.toList());
		assertThat(playersList.get(0).getKillsCount()).isEqualTo(-1);
	}
	
	@Test
	public void shouldCalculateScoreCorrectlyinSuicideCase(){
		staticsMinerService.setCurrentGame(new Game());
		staticsMinerService.extractKillInfo("20:54 Kill: 1022 2 22: Isgalamido killed Isgalamido by MOD_TRIGGER_HURT");
		List<Player> playersList = staticsMinerService.getCurrentGame().getPlayers().stream().collect(Collectors.toList());
		assertThat(playersList.get(0).getKillsCount()).isEqualTo(0);
	}
	
	

}
