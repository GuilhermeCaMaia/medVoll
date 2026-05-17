package med.voll.api.service;

import med.voll.api.domain.medico.Especialidade;
import med.voll.api.domain.medico.Medico;
import med.voll.api.dto.endereco.EnderecoDTO;
import med.voll.api.dto.medico.AtualizacaoMedicoDTO;
import med.voll.api.dto.medico.CadastroMedicoDTO;
import med.voll.api.repository.MedicoRepository;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MedicoServiceTest {

    @InjectMocks
    private MedicoService medicoService;

    @Mock
    private MedicoRepository medicoRepository;

    @Test
    @DisplayName("Deveria cadastrar medico com sucesso")
    void cadastrarMedicoCenario1(){
        // Arrange ou Given
        var dadosCadastro = cadastroMedicoDTO();
        var medicoSalvo = new Medico(dadosCadastro);
        // Act ou When
        when(medicoRepository.save(any(Medico.class))).thenReturn(medicoSalvo);
        var resultado = medicoService.cadastrar(dadosCadastro);
        // Assert ou then
        assertThat(resultado).isNotNull();
        assertThat(resultado.email()).isEqualTo(dadosCadastro.email());
        assertThat(resultado.crm()).isEqualTo(dadosCadastro.crm());

        verify(medicoRepository, times(1)).save(any(Medico.class));
    }

    @Test
    @DisplayName("Deveria dar erro ao cadastrar medico nullo")
    void cadastrarMedicoCenario3(){
        // Arrange ou Given
        var medicoCadastro = new CadastroMedicoDTO(
                null,
                null,
                null,
                null,
                null,
                null
        );
        // Act ou When
        // Assert ou then
        assertThrows(NullPointerException.class, () -> {
            medicoService.cadastrar(medicoCadastro);
        });
    }

//    @Test
//    @DisplayName("Deveria dar erro de email repetido")
//    void cadastrarMedicoCenario2(){
//        // Arrange ou Given
//        var medico1Cadastro = cadastroMedicoDTO();
//        medicoService.cadastrar(medico1Cadastro);
//        var medico2cadastro = new CadastroMedicoDTO(
//                "Guilherme",
//                "guilherme.maia@voll.med",
//                "21999995555",
//                "654321",
//                Especialidade.CARDIOLOGIA,
//                dadosEndereco()
//        );
//        // Act ou When
//        // Assert ou then
//        assertThrows(ValidacaoException.class, () -> {
//            medicoService.cadastrar(medico2cadastro);
//        });
//    }

//    @Test
//    @DisplayName("Deveria dar erro de crm repetido")
//    void cadastrarMedicoCenario3(){
//        // Arrange ou Given
//        var medico1Cadastro = cadastroMedicoDTO();
//        medicoService.cadastrar(medico1Cadastro);
//        var medico2cadastro = new CadastroMedicoDTO(
//                "Guilherme",
//                "outro.email@voll.med",
//                "21999995555",
//                "123456",
//                Especialidade.CARDIOLOGIA,
//                dadosEndereco()
//        );
//        // Act ou When
//        // Assert ou then
//        assertThrows(ValidacaoException.class, () -> {
//            medicoService.cadastrar(medico2cadastro);
//        });
//    }

    @Test
    @DisplayName("Deveria listar medicos ativos")
    void listarCenario1(){
        // Arrange ou Given
        Pageable paginacao = PageRequest.of(0, 10);
        var medico = new Medico(cadastroMedicoDTO());
        var paginaMock = new PageImpl<>(List.of(medico));

        when(medicoRepository.findAllByAtivoTrue(paginacao)).thenReturn(paginaMock);
        // Act ou When
        var resultado = medicoService.listar(paginacao);
        // Assert ou then
        assertThat(resultado).isNotNull();
        assertThat(resultado.getContent()).hasSize(1);
        assertThat(resultado.getContent().get(0).nome()).isEqualTo(medico.getNome());

        verify(medicoRepository, times(1)).findAllByAtivoTrue(paginacao);
    }

    @Test
    @DisplayName("Deveria atualizar informacoes do medico")
    void atualizarCenario1(){
        // Arrange ou Given
        var dadosAtualizacao = new AtualizacaoMedicoDTO(1L, "Novo Nome", null, null);
        var medicoOriginal = new Medico(cadastroMedicoDTO());

        when(medicoRepository.getReferenceById(1L)).thenReturn(medicoOriginal);
        // Act ou When
        var resultado = medicoService.atualizar(dadosAtualizacao);
        // Assert ou then
        assertThat(resultado.nome()).isEqualTo("Novo Nome");
        verify(medicoRepository, times(1)).getReferenceById(1L);
    }

    @Test
    @DisplayName("Deveria inativar/excluir medico")
    void excluirCenario1(){
        // Arrange ou Given
        var medico = spy(new Medico(cadastroMedicoDTO()));
        when(medicoRepository.getReferenceById(1L)).thenReturn(medico);
        // Act ou When
        medicoService.excluir(1L);
        // Assert ou then
        verify(medicoRepository, times(1)).getReferenceById(1L);
        verify(medico, times(1)).excluir();
        assertThat(medico.getAtivo()).isFalse();
    }



    private CadastroMedicoDTO cadastroMedicoDTO() {
        return new CadastroMedicoDTO(
                "Guilherme",
                "guilherme.maia@voll.med",
                "22999998888",
                "123456",
                Especialidade.CARDIOLOGIA,
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















