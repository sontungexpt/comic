package com.comic.server.annotation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Pageable as query param.
 *
 * @author bnasslahsen
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
    in = ParameterIn.QUERY,
    description = "Zero-based page index (0..N)",
    name = "page",
    schema = @Schema(type = "integer", defaultValue = "0"))
@Parameter(
    in = ParameterIn.QUERY,
    description = "The size of the page to be returned",
    name = "size",
    schema = @Schema(type = "integer", defaultValue = "24"))
@Parameter(
    in = ParameterIn.QUERY,
    description =
        "Sorting criteria in the format: property,(asc|desc). "
            + "Default sort order is ascending. "
            + "Multiple sort criteria are supported.",
    name = "sort",
    array = @ArraySchema(schema = @Schema(type = "string")))
@Parameter(hidden = true, name = "pageable")
public @interface PageableQueryParams {}
