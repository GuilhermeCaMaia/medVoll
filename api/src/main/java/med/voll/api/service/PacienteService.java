package med.voll.api.service;

import med.voll.api.domain.paciente.Paciente;
import med.voll.api.dto.paciente.AtualizacaoPacienteDTO;
import med.voll.api.dto.paciente.CadastroPacienteDTO;
import med.voll.api.dto.paciente.DetalhamentoPacienteDTO;
import med.voll.api.dto.paciente.ListagemPacienteDTO;
import med.voll.api.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Transactional
    public DetalhamentoPacienteDTO cadastrar(CadastroPacienteDTO dto){
        var paciente = new Paciente(dto);
        pacienteRepository.save(paciente);
        return new DetalhamentoPacienteDTO(paciente);
    }

    @Transactional(readOnly = true)
    public Page<ListagemPacienteDTO> listar(Pageable paginacao){
        return pacienteRepository
                .findAllByAtivoTrue(paginacao).map(ListagemPacienteDTO::new);
    }

    @Transactional
    public DetalhamentoPacienteDTO atualizar(AtualizacaoPacienteDTO dto){
        var paciente = pacienteRepository.getReferenceById(dto.id());
        paciente.atualizarInformacoes(dto);
        return new DetalhamentoPacienteDTO(paciente);
    }

    @Transactional
    public void excluir(Long id){
        var paciente = pacienteRepository.getReferenceById(id);
        paciente.excluir();
    }

    @Transactional(readOnly = true)
    public DetalhamentoPacienteDTO detalhar(Long id) {
        var paciente = pacienteRepository.getReferenceById(id);
        return new DetalhamentoPacienteDTO(paciente);
    }
}
