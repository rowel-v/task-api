package com.rowel.taskapi;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModularityTest {

  @Test
  void verifiesModuleBoundaries() {
    ApplicationModules.of(TaskApiApplication.class).verify();
  }
}
