package med.voll.api.service;

import med.voll.api.domain.paciente.Paciente;
import med.voll.api.dto.endereco.EnderecoDTO;
import med.voll.api.dto.paciente.AtualizacaoPacienteDTO;
import med.voll.api.dto.paciente.CadastroPacienteDTO;
import med.voll.api.repository.PacienteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class PacienteServiceTest {

    @InjectMocks
    private PacienteService pacienteService;

    @Mock
    private PacienteRepository pacienteRepository;

    @Test
    @DisplayName("Deveria cadastrar paciente com sucesso")
    void cadastrarPacienteCenario1(){
        var dadosCadastro = cadastroPacienteDTO();
        var pacienteSalvo = new Paciente(dadosCadastro);

        when(pacienteRepository.save(any(Paciente.class))).thenReturn(pacienteSalvo);
        var resultado = pacienteService.cadastrar(dadosCadastro);

        assertThat(resultado).isNotNull();
        assertThat(resultado.email()).isEqualTo(dadosCadastro.email());
        assertThat(resultado.cpf()).isEqualTo(dadosCadastro.cpf());

        verify(pacienteRepository, times(1)).save(any(Paciente.class));
    }

    @Test
    @DisplayName("Não deveria cadastrar paciente com sucesso")
    void cadastrarPacienteCenario2(){
        var pacienteCadastro = new CadastroPacienteDTO(
                null,
                null,
                null,
                null,
                null
        );

        assertThrows(NullPointerException.class, () -> {
            pacienteService.cadastrar(pacienteCadastro);
        });
    }

    @Test
    @DisplayName("Deveria listar pacientes ativos")
    void listarCenario1(){
        Pageable paginacao = PageRequest.of(0, 10);
        var paciente = new Paciente(cadastroPacienteDTO());
        var paginaMock = new PageImpl<>(List.of(paciente));

        when(pacienteRepository.findAllByAtivoTrue(paginacao)).thenReturn(paginaMock);

        var resultado = pacienteService.listar(paginacao);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo(paciente.getNome());
    }

    @Test
    @DisplayName("Deveria atualizar informacoes do paciente")
    void atualizarCenario1(){
        var dadosAtualizar = new AtualizacaoPacienteDTO(
                1L, "Novo nome", null, null
        );

        when(pacienteRepository.getReferenceById(1L))
                .thenReturn(new Paciente(cadastroPacienteDTO()));

        var resultado = pacienteService.atualizar(dadosAtualizar);
        assertThat(resultado.nome()).isEqualTo("Novo nome");
        verify(pacienteRepository, times(1)).getReferenceById(1L);
    }

    @Test
    @DisplayName("Deveria inativar/excluir paciente")
    void excluirCenario1(){
        var paciente = spy(new Paciente(cadastroPacienteDTO()));

        when(pacienteRepository.getReferenceById(1L)).thenReturn(paciente);

        pacienteService.excluir(1L);
        verify(paciente, times(1)).excluir();
        assertThat(paciente.getAtivo()).isFalse();
    }

    private CadastroPacienteDTO cadastroPacienteDTO(){
        return new CadastroPacienteDTO(
                "Guilherme",
                "guilherme.maia@voll.med",
                "22997524385",
                "12345678911",
                dadosEndereco()
        );
    }

    private EnderecoDTO dadosEndereco() {
        return new EnderecoDTO(
                "rua acre",
                "bairro",
                "12345678",
                "Rio de Janeiro",
                "RJ",
                "complemento",
                "1"
        );
    }
}