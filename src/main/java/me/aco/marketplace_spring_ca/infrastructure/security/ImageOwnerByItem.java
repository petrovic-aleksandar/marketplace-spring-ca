package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to restrict access to image addition operations only to the item owner.
 * The path variable must be "itemId".
 * 
 * Usage:
 * @ImageOwnerByItem
 * @PostMapping(value = "{itemId}", consumes = { "multipart/form-data" })
 * public ResponseEntity<ImageDto> add(@PathVariable Long itemId, @RequestParam("file") MultipartFile file) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('USER') and @imageOwnershipService.isItemOwner(#itemId, authentication.principal)")
public @interface ImageOwnerByItem {
}
