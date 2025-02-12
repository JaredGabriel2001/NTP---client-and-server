Feature: Local server and client

  Scenario: Sucesso ao executar servidor e cliente local
    Given o server local esteja sendo executado corretamente
    When o cliente local seja executado corretamente
    Then o retorno deve estar correto

  Scenario: Falha ao executar servidor e cliente local
    Given o server local esteja sendo executado corretamente
    When o cliente local nao seja executado corretamente
    Then uma mensagem de erro deve ser exibida

