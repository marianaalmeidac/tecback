package br.uniesp.si.techback.controller;


import br.uniesp.si.techback.exception.ConflitoDeDadosException;
import br.uniesp.si.techback.exception.EntidadeNaoEncontradaException;
import br.uniesp.si.techback.model.Conteudo;
import br.uniesp.si.techback.model.Tipo;
import br.uniesp.si.techback.service.ConteudoService;
import jakarta.validation.ConstraintTarget;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/conteudos")
@RequiredArgsConstructor
public class ConteudoController {
    private final ConteudoService conteudoService;

    @GetMapping
    public ResponseEntity<Page<Conteudo>> listarConteudos(
            @RequestParam(required = false) Tipo tipo,
            @RequestParam(required = false) String genero,
            Pageable pageable) {
        Page<Conteudo> conteudos = conteudoService.listarFiltradoEPaginado(tipo, genero, pageable);
        return ResponseEntity.ok(conteudos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detalharConteudo(@PathVariable UUID id) {
        try {
            Conteudo conteudo = conteudoService.buscarPorId(id);
            return ResponseEntity.ok(conteudo);
        } catch (EntidadeNaoEncontradaException erro) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> criarConteudo(@Valid @RequestBody Conteudo conteudo) {
        try {
            Conteudo conteudoSalvo = conteudoService.criar(conteudo);
            return ResponseEntity.status(HttpStatus.CREATED).body(conteudoSalvo);
        } catch (IllegalArgumentException erro) {
            Map<String, String> errado = Map.of("errado", erro.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errado);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarConteudo(
            @PathVariable UUID id,
            @Valid @RequestBody Conteudo conteudo) {
        try {
            Conteudo conteudoAtualizado = conteudoService.atualizar(id, conteudo);
            return ResponseEntity.ok(conteudoAtualizado);

        } catch (EntidadeNaoEncontradaException e) {
            return ResponseEntity.notFound().build();

        } catch (ConflitoDeDadosException erro) {
            Map<String, String> errado = Map.of("erro", erro.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(errado);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConteudo(@PathVariable UUID id) {
        try {
            conteudoService.deletar(id);
            return ResponseEntity.noContent().build();
        } catch (EntidadeNaoEncontradaException erro) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/ordenado/titulo")
    public ResponseEntity<List<Conteudo>> listarOrdenadoPorTitulo() {
        List<Conteudo> conteudos = conteudoService.listarOrdenadoPorTitulo();
        return ResponseEntity.ok(conteudos);
    }

    @GetMapping("/genero")
    public ResponseEntity<List<Conteudo>> buscarPorGenero( @RequestParam String genero) {
        List<Conteudo> lista = conteudoService.buscarPorGeneroOrdenado(genero);
        return ResponseEntity.ok(lista);
    }

    @GetMapping
    public ResponseEntity<List<Conteudo>> topConteudos(@RequestParam int n) {
        List<Conteudo> conteudos = conteudoService.buscarTopNConteudos(n);
        return ResponseEntity.ok(conteudos);
    }

    @GetMapping("/apos-ano")
    public ResponseEntity<List<Conteudo>> conteudosAposAno(@RequestParam int ano) {
        List<Conteudo> conteudos = conteudoService.buscarConteudosLancadosDepoisDe(ano);
        return ResponseEntity.ok(conteudos);
    }

    @GetMapping("/com-trailer")
    public ResponseEntity<List<Conteudo>> conteudosComTrailer() {
        List<Conteudo> conteudo = conteudoService.buscarConteudosComTrailer();
        return ResponseEntity.ok(conteudo);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Conteudo>> buscarConteudos(@RequestParam String palavra) {
        List<Conteudo> conteudo = conteudoService.buscarPorPalavraChave(palavra);
        return ResponseEntity.ok(conteudo);
    }
}
