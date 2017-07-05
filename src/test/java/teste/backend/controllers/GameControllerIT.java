package teste.backend.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;

import teste.backend.Application;
import teste.backend.dtos.GameDto;
import teste.backend.dtos.MessageDto;
import teste.backend.dtos.PlayerDto;
import teste.backend.service.StaticsMinerServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GameControllerIT {

	TestRestTemplate restTemplate = new TestRestTemplate();

	@Autowired
	StaticsMinerServiceImpl service;

	@LocalServerPort
	private int port;

	CountDownLatch countDownLatch;

	static boolean firstInsert = true;

	@Before
	public void setUp() throws IOException {

		if (firstInsert) {
			countDownLatch = new CountDownLatch(1);
			service.setCountDownLatch(countDownLatch);
			LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
			map.add("file", new FileSystemResource(new ClassPathResource("games.log").getFile()));
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			HttpEntity<LinkedMultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);

			ResponseEntity<?> x = restTemplate.exchange("http://localhost:" + port + "/games/uploadFile",
					HttpMethod.POST, requestEntity, MessageDto.class);
		}
		firstInsert = false;

	}

	@Test
	public void integrationTestAShouldGetSecondGame() throws IOException, InterruptedException {

		countDownLatch.await();
		ParameterizedTypeReference<Map<String, GameDto>> typeRef = new ParameterizedTypeReference<Map<String, GameDto>>() {
		};
		ResponseEntity<Map<String, GameDto>> response = this.restTemplate
				.exchange("http://localhost:" + port + "/games/2", HttpMethod.GET, null, typeRef);
		Map<String, GameDto> gameDto = response.getBody();

		assertThat(gameDto.get("game_2").getTotalKills()).isEqualTo(11);
		assertThat(gameDto.get("game_2").getPlayers().get(0)).isEqualTo("Mocinha");
		assertThat(gameDto.get("game_2").getKills().keySet().size()).isEqualTo(2);

	}

	@Test
	public void integrationTestBShouldGetAllGames() throws InterruptedException {

		ParameterizedTypeReference<Map<String, GameDto>> typeRef = new ParameterizedTypeReference<Map<String, GameDto>>() {
		};
		ResponseEntity<Map<String, GameDto>> response = this.restTemplate
				.exchange("http://localhost:" + port + "/games/", HttpMethod.GET, null, typeRef);
		Map<String, GameDto> gamesDto = response.getBody();

		assertThat(gamesDto.size()).isEqualTo(21);
		assertThat(gamesDto.get("game_1").getTotalKills()).isEqualTo(0);
		assertThat(gamesDto.get("game_8").getPlayers().get(1)).isEqualTo("Oootsimo");

	}

	@Test
	public void integrationTestCShouldGetAllGamesByName() throws InterruptedException {

		ParameterizedTypeReference<Map<String, GameDto>> typeRef = new ParameterizedTypeReference<Map<String, GameDto>>() {
		};
		ResponseEntity<Map<String, GameDto>> response = this.restTemplate
				.exchange("http://localhost:" + port + "/games?playerName=Mal", HttpMethod.GET, null, typeRef);
		Map<String, GameDto> gamesDto = response.getBody();

		assertThat(gamesDto.size()).isEqualTo(12);
		assertThat(gamesDto.get("game_21").getTotalKills()).isEqualTo(131);
		assertThat(gamesDto.get("game_1")).isNull();

	}

	@Test
	public void integrationTestDShouldGetAllGamesWithParameterButNoName() throws InterruptedException {

		ParameterizedTypeReference<Map<String, GameDto>> typeRef = new ParameterizedTypeReference<Map<String, GameDto>>() {
		};
		ResponseEntity<Map<String, GameDto>> response = this.restTemplate
				.exchange("http://localhost:" + port + "/games?playerName=", HttpMethod.GET, null, typeRef);
		Map<String, GameDto> gamesDto = response.getBody();

		assertThat(gamesDto.size()).isEqualTo(21);
		assertThat(gamesDto.get("game_1").getTotalKills()).isEqualTo(0);
		assertThat(gamesDto.get("game_8").getPlayers().get(1)).isEqualTo("Oootsimo");

	}

	@Test
	public void integrationTestEShoulGetRanking() {
		ParameterizedTypeReference<ArrayList<PlayerDto>> typeRef = new ParameterizedTypeReference<ArrayList<PlayerDto>>() {
		};
		ResponseEntity<ArrayList<PlayerDto>> response = this.restTemplate
				.exchange("http://localhost:" + port + "/games/ranking", HttpMethod.GET, null, typeRef);
		List<PlayerDto> playersDto = response.getBody();

		assertThat(playersDto.size()).isEqualTo(10);
		assertThat(playersDto.get(1).getPlayerName()).isEqualTo("Zeh");
		assertThat(playersDto.get(0).getKills()).isEqualTo(138);
	}
}