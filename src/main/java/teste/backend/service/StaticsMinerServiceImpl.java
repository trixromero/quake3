package teste.backend.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import teste.backend.entities.Game;
import teste.backend.entities.Player;

@Service
public class StaticsMinerServiceImpl implements StaticsMinerService {

	private final static String INIT_GAME = "InitGame";

	private final static String KILL = "Kill";

	private final static String KILLER_NAME_DELIMITER = "killed";

	private final static String KILLED_NAME_DELIMITER = "by";

	private final static String WORLD = "<world>";

	private Game currentGame;

	@Autowired
	private GameInfoService gameInfoService;

	@Autowired
	private NextSequenceService nextSeqService;

	@Override
	@JmsListener(destination = "readerQueue")//, containerFactory = "myFactory")
	public void extractStaticsFromFile(byte[] file) throws IOException {

		String readLine = "";

		File statics = new File("statics");
		FileUtils.writeByteArrayToFile(statics, file);

		try (Scanner scanner = new Scanner(statics)) {

			while (scanner.hasNext()) {

				readLine = scanner.nextLine();

				if (!readLine.contains(INIT_GAME) && !readLine.contains(KILL)) {
					continue;
				} else {
					extractInfo(readLine.trim());
				}
			}
			
			saveActualStatics();	
		} catch (IOException e) {
			throw e;
		}
	}

	private void resetStatics() {
		currentGame = new Game();

	}

	public void extractInfo(String readLine) {
		if (readLine.contains(INIT_GAME)) {
			saveActualStatics();
			resetStatics();
		} else {
			if (readLine.contains(KILL)) {
				extractKillInfo(readLine);
			}
		}

	}

	public void extractKillInfo(String readLine) {
		String killInfoToken = readLine.split(":")[3].trim();
		String killerName = killInfoToken.split(KILLER_NAME_DELIMITER)[0].trim();
		String killedName = killInfoToken.split(KILLER_NAME_DELIMITER)[1].split(KILLED_NAME_DELIMITER)[0].trim();
		calculateScore(killerName, killedName);
	}

	private void calculateScore(String killerName, String killedName) {

		currentGame.setTotalKills(currentGame.getTotalKills() + 1);

		if (!killedName.equals(killerName)) {
			if (!killerName.equals(WORLD)) {
				Player killer = getPlayerByNameInCurrentGame(killerName);
				killer.setKillsCount(killer.getKillsCount() + 1);
				addPlayerToCurrentGame(new Player(killedName));
			} else {
				Player killed = getPlayerByNameInCurrentGame(killedName);
				killed.setKillsCount(killed.getKillsCount() - 1);
			}
		} else {
			addPlayerToCurrentGame(new Player(killedName));
		}
	}

	private Player getPlayerByNameInCurrentGame(String playerName) {

		Optional<Player> player = currentGame.getPlayers().stream().filter(p->p.getName().equals(playerName)).findFirst();
		return player.isPresent() ? player.get() : addPlayerToCurrentGame(new Player(playerName));

	}

	private Player addPlayerToCurrentGame(Player player) {
		currentGame.getPlayers().add(player);
		return player;
	}

	public void saveActualStatics() {
		if (currentGame != null) {
			currentGame.setNumber(nextSeqService.getNextSequence("games"));
			gameInfoService.saveGame(currentGame);
		}
	}

	public Game getCurrentGame() {
		return currentGame;
	}

	public void setCurrentGame(Game currentGame) {
		this.currentGame = currentGame;
	}
	
	

}
