package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to restrict access to image modification operations only to the item owner.
 * The path variable must be "imageId".
 * 
 * Usage:
 * @ImageOwner
 * @PostMapping("/front/{imageId}")
 * public ResponseEntity<ImageDto> makeImageFront(@PathVariable Long imageId) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('USER') and @imageOwnershipService.isImageOwner(#imageId, authentication.principal)")
public @interface ImageOwner {
}
