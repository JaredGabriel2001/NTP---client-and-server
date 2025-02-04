# **Cliente e servidor utilizando NTP**

## Objetivo Principal
O objetivo deste trabalho prático é implementar um cliente e um servidor compatíveis com o
protocolo Network Time Protocol (NTP). O sistema implementado deverá ser capaz de se
comunicar com clientes e servidores NTP oficiais, respeitando o protocolo NTP na versão 3 ou
superior. O foco está na construção manual das funcionalidades do NTP, sem o uso de bibliotecas
específicas para NTP, mas com a possibilidade de usar bibliotecas de criptografia para autenticação.
O cliente desenvolvido deve fornecer uma interface que permita informar o endereço do servidor.
Na ausência deste parâmetro, o cliente deve utilizar um servidor oficial do NTP.br. O endereço do
servidor deve ser informado ao usuário juntamente com a data e hora calculados.

## Requisitos:
1. **Protocolo:**   
   1. A implementação deve seguir a especificação do NTPv4 (RFC 5905).
   2. A comunicação deve ocorrer via UDP na porta 123, conforme o protocolo oficial. O
servidor local pode ser executado em outra porta, se necessário.
2. **Funcionalidades:**
   1. O cliente NTP deve ser capaz de solicitar e receber o tempo de servidores NTP
compatíveis (incluindo servidores públicos, como pool.ntp.org).
   1. O servidor NTP deve ser capaz de responder a requisições de clientes NTP,
incluindo servidores e clientes oficiais, fornecendo o tempo correto.
   1. O tempo deve ser corretamente calculado, utilizando o formato de timestamp NTP
(contagem de segundos desde 1900).
3. **Autenticação (Opcional, com pontos extras):**
   1. Implementar autenticação usando HMAC-SHA256 com suporte para chaves pré-
c/ompartilhadas.
   2. A autenticação deve seguir o modelo do NTPsec para garantir a integridade das
mensagens trocadas entre cliente e servidor.
4. Linguagem de Programação:
   1. A escolha da linguagem é livre (Python, C, Go, Rust, etc.), desde que a implementação respeite a especificação do NTP e seja compatível com clientes e servidores oficiais.
   2. Não é permitido o uso de bibliotecas prontas para NTP. No entanto, bibliotecas de criptografia podem ser usadas para a parte de autenticação.
5. **Implementação Manual:**
   1. O pacote NTP deve ser construído manualmente, incluindo campos como Leap Indicator, Version, Mode, Stratum, Poll Interval, Precision, Root Delay, Root Dispersion, Reference ID, e os timestamps de origem, recebimento, e transmissão.
   2. A implementação deve calcular corretamente o offset e o round-trip delay (atraso de ida e volta) para ajustar o tempo do cliente com o servidor.
6. **Comunicação:**
   1. O cliente deve enviar pacotes NTP adequadamente formatados para o servidor e esperar pela resposta. Ele deve informar o horário atualizado baseado no tempo recebido e calcular os parâmetros necessários, como offset e delay.
   2. O servidor deve receber a requisição, calcular a resposta corretamente e enviá-la de volta ao cliente.
   
## Etapas do Desenvolvimento:
1. **Entendimento do Protocolo NTP:**   
      - Estudar a especificação oficial do NTPv4 (RFC 5905), com foco no formato de
pacote NTP e nas funções de cliente e servidor.
      - Compreender como calcular o offset e o round-trip delay de forma correta.
2. **Implementação do Cliente:**
      - Construir um pacote NTP manualmente com os campos adequados. 
      - Enviar o pacote para servidores NTP oficiais (ex.: a.ntp.br).
      - Receber o pacote de resposta, extrair os timestamps e ajustar o relógio do cliente.
3. **Implementação do Servidor:**
      - Receber pacotes de clientes NTP (tanto implementados manualmente quanto
oficiais).
      - Calcular o timestamp correto e responder ao cliente com um pacote NTP
devidamente formatado. 
4. **Autenticação:**
      - Implementar um sistema de autenticação usando HMAC, criptografando o conteúdo
da mensagem com uma chave compartilhada.
      - Verificar a autenticidade das mensagens recebidas e garantir que apenas clientes
autorizados possam sincronizar o tempo.
5. **Testes:**
      - Testar a implementação com outros servidores NTP oficiais para garantir a
compatibilidade. 
      - Testar o servidor com clientes oficiais e vice-versa.

## Instruções de execução do programa principal:

## Instruções de execução dos testes:
