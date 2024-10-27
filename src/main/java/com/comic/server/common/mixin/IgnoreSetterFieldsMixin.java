package com.comic.server.common.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(
    value = {"id", "slug", "originalSource"},
    allowGetters = true)
public abstract class IgnoreSetterFieldsMixin {}
