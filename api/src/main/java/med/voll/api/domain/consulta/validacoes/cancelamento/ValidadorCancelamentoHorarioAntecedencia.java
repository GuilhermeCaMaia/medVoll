package med.voll.api.domain.consulta.validacoes.cancelamento;

import med.voll.api.domain.ValidacaoException;
import med.voll.api.repository.ConsultaRepository;
import med.voll.api.dto.consulta.CancelamentoConsultaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class ValidadorCancelamentoHorarioAntecedencia implements ValidadorCancelamentoDeConsulta {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Override
    public void validar(CancelamentoConsultaDTO dados) {
        var horaAgora = LocalDateTime.now();
        var horaConsulta = consultaRepository.getReferenceById(dados.idConsulta());
        var diferencaEmHoras = Duration.between(horaAgora, horaConsulta.getData()).toHours();

        if (diferencaEmHoras < 24){
            throw new ValidacaoException("Consulta somente pode ser cancelada com antecedência mínima de 24h!");
        }
    }
}
