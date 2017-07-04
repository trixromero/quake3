package teste.backend.utils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component
public class Messages {

	@Autowired
	private MessageSource messageSource;

	private MessageSourceAccessor accessor;

	@PostConstruct
	private void init() {
		accessor = new MessageSourceAccessor(messageSource);
	}

	public String get(String code) {
		return accessor.getMessage(code,LocaleContextHolder.getLocale());
	}
	
	public String get(String code, String... parameter){
		return accessor.getMessage(code, parameter, LocaleContextHolder.getLocale());
	}

}