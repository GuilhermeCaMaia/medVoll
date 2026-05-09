package med.voll.api.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import med.voll.api.domain.paciente.*;
import med.voll.api.dto.paciente.AtualizacaoPacienteDTO;
import med.voll.api.dto.paciente.CadastroPacienteDTO;
import med.voll.api.dto.paciente.DetalhamentoPacienteDTO;
import med.voll.api.dto.paciente.ListagemPacienteDTO;
import med.voll.api.repository.PacienteRepository;
import med.voll.api.service.PacienteService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("pacientes")
@SecurityRequirement(name = "bearer-key")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @PostMapping
    @Transactional
    public ResponseEntity cadastrar(@RequestBody CadastroPacienteDTO dados, UriComponentsBuilder uriBuilder){
        var dto = pacienteService.cadastrar(dados);
        var uri = uriBuilder.path("pacientes/{id}").buildAndExpand(dto.id()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity<Page<ListagemPacienteDTO>> listar(@ParameterObject @PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao){
        var page = pacienteService.listar(paginacao);
        return ResponseEntity.ok(page);
    }

    @PutMapping
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid AtualizacaoPacienteDTO dados){
        var paciente = pacienteService.atualizar(dados);
        return ResponseEntity.ok(paciente);
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity deletar(@PathVariable Long id){
        pacienteService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetalhamentoPacienteDTO> detalhar(@PathVariable Long id) {
        var dto = pacienteService.detalhar(id);
        return ResponseEntity.ok(dto);
    }
}









