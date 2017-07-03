package teste.backend.controllers;

import java.io.IOException;

import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
@RequestMapping(path = "games", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class GameController {





	@GetMapping(path = "/{gameNumber}")
	public ResponseEntity<?> getGameInfo(@PathVariable("gameNumber") Integer gameNumber,
			@RequestParam(value = "playersInnerGameOrderDirectionByKill", required = false) String order) {
		return null;
		
	}

	@GetMapping(path = "/ranking")
	public ResponseEntity<?> getGamesRanking(@RequestParam(value = "rankingOrder", required = false) String order) {
		return null;
	}

	@GetMapping()
	public ResponseEntity<?> getGameInfos(
			@RequestParam(value = "playersInnerGameOrderDirectionByKill", required = false) String order,
			@RequestParam(value = "playerName", required = false) String playerName) {
		return null;
	}
	

	@PostMapping(path = "uploadFile", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<?> uploadLogStaticsFile(@RequestParam MultipartFile file) throws IOException {

		if (file.isEmpty()) {
			return new ResponseEntity<String>("Arquivo Vazio", HttpStatus.BAD_REQUEST);
		}

		if (!FilenameUtils.getExtension(file.getOriginalFilename()).equals("log")) {
			return new ResponseEntity<String>("Extensao Invalida",
					HttpStatus.BAD_REQUEST);
		}

		

		return ResponseEntity.ok("OK");

	}


}