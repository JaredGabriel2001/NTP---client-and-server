package org.tests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class Official_serverSteps {

    //Sucesso ao executar
    @Given("o server oficial esteja configurado corretamente")
    public void o_server_oficial_esteja_configurado_corretamente() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("o cliente local \\(porta {int}) seja executado corretamente")
    public void o_cliente_local_porta_seja_executado_corretamente(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //Falha ao executar
    @When("o cliente local \\(porta {int}) nao seja executado corretamente")
    public void o_cliente_local_porta_nao_seja_executado_corretamente(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


}
