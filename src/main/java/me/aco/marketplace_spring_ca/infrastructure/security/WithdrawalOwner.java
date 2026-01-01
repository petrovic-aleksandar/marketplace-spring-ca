package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to restrict access to withdrawal operations only to the user withdrawing.
 * The request body must contain a userId field.
 * 
 * Usage:
 * @WithdrawalOwner
 * @PostMapping("/withdrawal")
 * public ResponseEntity<TransferDto> addWithdrawal(@RequestBody AddWithdrawalCommand command) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('USER') and @transferOwnershipService.isUserIdOwner(#command?.userId(), authentication.principal)")
public @interface WithdrawalOwner {
}
