package br.com.phamtecnologia.apiclientes.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteDto {

    @Size(min = 3, max = 150, message = "O nome do cliente deve ter de 3 a 150 caracteres.")
    @NotEmpty(message = "O nome do cliente é obrigatório.")
    private String nome;

    @Email(message = "O e-mail do cliente deve estar em um formato válido.")
    @NotEmpty(message = "O e-mail do cliente é obrigatório.")
    private String email;

    @Pattern(regexp = "^\\d{2}\\d{9}$", message = "O telefone deve ter somente números no formato: (21) 98745-9632.")
    @NotEmpty(message = "o telefone do cliente é obrigatório.")
    private String telefone;

    @Pattern(regexp = "^(PESSOA_FISICA|PESSOA_JURIDICA)$", message = "O tipo deve ser apenas PESSOA_FISICA ou PESSOA_JURIDICA.")
    @NotEmpty(message = "O tipo do cliente é obrigatório.")
    private String tipo;

}
