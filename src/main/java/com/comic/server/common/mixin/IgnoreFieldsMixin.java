package com.comic.server.common.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(
    value = {"createdAt", "updatedAt", "createdBy", "updatedBy"},
    allowGetters = false,
    allowSetters = false)
public abstract class IgnoreFieldsMixin {}
