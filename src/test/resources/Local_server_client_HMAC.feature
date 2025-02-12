Feature: Local client and server HMAC

  Scenario: Sucesso ao executar cliente e server local com HMAC
    Given o server local utilizando HMAC esteja sendo executado corretamente
    When o cliente local utilizando HMAC seja executado corretamente
    Then o retorno deve estar correto

  Scenario: Falha ao executar cliente e server local com HMAC
    Given o server local utilizando HMAC esteja sendo executado corretamente
    When o cliente local utilizando HMAC nao seja executado corretamente
    Then uma mensagem de erro deve ser exibida