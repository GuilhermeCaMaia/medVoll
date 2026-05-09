package med.voll.api.dto.medico;

import jakarta.validation.constraints.NotNull;
import med.voll.api.dto.endereco.DadosEndereco;

public record DadosAtualizacaoMedico(
        @NotNull
        Long id,
        String nome,
        String telefone,
        DadosEndereco endereco
) {
}
