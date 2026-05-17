package med.voll.api.controller;

import med.voll.api.domain.endereco.Endereco;
import med.voll.api.domain.medico.Especialidade;
import med.voll.api.dto.endereco.EnderecoDTO;
import med.voll.api.dto.medico.AtualizacaoMedicoDTO;
import med.voll.api.dto.medico.CadastroMedicoDTO;
import med.voll.api.dto.medico.DetalhamentoMedicoDTO;
import med.voll.api.service.MedicoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class MedicoControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<CadastroMedicoDTO> cadastroMedicoDTO;

    @Autowired
    private JacksonTester<DetalhamentoMedicoDTO> detalhamentoMedicoDTO;

    @Autowired
    private JacksonTester<AtualizacaoMedicoDTO> atualizacaoMedicoDTO;

    @MockitoBean
    private MedicoService medicoService;

    @Test
    @DisplayName("Deveria devolver codigo http 400 quando cadastrar medico")
    @WithMockUser
    void cadastrarCenario1() throws Exception {
        // ACT
        var response = mvc.perform(post("/medicos"))
                .andReturn().getResponse();
        // ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deveria devolver codigo http 201 quando cadastrar medico")
    @WithMockUser
    void cadastrarCenario2() throws Exception {
        // ARRANGE
        var dadosCadastro = new CadastroMedicoDTO(
                "Medico",
                "medico@voll.med",
                "11222333444",
                "123456",
                Especialidade.CARDIOLOGIA,
                dadosEndereco()
        );

        var detalhamentoMedico = new DetalhamentoMedicoDTO(
                null,
                "Medico",
                "medico@voll.med",
                "123456",
                "11222333444",
                Especialidade.CARDIOLOGIA,
                new Endereco(dadosEndereco())
        );

        when(medicoService.cadastrar(any())).thenReturn(detalhamentoMedico);
        // ACT
        var response = mvc.perform(
                post("/medicos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(detalhamentoMedicoDTO.write(detalhamentoMedico).getJson())
        ).andReturn().getResponse();

        // ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        var jsonEsperado = detalhamentoMedicoDTO.write(detalhamentoMedico).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }

    @Test
    @DisplayName("Deveria devolver codigo http 200 quando listar medicos")
    @WithMockUser
    void listarCenario1() throws Exception {
        var response = mvc.perform(
                get("/medicos")
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deveria devolver codigo http 200 quando atualizar medicos")
    @WithMockUser
    void atualizarCenario1() throws Exception {
        var atualizarDTO = new AtualizacaoMedicoDTO(
                1L,
                "Medico",
                "11222333444",
                null
        );
        var detalhamentoMedico = new DetalhamentoMedicoDTO(
                null,
                "Medico",
                "medico@voll.med",
                "123456",
                "11222333444",
                Especialidade.CARDIOLOGIA,
                new Endereco(dadosEndereco())
        );
        // ACT
        var response = mvc.perform(
                put("/medicos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atualizacaoMedicoDTO.write(atualizarDTO).getJson())
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deveria devolver codigo http 204 ao excluir um medico")
    @WithMockUser
    void excluirCenario1() throws Exception {
        var response = mvc.perform(delete("/medicos/1"))
                .andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(medicoService, times(1)).excluir(1L);
    }

    private EnderecoDTO dadosEndereco() {
        return new EnderecoDTO(
                "rua xpto",
                "bairro",
                "00000000",
                "Brasilia",
                "DF",
                null,
                null
        );
    }
}