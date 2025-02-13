package org.tests.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;

public class Official_serverSteps {

    //Sucesso ao executar
    @Given("the official server is configured correctly")
    public void the_official_server_is_configured_correctly() {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }
    @When("the local client \\(port {int}) runs correctly")
    public void the_local_client_port_runs_correctly(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }

    //Falha
    @When("the local client \\(port {int}) does not run correctly")
    public void the_local_client_port_does_not_run_correctly(Integer int1) {
        // Write code here that turns the phrase above into concrete actions
        throw new io.cucumber.java.PendingException();
    }


}
