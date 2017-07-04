package teste.backend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import teste.backend.builder.PlayerBuilder;
import teste.backend.entities.Game;
import teste.backend.entities.Player;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameRepositoryMongoTests {

	@Autowired
	GameRepository gameRepository;

	@Before
	public void setUp() {
		gameRepository.deleteAll();
		Game g = new Game();
		List<Player> players = Arrays.asList(PlayerBuilder.aPlayer().withName("Joao").withKillsCount(100).build(),
				PlayerBuilder.aPlayer().withName("Maria").withKillsCount(50).build(),
				PlayerBuilder.aPlayer().withName("Teodoro").withKillsCount(0).build(),
				PlayerBuilder.aPlayer().withName("Joaquina").withKillsCount(-2).build());
		g.setPlayers(new HashSet<Player>(players));
		g.setNumber(1);
		gameRepository.save(g);
		List<Player> players2 = Arrays.asList(PlayerBuilder.aPlayer().withName("Joao").withKillsCount(100).build(),
				PlayerBuilder.aPlayer().withName("Maria").withKillsCount(50).build(),
				PlayerBuilder.aPlayer().withName("Teodoro").withKillsCount(0).build(),
				PlayerBuilder.aPlayer().withName("Joaquina").withKillsCount(-2).build());
		Game g2 = new Game();
		g2.setPlayers(new HashSet<Player>(players2));
		g2.setNumber(2);
		gameRepository.save(g2);
	}

	@Test
	public void shouldFindGameByNumber() {
		Game game = gameRepository.findByNumber(2);
		assertThat(game.getNumber()).isEqualTo(2);
		assertThat(game.getPlayers().size()).isEqualTo(4);
	}

	@Test
	public void shouldNotFindGameByPlayer() {
		List<Game> game = gameRepository.findByPlayersName("Teodar");
		assertThat(game.isEmpty()).isEqualTo(true);
	}

	@Test
	public void shouldFindGameByPlayer() {
		List<Game> game = gameRepository.findByPlayersName("Joaquina");
		assertThat(game.size()).isEqualTo(2);
	}

	@Test
	public void shouldFinGameByPlayerIn() {
		List<String> names = Arrays.asList("Joaquina", "Pedro");
		List<Game> game = gameRepository.findByPlayersNameIn(names);
		assertThat(game.size()).isEqualTo(2);
	}
	
	@Test
	public void shouldNotFinGameByPlayerIn() {
		List<String> names = Arrays.asList("Mariazinha", "Pedro");
		List<Game> game = gameRepository.findByPlayersNameIn(names);
		assertThat(game.isEmpty()).isEqualTo(true);
	}

}
