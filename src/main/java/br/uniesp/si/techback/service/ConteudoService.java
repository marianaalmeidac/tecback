package br.uniesp.si.techback.service;

import br.uniesp.si.techback.exception.ConflitoDeDadosException;
import br.uniesp.si.techback.exception.EntidadeNaoEncontradaException;
import br.uniesp.si.techback.model.Conteudo;
import br.uniesp.si.techback.model.Tipo;
import br.uniesp.si.techback.model.Usuario;
import br.uniesp.si.techback.repository.ConteudoRepository;
import br.uniesp.si.techback.repository.ConteudoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;


import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ConteudoService {

    private final ConteudoRepository conteudoRepository;

    public Page<Conteudo> listarFiltradoEPaginado(Tipo tipo, String genero, Pageable pageable) {
        Specification<Conteudo> spec = ConteudoSpecification.comFiltros(tipo, genero);

        return conteudoRepository.findAll(spec, pageable);
    }

    public Conteudo buscarPorId(UUID id) {
        return conteudoRepository.findById(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Conteúdo com ID" + id + "não encontrado."));
    }

    public Conteudo criar(Conteudo conteudo) {
        boolean jaExiste = conteudoRepository.existsByTituloIgnoreCaseAndAnoAndTipo(
                conteudo.getTitulo(),
                conteudo.getAno(),
                conteudo.getTipo()
        );

        if (jaExiste) {
            throw new IllegalArgumentException("Um conteúdo com o mesmo título, ano e tipo já existe");
        }

        return conteudoRepository.save(conteudo);
    }

    public Conteudo atualizar(UUID id, Conteudo dadosAtualizados) {
        Conteudo conteudoExistente = buscarPorId(id);

        boolean jaExisteOutro = conteudoRepository.existsByTituloIgnoreCaseAndAnoAndTipoAndIdNot(
                dadosAtualizados.getTitulo(),
                dadosAtualizados.getAno(),
                dadosAtualizados.getTipo(),
                id
        );

        if (jaExisteOutro) {
            throw new ConflitoDeDadosException("Já existe outro conteúdo com o mesmo título, ano e tipo.");
        }

        conteudoExistente.setTitulo(dadosAtualizados.getTitulo());
        conteudoExistente.setTipo(dadosAtualizados.getTipo());
        conteudoExistente.setAno(dadosAtualizados.getAno());
        conteudoExistente.setDuracaoMinutos(dadosAtualizados.getDuracaoMinutos());
        conteudoExistente.setRelevancia(dadosAtualizados.getRelevancia());
        conteudoExistente.setSinopse(dadosAtualizados.getSinopse());
        conteudoExistente.setTrailerUrl(dadosAtualizados.getTrailerUrl());
        conteudoExistente.setGenero(dadosAtualizados.getGenero());

        return conteudoRepository.save(conteudoExistente);
    }

    public void deletar(UUID id) {
        if (!conteudoRepository.existsById(id)) {
            throw new EntidadeNaoEncontradaException("Conteúdo com ID" + id + " não encontrado");
        }
        conteudoRepository.deleteById(id);
    }

    public List<Conteudo> listarOrdenadoPorTitulo() {
        return conteudoRepository.listarOrdenadoPorTitulo();
    }

    public List<Conteudo> buscarPorGeneroOrdenado(String genero) {
        return conteudoRepository.findByGeneroIgnoreCaseOrderByTituloAsc(genero);
    }

    public List<Conteudo> buscarTopNConteudos(int n) {
        Pageable pageable = PageRequest.of(0, n);
        return conteudoRepository.findTopNByRelevancia(pageable);
    }

    public List<Conteudo> buscarConteudosLancadosDepoisDe(int ano) {
        return conteudoRepository.findConteudosLancadosDepoisDe(ano);
    }

    public List<Conteudo> buscarConteudosComTrailer() {
        return conteudoRepository.findConteudoComTrailer();
    }

    public List<Conteudo> buscarPorPalavraChave(String palavra) {
        return conteudoRepository.buscarPorPalavraChave(palavra);
    }

}
