package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to restrict access to item operations only to the item owner.
 * The path variable name must be "id" or "itemId" for this to work.
 * 
 * Usage:
 * @ItemOwner("id")
 * @PutMapping("/Deactivate/{id}")
 * public ResponseEntity<ItemDto> deactivateItem(@PathVariable Long id) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@itemOwnershipService.isItemOwner(#itemId, authentication.principal)")
public @interface ItemOwner {
}
