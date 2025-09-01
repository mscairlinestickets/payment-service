package com.erickWck.payment_service.domain;

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
public class CardTransaction {

    private Long paymentId;

    private Long bookId;

    @Size(min = 3, max = 170, message = "O nome deve conter entre {min} e {max} caracteres.")
    private String name;

    @CPF
    @NotBlank(message = "Insira o cpfNumber")
    private String cpfNumber;

    @NotBlank(message = "Insira o nome do responsavel pelo cartão.")
    private String cardholderName;

    private BigDecimal amount;

    @NotBlank(message = "Insira o tipo do cartão credito ou debito")
    private String type;

    @NotBlank(message = "Insira o numero do cartão")
    @Pattern(regexp = "^([0-9]{16})$", message = "O número do cartão está invalido.")
    private String cardNumber;

    @NotBlank(message = "Insira a data de expiração")
    @Pattern(regexp = "^([0-9]{6})$", message = "A data de expiração deve conter só 6 numeros.")
    private String expiryDate;

    @NotBlank(message = "Insira o código de segurançã do cartão")
    @Pattern(regexp = "^([0-9]{3})$", message = "O número do cartão está invalido.")
    private String cvv;

    @NotNull(message = "O limite não pode estar vazio.")
    @Size(min = 0)
    private BigDecimal limit;

    private PaymentStatus status;

    // Agora você pode só mudar o status com setStatus
    public void approved() {
        this.status = PaymentStatus.APPROVED;
    }

    public void rejected() {
        this.status = PaymentStatus.REJECTED;
    }
}
