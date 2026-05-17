package med.voll.api.service;

import jakarta.transaction.Transactional;
import med.voll.api.domain.ValidacaoException;
import med.voll.api.domain.consulta.MotivoCancelamento;
import med.voll.api.domain.medico.Especialidade;
import med.voll.api.domain.medico.Medico;
import med.voll.api.domain.paciente.Paciente;
import med.voll.api.dto.consulta.AgendamentoConsultaDTO;
import med.voll.api.dto.consulta.CancelamentoConsultaDTO;
import med.voll.api.dto.endereco.EnderecoDTO;
import med.voll.api.dto.medico.CadastroMedicoDTO;
import med.voll.api.dto.paciente.CadastroPacienteDTO;
import med.voll.api.repository.ConsultaRepository;
import med.voll.api.repository.MedicoRepository;
import med.voll.api.repository.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Transactional
class ConsultaServiceTest {

    @Autowired
    private ConsultaService consultaService;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private MedicoRepository medicoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Test
    @DisplayName("Deveria agendar consulta com sucesso quando os dados forem validos")
    void agendarCenario1(){
        var medico = cadastrarMedico("Guilherme Maia", "Guilherme.maia@voll.med", "123456", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("Fulano", "fulano@gmail.com", "11122233344");
        var dataConsulta = LocalDateTime.now().plusMonths(10).with(
                java.time.temporal.TemporalAdjusters.nextOrSame(
                        java.time.DayOfWeek.MONDAY
                )).withHour(10);
        var agendamentoDTO = new AgendamentoConsultaDTO(
                medico.getId(), paciente.getId(), dataConsulta, Especialidade.CARDIOLOGIA
        );

        var resultado = consultaService.agendar(agendamentoDTO);

        assertThat(resultado).isNotNull();
    }

    @Test
    @DisplayName("Deveria lançar exceção ao tentar agendar com paciente inexistente")
    void agendarCenario2(){
        var dataConsulta = LocalDateTime.now().plusMonths(10).with(
                java.time.temporal.TemporalAdjusters.nextOrSame(
                        java.time.DayOfWeek.MONDAY
                )).withHour(10);

        var dadosAgnedamento = new AgendamentoConsultaDTO(
                1L, 99999L, dataConsulta, Especialidade.CARDIOLOGIA
        );

        var excecao = assertThrows(ValidacaoException.class, () -> {
            consultaService.agendar(dadosAgnedamento);
        });

        assertThat(excecao.getMessage())
                .isEqualTo("Id do paciente informado não existe!");
    }

    @Test
    @DisplayName("Deveria cancelar uma consulta com sucesso")
    void cancelarConsulta1(){
        var medico = cadastrarMedico("Guilherme Maia", "Guilherme.maia@voll.med", "123456", Especialidade.CARDIOLOGIA);
        var paciente = cadastrarPaciente("Fulano", "fulano@gmail.com", "11122233344");
        var dataConsulta = LocalDateTime.now().plusMonths(10).with(
                java.time.temporal.TemporalAdjusters.nextOrSame(
                        java.time.DayOfWeek.MONDAY
                )).withHour(10);
        var agendamentoDTO = new AgendamentoConsultaDTO(
                medico.getId(), paciente.getId(), dataConsulta, Especialidade.CARDIOLOGIA
        );
        var consultaAgenda = consultaService.agendar(new AgendamentoConsultaDTO(
                medico.getId(), paciente.getId(), dataConsulta, Especialidade.CARDIOLOGIA
        ));

        var dadosCancelamento = new CancelamentoConsultaDTO(consultaAgenda.id(), MotivoCancelamento.PACIENTE_DESISTIU);
        consultaService.cancelar(dadosCancelamento);

        var consultaCancelada = consultaRepository.getReferenceById(consultaAgenda.id());
        assertThat(consultaCancelada.getMotivoCancelamento()).isEqualTo(MotivoCancelamento.PACIENTE_DESISTIU);
    }

//    @Test
//    @DisplayName("Deveria lançar exceção ao tentar cancelar consulta inexistente")
//    void cancelarConsulta2(){
//        var dadosCancelamento = new CancelamentoConsultaDTO(999L, MotivoCancelamento.PACIENTE_DESISTIU);
//
//        var excecao = assertThrows(ValidacaoException.class, () -> {
//            consultaService.cancelar(dadosCancelamento);
//        });
//
//        assertThat(excecao.getMessage()).isEqualTo("Id do paciente informado nao existe!");
//    }

    private AgendamentoConsultaDTO agendamentoConsultaDTO(){
        return new AgendamentoConsultaDTO(
                1L,
                1L,
                LocalDateTime.now(),
                Especialidade.CARDIOLOGIA
        );
    }

    private Medico cadastrarMedico(String nome, String email, String crm, Especialidade especialidade) {
        var dto = new CadastroMedicoDTO(nome, email, "21999999999", crm, especialidade, dadosEndereco());
        var medico = new Medico(dto);
        return medicoRepository.save(medico);
    }

    private Paciente cadastrarPaciente(String nome, String email, String cpf) {
        var dto = new CadastroPacienteDTO(nome, email, "21999998888", cpf, dadosEndereco());
        var paciente = new Paciente(dto);
        return pacienteRepository.save(paciente);
    }

    private EnderecoDTO dadosEndereco() {
        return new EnderecoDTO("Rua Acre", "Bairro Central", "12345678", "Rio de Janeiro", "RJ", null, "10");
    }
}