package com.comic.server.common.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(
    value = {"id", "createdAt", "updatedAt", "createdBy", "updatedBy", "slug"},
    allowGetters = true)
public abstract class IgnoreFieldsMixin {}
