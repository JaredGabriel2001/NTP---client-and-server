package org.tests.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;

public class TestHooks {
    @Before
    public void setup() {
        System.out.println("Iniciando o teste...");
    }

    @After
    public void teardown() {
        System.out.println("Teste finalizado.");
    }
}