package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to restrict access to purchase operations only to the buyer.
 * The request body must contain a buyerId field.
 * 
 * Usage:
 * @PurchaseOwner
 * @PostMapping("/purchase")
 * public ResponseEntity<TransferDto> addPurchase(@RequestBody PurchaseItemCommand command) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('USER') and @transferOwnershipService.isUserIdOwner(#command?.buyerId(), authentication.principal)")
public @interface PurchaseOwner {
}
