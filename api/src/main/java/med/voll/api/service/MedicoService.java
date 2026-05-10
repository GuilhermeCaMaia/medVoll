package med.voll.api.service;

import med.voll.api.domain.medico.Medico;
import med.voll.api.dto.medico.AtualizacaoMedicoDTO;
import med.voll.api.dto.medico.CadastroMedicoDTO;
import med.voll.api.dto.medico.DetalhamentoMedicoDTO;
import med.voll.api.dto.medico.ListagemMedicoDTO;
import med.voll.api.repository.MedicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Transactional
    public DetalhamentoMedicoDTO cadastrar(CadastroMedicoDTO dto){
        var medico = new Medico(dto);
        medicoRepository.save(medico);
        return new DetalhamentoMedicoDTO(medico);
    }

    @Transactional(readOnly = true)
    public Page<ListagemMedicoDTO> listar(Pageable paginacao){
        return medicoRepository.findAllByAtivoTrue(paginacao)
                .map(ListagemMedicoDTO::new);
    }

    @Transactional
    public DetalhamentoMedicoDTO atualizar(AtualizacaoMedicoDTO dto){
        var medico = medicoRepository.getReferenceById(dto.id());
        medico.atualizarInformacoes(dto);

        return new DetalhamentoMedicoDTO(medico);
    }

    @Transactional
    public void excluir(Long id){
        var medico = medicoRepository.getReferenceById(id);
        medico.excluir();
    }

    @Transactional(readOnly = true)
    public DetalhamentoMedicoDTO detalhar(Long id){
        var medico = medicoRepository.getReferenceById(id);
        return new DetalhamentoMedicoDTO(medico);
    }
}
