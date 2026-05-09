package med.voll.api.domain.consulta.validacoes.cancelamento;

import med.voll.api.dto.consulta.CancelamentoConsultaDTO;

public interface ValidadorCancelamentoDeConsulta {

    void validar(CancelamentoConsultaDTO dados);
}
