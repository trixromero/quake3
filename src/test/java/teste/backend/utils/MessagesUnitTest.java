package teste.backend.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MessagesUnitTest {
	
	@Autowired
	Messages messages;

	@Test
	public void shouldGetAMessage(){
		String recebido = messages.get(MessageCodes.FILE_RECEIVED);
		assertThat(recebido).isNotBlank();
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void shouldGetAMessageAPortugueseMessage(){
		LocaleContextHolder.getLocale().setDefault(Locale.getDefault());
		String recebido = messages.get(MessageCodes.FILE_RECEIVED);
		assertThat(recebido).isEqualTo("Arquivo recebido com sucesso");
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void shouldGetAMessageAnEnglishMessage(){
		LocaleContextHolder.getLocale().setDefault(Locale.ENGLISH);
		String recebido = messages.get(MessageCodes.FILE_RECEIVED);
		assertThat(recebido).isEqualTo("File received with sucess");
	}
	
}
