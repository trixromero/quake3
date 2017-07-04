package teste.backend.controllers;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.client.RestTemplate;

import teste.backend.builder.GameBuilder;
import teste.backend.builder.PlayerBuilder;
import teste.backend.entities.Game;
import teste.backend.entities.Player;
import teste.backend.service.GameInfoServiceImpl;
import teste.backend.utils.Messages;

@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
@WebAppConfiguration
public class GameControllerUnitTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GameInfoServiceImpl gameInfoServiceMock;

	@MockBean
	private RestTemplate restTemplateMock;

	@MockBean
	private Messages messageMock;

	@MockBean
	private JmsTemplate jmsTemplateMock;

	private final static String BASE_URI = "/games";

	private final static String ORDER_KILL = "playersInnerGameOrderDirectionByKill";

	private final static String PLAYER_NAME_FILTER = "playerName";

	@Test
	public void shouldReturn200FileOk() throws Exception {

		MockMultipartFile file = new MockMultipartFile("file", "test.log", null, "00:00 Init Game".getBytes());

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(BASE_URI + "/uploadFile").file(file);
		this.mockMvc.perform(builder).andExpect(status().is(200));
	}

	@Test
	public void shouldReturn400InvalidExtension() throws Exception {

		MockMultipartFile file = new MockMultipartFile("file", "test.txt", null, "00:00 Init Game".getBytes());

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(BASE_URI + "/uploadFile").file(file);
		this.mockMvc.perform(builder).andExpect(status().is(400));
	}

	@Test
	public void shouldReturn400EmptyFile() throws Exception {

		byte[] b = {};
		MockMultipartFile file = new MockMultipartFile("file", "test.txt", null, b);

		MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.fileUpload(BASE_URI + "/uploadFile").file(file);
		this.mockMvc.perform(builder).andExpect(status().is(400));
	}

	@Test
	public void shouldReturnGameInfoByNumberOk() throws Exception {
		Game game = GameBuilder.aGame().withNumber(2).withKillsCount(0).withPlayers(null).build();

		when(gameInfoServiceMock.findGameByGameNumber(2)).thenReturn(game);
		ResultActions resultAction = mockMvc.perform(get(BASE_URI + "/2")).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

		resultAction.andExpect(status().is(200)).andExpect(jsonPath("$").isNotEmpty())
				.andExpect(jsonPath("$.game_2.totalKills").value("0"));

		verify(gameInfoServiceMock, times(1)).findGameByGameNumber(2);
		verifyNoMoreInteractions(gameInfoServiceMock);
	}

	@Test
	public void shouldReturnGetGameByNumberBadRequest() throws Exception {
		Game game = GameBuilder.aGame().withNumber(2).withKillsCount(0).withPlayers(null).build();

		when(gameInfoServiceMock.findGameByGameNumber(2)).thenReturn(game);
		mockMvc.perform(get(BASE_URI + "/-2")).andExpect(status().is(400))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

		verifyNoMoreInteractions(gameInfoServiceMock);
	}

	@Test
	public void shouldReturnGetGameByNumberWithPlayersInsideGameDescKillOrder() throws Exception {
		Player p1 = PlayerBuilder.aPlayer().withKillsCount(100).withName("Joao").build();
		Player p2 = PlayerBuilder.aPlayer().withKillsCount(49).withName("Maria").build();

		Game game = GameBuilder.aGame().withId("teste").withNumber(2).withKillsCount(149)
				.withPlayers(new HashSet<>(Arrays.asList(p1, p2))).build();

		when(gameInfoServiceMock.findGameByGameNumber(2)).thenReturn(game);
		ResultActions resultAction = mockMvc.perform(get(BASE_URI + "/2?" + ORDER_KILL + "=desc"))
				.andExpect(status().is(200)).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[0]").value("Joao"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[1]").value("Maria"));

		verify(gameInfoServiceMock, times(1)).findGameByGameNumber(2);
		verifyNoMoreInteractions(gameInfoServiceMock);
	}

	@Test
	public void shouldReturnGetGameByNumberWithPlayersInsideGameAscKillOrder() throws Exception {
		Player p1 = PlayerBuilder.aPlayer().withKillsCount(100).withName("Joao").build();
		Player p2 = PlayerBuilder.aPlayer().withKillsCount(49).withName("Maria").build();

		Game game = GameBuilder.aGame().withId("teste").withNumber(2).withKillsCount(149)
				.withPlayers(new HashSet<>(Arrays.asList(p2, p1))).build();

		when(gameInfoServiceMock.findGameByGameNumber(2)).thenReturn(game);
		ResultActions resultAction = mockMvc.perform(get(BASE_URI + "/2?" + ORDER_KILL + "=asc"))
				.andExpect(status().is(200)).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[0]").value("Maria"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[1]").value("Joao"));

		verify(gameInfoServiceMock, times(1)).findGameByGameNumber(2);
		verifyNoMoreInteractions(gameInfoServiceMock);
	}

	@Test
	public void shouldReturnRankingJson() throws Exception {
		Player p1 = PlayerBuilder.aPlayer().withKillsCount(100).withName("Joao").build();
		Direction DESC = Direction.DESC;
		when(gameInfoServiceMock.getRanking(DESC)).thenReturn(Arrays.asList(p1));

		mockMvc.perform(get(BASE_URI + "/ranking")).andExpect(status().is(200))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

		verify(gameInfoServiceMock, times(1)).getRanking(DESC);
		verifyNoMoreInteractions(gameInfoServiceMock);

	}

	@Test
	public void shouldGetAllGamesDesc() throws Exception {
		Player p1 = PlayerBuilder.aPlayer().withKillsCount(100).withName("Joao").build();
		Player p2 = PlayerBuilder.aPlayer().withKillsCount(49).withName("Maria").build();
		Player p3 = PlayerBuilder.aPlayer().withKillsCount(67).withName("Orizeu").build();
		Player p4 = PlayerBuilder.aPlayer().withKillsCount(9999).withName("Xena").build();
		Game g1 = GameBuilder.aGame().withId("teste").withKillsCount(1215).withNumber(1)
				.withPlayers(new HashSet<Player>(Arrays.asList(p1, p2))).build();
		Game g2 = GameBuilder.aGame().withId("teste2").withKillsCount(1215).withNumber(2)
				.withPlayers(new HashSet<Player>(Arrays.asList(p3, p4))).build();

		when(gameInfoServiceMock.retrieveAllGames()).thenReturn(Arrays.asList(g1, g2));
		ResultActions resultAction = mockMvc.perform(get(BASE_URI + "?" + ORDER_KILL + "=desc"))
				.andExpect(status().is(200)).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_1.players[0]").value("Joao"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_1.players[1]").value("Maria"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[0]").value("Xena"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[1]").value("Orizeu"));

		verify(gameInfoServiceMock, times(1)).retrieveAllGames();

		verifyNoMoreInteractions(gameInfoServiceMock);
	}

	@Test
	public void shouldGetAllGamesAsc() throws Exception {
		Player p1 = PlayerBuilder.aPlayer().withKillsCount(100).withName("Joao").build();
		Player p2 = PlayerBuilder.aPlayer().withKillsCount(49).withName("Maria").build();
		Player p3 = PlayerBuilder.aPlayer().withKillsCount(67).withName("Orizeu").build();
		Player p4 = PlayerBuilder.aPlayer().withKillsCount(9999).withName("Xena").build();
		Game g1 = GameBuilder.aGame().withId("teste").withKillsCount(1215).withNumber(1)
				.withPlayers(new HashSet<Player>(Arrays.asList(p1, p2))).build();
		Game g2 = GameBuilder.aGame().withId("teste2").withKillsCount(1215).withNumber(2)
				.withPlayers(new HashSet<Player>(Arrays.asList(p3, p4))).build();

		when(gameInfoServiceMock.retrieveAllGames()).thenReturn(Arrays.asList(g1, g2));
		ResultActions resultAction = mockMvc.perform(get(BASE_URI + "?" + ORDER_KILL + "=asc"))
				.andExpect(status().is(200)).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_1.players[0]").value("Maria"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_1.players[1]").value("Joao"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[0]").value("Orizeu"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[1]").value("Xena"));

		verify(gameInfoServiceMock, times(1)).retrieveAllGames();

		verifyNoMoreInteractions(gameInfoServiceMock);
	}

	@Test
	public void shouldGetAllGamesByNameAsc() throws Exception {
		Player p1 = PlayerBuilder.aPlayer().withKillsCount(100).withName("Joao").build();
		Player p2 = PlayerBuilder.aPlayer().withKillsCount(49).withName("Maria").build();
		Player p3 = PlayerBuilder.aPlayer().withKillsCount(67).withName("Maria").build();
		Player p4 = PlayerBuilder.aPlayer().withKillsCount(9999).withName("Xena").build();
		Game g1 = GameBuilder.aGame().withId("teste").withKillsCount(1215).withNumber(1)
				.withPlayers(new HashSet<Player>(Arrays.asList(p1, p2))).build();
		Game g2 = GameBuilder.aGame().withId("teste2").withKillsCount(1215).withNumber(2)
				.withPlayers(new HashSet<Player>(Arrays.asList(p3, p4))).build();

		when(gameInfoServiceMock.fingGamesByPlayerName("Maria")).thenReturn(Arrays.asList(g1, g2));
		ResultActions resultAction = mockMvc
				.perform(get(BASE_URI + "?" + ORDER_KILL + "=asc&"+PLAYER_NAME_FILTER+"=Maria"))
				.andExpect(status().is(200)).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_1.players[0]").value("Maria"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_1.players[1]").value("Joao"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[0]").value("Maria"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[1]").value("Xena"));

		verify(gameInfoServiceMock, times(1)).fingGamesByPlayerName("Maria");
		verifyNoMoreInteractions(gameInfoServiceMock);
	}

	@Test
	public void shouldGetAllGamesByNameDesc() throws Exception {
		Player p1 = PlayerBuilder.aPlayer().withKillsCount(100).withName("Joao").build();
		Player p2 = PlayerBuilder.aPlayer().withKillsCount(49).withName("Maria").build();
		Player p3 = PlayerBuilder.aPlayer().withKillsCount(67).withName("Maria").build();
		Player p4 = PlayerBuilder.aPlayer().withKillsCount(9999).withName("Xena").build();
		Game g1 = GameBuilder.aGame().withId("teste").withKillsCount(1215).withNumber(1)
				.withPlayers(new HashSet<Player>(Arrays.asList(p1, p2))).build();
		Game g2 = GameBuilder.aGame().withId("teste2").withKillsCount(1215).withNumber(2)
				.withPlayers(new HashSet<Player>(Arrays.asList(p3, p4))).build();

		when(gameInfoServiceMock.fingGamesByPlayerName("Maria")).thenReturn(Arrays.asList(g1, g2));
		ResultActions resultAction = mockMvc
				.perform(get(BASE_URI + "?" + ORDER_KILL + "=desc&"+PLAYER_NAME_FILTER+"=Maria"))
				.andExpect(status().is(200)).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));

		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_1.players[0]").value("Joao"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_1.players[1]").value("Maria"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[0]").value("Xena"));
		resultAction.andExpect(jsonPath("$").isNotEmpty()).andExpect(jsonPath("$.game_2.players[1]").value("Maria"));

		verify(gameInfoServiceMock, times(1)).fingGamesByPlayerName("Maria");
		verifyNoMoreInteractions(gameInfoServiceMock);
	}

}
