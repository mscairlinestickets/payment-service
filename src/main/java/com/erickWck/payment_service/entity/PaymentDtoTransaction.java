package com.erickWck.payment_service.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.math.BigDecimal;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDtoTransaction {

    private Long bookId;

    @Size(min = 3, max = 170, message = "O nome deve conter entre {min} e {max} caracteres.")
    private String name;

    @CPF
    private String cpfNumber;

    @Schema(description = "Nome do titular do cartão", example = "João da Silva", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cardholderName;

    @Size(min = 3, max = 170, message = "A chave pix deve conter entre {min} e {max} caracteres.")
    private String pixKey;

    BigDecimal amount;

    @Schema(description = "Tipo de cartão (credito ou debito)", example = "credito", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "Insira o tipo do cartão credito ou debito")
    private String type;

    @Schema(description = "Número do cartão", example = "1234567890123456", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^([0-9]{16})$", message = "O número do cartão está invalido.")
    private String cardNumber;

    @Schema(description = "Data de expiração (MMYYYY)", example = "122025", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^([0-9]{6})$", message = "A data de expiração deve conter só 6 numeros.")
    private String expiryDate;

    @Schema(description = "Código de segurança (CVV)", example = "123", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^([0-9]{3})$", message = "O número do cartão está invalido.")
    private String cvv;


    @NotNull(message = "O valor disponível não pode estar vazio.")
    @Size(min = 0)
    private BigDecimal availableAmount;

    private PaymentStatus status;

    private PaymentType paymentType;

    public void approved() {
        this.status = PaymentStatus.APPROVED;
    }

    public void rejected() {
        this.status = PaymentStatus.REJECTED;
    }
}
