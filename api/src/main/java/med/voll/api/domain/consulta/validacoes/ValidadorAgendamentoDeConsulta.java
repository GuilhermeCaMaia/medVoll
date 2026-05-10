package med.voll.api.domain.consulta.validacoes;

import med.voll.api.dto.consulta.AgendamentoConsultaDTO;

public interface ValidadorAgendamentoDeConsulta {

    void validar(AgendamentoConsultaDTO dados);
}
