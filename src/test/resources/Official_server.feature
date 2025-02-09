Feature: Local client

  Scenario: Sucesso ao executar server oficial
    Given o server oficial esteja configurado corretamente
    When o cliente local (porta 123) seja executado corretamente
    Then o retorno deve estar correto

  Scenario: Falha ao executar server oficial
    Given o server oficial esteja configurado corretamente
    When o cliente local (porta 123) nao seja executado corretamente
    Then uma mensagem de erro deve ser exibida


