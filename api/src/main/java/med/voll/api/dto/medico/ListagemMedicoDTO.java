package med.voll.api.dto.medico;

import med.voll.api.domain.medico.Especialidade;
import med.voll.api.domain.medico.Medico;

public record ListagemMedicoDTO(
        Long id,
        String nome,
        String email,
        String crm,
        Especialidade especialidade
) {
    public ListagemMedicoDTO(Medico medico){
        this(
                medico.getId(),
                medico.getNome(),
                medico.getEmail(),
                medico.getCrm(),
                medico.getEspecialidade()
        );
    }
}
