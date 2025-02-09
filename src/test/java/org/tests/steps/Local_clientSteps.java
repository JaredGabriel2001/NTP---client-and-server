package org.tests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class Local_clientSteps {

    //Sucesso ao executar cliente local
    @Given("o server local esteja sendo executado corretamente")
    public void o_server_local_esteja_sendo_executado_corretamente() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("o cliente local seja executado corretamente")
    public void o_cliente_local_seja_executado_corretamente() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("o retorno deve estar correto")
    public void o_retorno_deve_estar_correto() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //Falha ao executar cliente local
    @When("o cliente local nao seja executado corretamente")
    public void o_cliente_local_nao_seja_executado_corretamente() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @Then("uma mensagem de erro deve ser exibida")
    public void uma_mensagem_de_erro_deve_ser_exibida() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //Sucesso ao executar cliente local com HMAC
    @Given("o server local utilizando HMAC esteja sendo executado corretamente")
    public void o_server_local_utilizando_hmac_esteja_sendo_executado_corretamente() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("o cliente local utilizando HMAC seja executado corretamente")
    public void o_cliente_local_utilizando_hmac_seja_executado_corretamente() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //Falha ao executar cliente local com HMAC
    @When("o cliente local utilizando HMAC nao seja executado corretamente")
    public void o_cliente_local_utilizando_hmac_nao_seja_executado_corretamente() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }



}
