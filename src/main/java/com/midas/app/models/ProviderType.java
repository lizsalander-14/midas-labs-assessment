package com.midas.app.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProviderType {
  STRIPE("stripe");

  private String name;
}
