Feature: Local client

  Scenario: Sucesso ao executar server local
    Given o server local esteja sendo executado corretamente
    When o cliente local seja executado corretamente
    Then o retorno deve estar correto

  Scenario: Falha ao executar server local
    Given o server local esteja sendo executado corretamente
    When o cliente local nao seja executado corretamente
    Then uma mensagem de erro deve ser exibida

  Scenario: Sucesso ao executar server local com HMAC
    Given o server local utilizando HMAC esteja sendo executado corretamente
    When o cliente local utilizando HMAC seja executado corretamente
    Then o retorno deve estar correto

  Scenario: Falha ao executar server local com HMAC
    Given o server local utilizando HMAC esteja sendo executado corretamente
    When o cliente local utilizando HMAC nao seja executado corretamente
    Then uma mensagem de erro deve ser exibida
