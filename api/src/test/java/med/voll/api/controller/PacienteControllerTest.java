package med.voll.api.controller;

import med.voll.api.domain.endereco.Endereco;
import med.voll.api.domain.medico.Especialidade;
import med.voll.api.dto.endereco.EnderecoDTO;
import med.voll.api.dto.paciente.AtualizacaoPacienteDTO;
import med.voll.api.dto.paciente.CadastroPacienteDTO;
import med.voll.api.dto.paciente.DetalhamentoPacienteDTO;
import med.voll.api.service.PacienteService;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class PacienteControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<CadastroPacienteDTO> cadastroPacienteDTO;

    @Autowired
    private JacksonTester<DetalhamentoPacienteDTO> detalhamentoPacienteDTO;

    @Autowired
    private JacksonTester<AtualizacaoPacienteDTO> atualizacaoPacienteDTO;

    @MockitoBean
    private PacienteService pacienteService;

    @Test
    @DisplayName("Deveria devolver codigo http 400 quando cadastrar paciente")
    @WithMockUser
    void cadastrarCenario1() throws Exception {
        // ARRANGE
        // ACT
        var response = mvc.perform(post("/pacientes"))
                .andReturn().getResponse();
        // ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("Deveria devolver codigo http 200 quando cadastrar paciente")
    @WithMockUser
    void cadastrarCenario2() throws Exception {
        // ARRANGE
        var dadosCadastro = new CadastroPacienteDTO(
                "Paciente",
                "paciente@vollmed",
                "11222333444",
                "11122233345",
                dadosEndereco()
        );

        var detalhamentoPaciente = new DetalhamentoPacienteDTO(
                null,
                "Paciente",
                "paciente@vollmed",
                "11222333444",
                "11122233345",
                new Endereco(dadosEndereco())
        );
        // ACT
        var response = mvc.perform(
                post("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(detalhamentoPacienteDTO.write(detalhamentoPaciente).getJson())
        ).andReturn().getResponse();
        // ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());

        var jsonEsperado = detalhamentoPacienteDTO.write(detalhamentoPaciente).getJson();
        assertThat(response.getContentAsString()).isEqualTo(jsonEsperado);
    }

    @Test
    @DisplayName("Deveria devolver codigo http 200 quando listar pacientes")
    @WithMockUser
    void listarCenario1() throws Exception {
        // ACT
        var response = mvc.perform(
                get("/pacientes")
        ).andReturn().getResponse();
        // ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deveria devolver codigo http 200 quando atualizar paciente")
    @WithMockUser
    void atualizarCenario1() throws Exception {
        // ARRANGE
        var atualizarDTO = new AtualizacaoPacienteDTO(
                1L,
                "Paciente",
                "11222333444",
                null
        );

        var detalhamentoPaciente = new DetalhamentoPacienteDTO(
                null,
                "Paciente",
                "paciente@vollmed",
                "11222333444",
                "11122233345",
                new Endereco(dadosEndereco())
        );
        // ACT
        var response = mvc.perform(
                put("/pacientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(atualizacaoPacienteDTO.write(atualizarDTO).getJson())
        ).andReturn().getResponse();
        // ASSERT
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    @DisplayName("Deveria devolver codigo http 204 ao excluir um paciente")
    @WithMockUser
    void excluirCenario1() throws Exception {
        var response = mvc.perform(
                delete("/pacientes/1")
        ).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(pacienteService, times(1)).excluir(1L);
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