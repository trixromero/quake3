package teste.backend.controllers;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import teste.backend.dtos.GameDto;
import teste.backend.dtos.MessageDto;
import teste.backend.dtos.PlayerDto;
import teste.backend.entities.Game;
import teste.backend.entities.Player;
import teste.backend.service.GameInfoService;
import teste.backend.utils.MessageCodes;
import teste.backend.utils.Messages;

@RestController
@CrossOrigin
@RequestMapping(path = "games", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GameController {
   
   private final Logger logger = LoggerFactory.getLogger(GameController.class);

	@Autowired
	private GameInfoService gameInfoService;

	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private HttpServletRequest request;

	@Autowired
	private Messages messages;
	
	@ApiOperation(value = "Retorna Game de acordo com ID",
	         notes="Players de cada game podem ser ordenados por quantidade de kills" , 
	         produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
   @ApiResponses({ 
      @ApiResponse(code = 200, message = " Busca realizada com sucesso " , response = GameDto.class, responseContainer="Map"),
      @ApiResponse(code = 400, message = "Entrada inválida"),
      @ApiResponse(code = 404, message = "Dados não encontrados")
   })
	@GetMapping(path = "/{gameNumber}")
	public ResponseEntity<?> getGameInfo(@PathVariable("gameNumber") Integer gameNumber,
			@RequestParam(value = "playersInnerGameOrderDirectionByKill", required = false) String order) {
		
	   logger.info(defaultRequestLog(gameNumber,"order="+order));
	   
		if (gameNumber == null || gameNumber < 0){
			return new ResponseEntity<>(new MessageDto(messages.get(MessageCodes.INVALID_PARAMETER_REQUEST)), HttpStatus.BAD_REQUEST);
		}

		Direction playersInnerGameOrderDirection = getOrderDirection(order);
		Game game = gameInfoService.findGameByGameNumber(gameNumber);
		
		if (game == null){
		   return new ResponseEntity<>(new MessageDto(messages.get(MessageCodes.NOT_FOUND)), HttpStatus.NOT_FOUND);
		}
		
		Map<String, GameDto> gamesReponse = mountResponse(
				Arrays.asList(game), playersInnerGameOrderDirection);
		
		logger.info("Game found" + gamesReponse + " returning http status 200 [OK] ");
		
		return ResponseEntity.ok(gamesReponse);
	}

	@ApiOperation(value = "Retorna Ranking somado de acordo com todos Games",
            notes="Pode ser ordenado de acordo com quantidade de kills" , 
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
   @ApiResponses({ 
      @ApiResponse(code = 200, message = " Busca realizada com sucesso " , response = PlayerDto.class, responseContainer="list"),
      @ApiResponse(code = 400, message = "Entrada inválida"),
      @ApiResponse(code = 404, message = "Dados não encontrados")
   })
	@GetMapping(path = "/ranking")
	public ResponseEntity<?> getGamesRanking(@RequestParam(value = "rankingOrder", required = false) String order) {
	   logger.info(defaultRequestLog("order="+order));
		Direction rankingDirection = getOrderDirection(order);
		List<Player> ranking = gameInfoService.getRanking(rankingDirection);
		
		List<PlayerDto> rankingDto ;
		
		if (ranking == null || ranking.isEmpty()){
		   return new ResponseEntity<>(new MessageDto(messages.get(MessageCodes.NOT_FOUND)), HttpStatus.NOT_FOUND);
		}else{
		   rankingDto = ranking.stream().map(p-> PlayerDto.toDto( p )).collect( Collectors.toList() );
		}
		
		logger.info("Ranking " + rankingDto + " returning http status 200 [OK] ");
		return ResponseEntity.ok(rankingDto);
	}

	  @ApiOperation(value = "Retorna todos os Games processados",
	            notes="Pode ser filtrado por nome do Player e os Players em cada jogo pode ser ordenados por quantidade de kills" , 
	            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	   @ApiResponses({ 
	      @ApiResponse(code = 200, message = " Busca realizada com sucesso " , response = GameDto.class, responseContainer="map"),
	      @ApiResponse(code = 404, message = "Dados não encontrados")
	   })
	@GetMapping()
	public ResponseEntity<?> getGameInfos(
			@RequestParam(value = "playersInnerGameOrderDirectionByKill", required = false) String order,
			@RequestParam(value = "playerName", required = false) String playerName) {

	   logger.info(defaultRequestLog("order="+order,"playerName="+playerName));
	   
		Direction playersInnerGameOrderDirection = getOrderDirection(order);
		
		List<Game> games =  StringUtils.isEmpty(playerName) ? gameInfoService.retrieveAllGames() : gameInfoService.fingGamesByPlayerName(playerName);
		
		if (games == null || games.isEmpty()){
		   return new ResponseEntity<>(new MessageDto(messages.get(MessageCodes.NOT_FOUND)), HttpStatus.NOT_FOUND);
		}
		
		Map<String, GameDto> gamesReponse = mountResponse(games,
				playersInnerGameOrderDirection);
		
		logger.info("Games found" + gamesReponse + " returning http status 200 [OK] ");
		
		return ResponseEntity.ok(gamesReponse);
	}
	
	  @ApiOperation(value = "Processo arquivo de Log de quake3 para gerar estaticas de Kill dos jogos",
              produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
     @ApiResponses({ 
        @ApiResponse(code = 200, message = " Arquivo recebido com sucesso" , response = GameDto.class, responseContainer="map"),
        @ApiResponse(code = 400, message = "Arquivo Vazio/Extensão Inválida")
     })  
	@PostMapping(path = "uploadFile", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<MessageDto> uploadLogStaticsFile(@RequestParam MultipartFile file) throws IOException {

	   logger.info(defaultRequestLog(file.getOriginalFilename()));
	   
		if (file.isEmpty()) {
			return new ResponseEntity<>(new MessageDto(messages.get(MessageCodes.FILE_EMPTY)), HttpStatus.BAD_REQUEST);
		}

		if (!FilenameUtils.getExtension(file.getOriginalFilename()).equals("log")) {
			return new ResponseEntity<>(new MessageDto(messages.get(MessageCodes.FILE_INVALID_EXTENSION)),
					HttpStatus.BAD_REQUEST);
		}

		jmsTemplate.convertAndSend("readerQueue", file.getBytes());
		
		logger.info("File sent to Queue readerQueue" + file.getOriginalFilename() + " returning http status 200 [OK] ");

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
	
	private String defaultRequestLog(Object... parameters) {
      return "Request [" + request.getMethod() + "] uri:" + request.getRequestURI() + " received: " + parameters;
   }

}