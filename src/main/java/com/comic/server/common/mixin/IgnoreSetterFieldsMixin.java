package com.comic.server.common.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(
    value = {"id", "slug"},
    allowGetters = true)
public abstract class IgnoreSetterFieldsMixin {}
