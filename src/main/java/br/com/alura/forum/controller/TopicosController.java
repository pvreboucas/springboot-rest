package br.com.alura.forum.controller;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.alura.forum.controller.dto.DetalhesTopicoDto;
import br.com.alura.forum.controller.dto.TopicoDto;
import br.com.alura.forum.controller.form.AtualizacaoTopicoForm;
import br.com.alura.forum.controller.form.TopicoForm;
import br.com.alura.forum.modelo.Topico;
import br.com.alura.forum.repository.CursoRepository;
import br.com.alura.forum.repository.TopicoRepository;

//Sinaliza que todos os métodos do Controller serão @ResponseBody
@RestController
@RequestMapping("/topicos")
public class TopicosController {

	@Autowired
	private TopicoRepository topicoRepository;
	
	@Autowired
	private CursoRepository cursoRepository;
	
//  busca todos os topicos
	@GetMapping
//	@RequestMapping(value="/topicos", method = RequestMethod.GET)
//  Indica que o método não retornará uma página, e que a chamada da url no RequestMapping retornará os dados para a página corrente.	
//	@ResponseBody
	public List<TopicoDto> lista(String nomeCurso) {
		//Topico topico = new Topico("Dúvida", "dúvida com spring", new Curso("Spring", "Programação"));
		//return TopicoDto.converter(Arrays.asList(topico, topico, topico));
		List<Topico> topicos = null;
		if(nomeCurso == null) {
			topicos = topicoRepository.findAll();
		}
		else {
			//Pesquisar pelo parâmetro de um objeto proveniente de um relacionamento
			topicos = topicoRepository.findByCurso_Nome(nomeCurso);
		}
		return TopicoDto.converter(topicos);
	}
	
//	insere um topico novo
	@PostMapping
//	@RequestMapping(value="/topicos", method = RequestMethod.POST)
//	O @ResquestBody indica que os dados serão retirados do corpo da requisição http ao invés da URL. Dados estes atribuídos ao objeto parâmetro do método.
//	O ResponseEntity personaliza os tipos de retornos padrão de servidores, exemplo: 200, 404, 500, etc...
//  O UriComponenteBuilder captura a uri da requisição
//	@Valid valida os dados informados conforme as anotations em TopicoForm
	public ResponseEntity<TopicoDto> cadastrar(@RequestBody @Valid TopicoForm topicoForm, UriComponentsBuilder uriBuilder) {
		Topico topico = topicoForm.converter(cursoRepository);
		topicoRepository.save(topico);
//		as chaves {} indica que o que está dentro dela é um parâmetro dinâmico, uma informação mutável conforme o cenário.
		URI uri = uriBuilder.path("/topicos/{id}").buildAndExpand(topico.getId()).toUri();
		return ResponseEntity.created(uri).body(new TopicoDto(topico));
	}
	
//	busca um topico específico
//	parâmetro dinâmico
	@GetMapping("/{id}")
	public ResponseEntity<DetalhesTopicoDto> detalhar(@PathVariable("id") Long codigo) {
//		Topico topico = topicoRepository.getOne(codigo);
		Optional<Topico> topico = topicoRepository.findById(codigo);
		if(topico.isPresent()) {
			return ResponseEntity.ok(new DetalhesTopicoDto(topico.get()));
		}
		else {
			return ResponseEntity.notFound().build();
		}
			
	}
	
//	atualiza um topico específico
	@PutMapping("/{id}")
//	indica que é uma transação que deve ser persistida - commit - caso não exista Exception
	@Transactional
	public ResponseEntity<TopicoDto> atualizar(@PathVariable Long id, @RequestBody @Valid AtualizacaoTopicoForm topicoForm){
		Optional<Topico> topicoOptional = topicoRepository.findById(id);
		if(topicoOptional.isPresent()) {
			Topico topico = topicoForm.atualizar(id, topicoRepository);
			return ResponseEntity.ok(new TopicoDto(topico));
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}

// remove um tópico
	@DeleteMapping("/{id}")
	@Transactional
	public ResponseEntity<?> remover(@PathVariable Long id){
		Optional<Topico> topicoOptional = topicoRepository.findById(id);
		//Responde com os atributos padrão da requisição http
		if(topicoOptional.isPresent()) {
			topicoRepository.deleteById(id);
			return ResponseEntity.ok().build();
		}
		else {
			return ResponseEntity.notFound().build();
		}
	}
	
}
