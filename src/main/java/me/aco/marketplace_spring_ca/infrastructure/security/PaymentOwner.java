package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to restrict access to payment operations only to the user making the payment.
 * The request body must contain a userId field.
 * 
 * Usage:
 * @PaymentOwner
 * @PostMapping("/payment")
 * public ResponseEntity<TransferDto> addPayment(@RequestBody AddPaymentCommand command) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('USER') and @transferOwnershipService.isUserIdOwner(#command?.userId(), authentication.principal)")
public @interface PaymentOwner {
}
