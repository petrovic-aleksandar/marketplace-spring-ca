package me.aco.marketplace_spring_ca.infrastructure.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to restrict access to self-update operations only to the authenticated user
 * matching the target user ID. The path variable name must be "id" for this to work.
 * 
 * Usage:
 * @SelfOwner
 * @PostMapping("/update-self/{id}")
 * public ResponseEntity<UserDto> updateSelf(@PathVariable Long id, @RequestBody UpdateSelfCommand command) { ... }
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("@selfOwnershipService.isOwnProfile(#id, authentication.principal)")
public @interface SelfOwner {
}
