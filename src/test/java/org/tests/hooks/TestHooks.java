package org.tests.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

public class TestHooks {

    @Before
    public void setup(Scenario scenario) {
        System.out.println("Iniciando cenário: " + scenario.getName());
    }

    @After
    public void teardown(Scenario scenario) {
        if (scenario.isFailed()) {
            System.out.println("Cenário falhou: " + scenario.getName());
        }
        System.out.println("Cenário finalizado: " + scenario.getName());
    }
}
