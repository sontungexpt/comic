package com.comic.server.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to access currently authenticated user in the codebase. Works the same as
 * AuthenticationPrincipal. Created to reduce dependency on Spring Security
 */
@Target({ElementType.FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonPatchIgnore {}
