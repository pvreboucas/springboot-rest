package br.com.alura.forum.config.validacao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//anotação indicando que a classe será um interceptador de exceções
@RestControllerAdvice
public class ErroDeValidacaoHandler {

	//classe que ajuda a pegar mensagens de erro conforme o idioma da aplicação e requisição
	@Autowired
	private MessageSource messageSource;
	
	//modifica o status code de retorno para error 400
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	//Captura os erros de validação de formulário
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public List<ErroDeFormularioDto> handle(MethodArgumentNotValidException exception) {
		List<ErroDeFormularioDto> errosDeFormularioDto = new ArrayList<ErroDeFormularioDto>();
		List<FieldError> fieldErros = exception.getBindingResult().getFieldErrors();
		
		//percorrer lista
		fieldErros.forEach(e -> {
			String mensagemErro = messageSource.getMessage(e, LocaleContextHolder.getLocale());
			ErroDeFormularioDto erro = new ErroDeFormularioDto(e.getField(), mensagemErro);
			errosDeFormularioDto.add(erro);
		});
		
		return errosDeFormularioDto;
	}
	
}
