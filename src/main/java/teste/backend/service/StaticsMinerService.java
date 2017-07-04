package teste.backend.service;

import java.io.IOException;

public interface StaticsMinerService {
   
	void extractStaticsFromFile(byte[] file) throws IOException;
	
	void extractInfo(String readLine);
	
	void extractKillInfo(String readLine);

}
