package teste.backend.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import teste.backend.dtos.GameDto;
import teste.backend.dtos.MessageDto;
import teste.backend.entities.Game;
import teste.backend.entities.Player;
import teste.backend.service.GameInfoService;
import teste.backend.utils.MessageCodes;
import teste.backend.utils.Messages;

@RestController
@CrossOrigin
@RequestMapping(path = "games", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GameController {

	@Autowired
	private GameInfoService gameInfoService;

	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	private Messages messages;

	@GetMapping(path = "/{gameNumber}")
	public ResponseEntity<?> getGameInfo(@PathVariable("gameNumber") Integer gameNumber,
			@RequestParam(value = "playersInnerGameOrderDirectionByKill", required = false) String order) {
		
		if (gameNumber == null || gameNumber < 0){
			return new ResponseEntity<>(new MessageDto(messages.get(MessageCodes.FILE_EMPTY)), HttpStatus.BAD_REQUEST);
		}

		Direction playersInnerGameOrderDirection = getOrderDirection(order);
		Map<String, GameDto> gamesReponse = mountResponse(
				Arrays.asList(gameInfoService.findGameByGameNumber(gameNumber)), playersInnerGameOrderDirection);
		return ResponseEntity.ok(gamesReponse);
	}

	@GetMapping(path = "/ranking")
	public ResponseEntity<List<Player>> getGamesRanking(@RequestParam(value = "rankingOrder", required = false) String order) {
		Direction rankingDirection = getOrderDirection(order);
		List<Player> ranking = gameInfoService.getRanking(rankingDirection);
		return ResponseEntity.ok(ranking);
	}

	@GetMapping()
	public ResponseEntity<Map<String, GameDto>> getGameInfos(
			@RequestParam(value = "playersInnerGameOrderDirectionByKill", required = false) String order,
			@RequestParam(value = "playerName", required = false) String playerName) {

		Direction playersInnerGameOrderDirection = getOrderDirection(order);
		
		List<Game> games =  StringUtils.isEmpty(playerName) ? gameInfoService.retrieveAllGames() : gameInfoService.fingGamesByPlayerName(playerName);
		
		Map<String, GameDto> gamesReponse = mountResponse(games,
				playersInnerGameOrderDirection);
		return ResponseEntity.ok(gamesReponse);
	}
	

	@PostMapping(path = "uploadFile", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<MessageDto> uploadLogStaticsFile(@RequestParam MultipartFile file) throws IOException {

		if (file.isEmpty()) {
			return new ResponseEntity<>(new MessageDto(messages.get(MessageCodes.FILE_EMPTY)), HttpStatus.BAD_REQUEST);
		}

		if (!FilenameUtils.getExtension(file.getOriginalFilename()).equals("log")) {
			return new ResponseEntity<>(new MessageDto(messages.get(MessageCodes.FILE_INVALID_EXTENSION)),
					HttpStatus.BAD_REQUEST);
		}

		jmsTemplate.convertAndSend("readerQueue", file.getBytes());

		return ResponseEntity.ok(new MessageDto(messages.get(MessageCodes.FILE_RECEIVED)));

	}

	private Map<String, GameDto> mountResponse(List<Game> games, Direction order) {

		Map<String, GameDto> responseMap = new LinkedHashMap<>();
		games.stream().forEach(g -> responseMap.put(getGameTag(g.getNumber()), GameDto.toDto(g, order)));
		return responseMap;
	}

	private String getGameTag(Integer gameNumber) {
		return "game_" + gameNumber;
	}

	private Direction getOrderDirection(String killersOrder) {
		return (StringUtils.isEmpty(killersOrder) || killersOrder.equalsIgnoreCase("desc")) ? Sort.Direction.DESC
				: Sort.Direction.ASC;
	}

}