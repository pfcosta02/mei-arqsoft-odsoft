# Documentação ARQSOFT (Arquitetura de Software)

Este documento serve para toda a documentação do projeto para a unidade curricular de Arquitetura de Software (ARQSOFT)

---
## Architectural Software Requirements (ASR)

### Contexto
O sistema LMS (Library Management System) é um serviço backend responsável pela gestão de uma biblioteca. O sistema disponibiliza um conjunto de endpoints que permitem a interação com as suas funcionalidades principais, incluindo a gestão de livros, géneros, autores, leitores e empréstimos. Através destes serviços, é possível realizar operações de consulta, criação, atualização e eliminação dos diferentes recursos.

No sprint/projeto anterior, a aplicação LMS foi desenvolvida como monólito modular, mas verificou-se que esta arquitetura centralizada limita significativamente a performance, disponibilidade, escalabilidade e elasticidade.

### Problemas
Nesta nova iteração, devem ser resolvidos os seguintes problemas:

- **Disponibilidade**: Necessidade de garantir uptime elevado mesmo com falhas em micro-serviços, mensagens ou base de dados.
- **Performance em Alta Demanda**: O sistema deve aumentar a performance em 25% quando em situações de elevada procura (>Y pedidos por período).
- **Uso Eficiente de Recursos**: Hardware deve ser utilizado de forma parcimoniosa, escalando apenas quando picos de demanda (>Y requests/período) ocorrem.
- **Modifiabilidade**: Os clientes de software não devem ser afetados por mudanças na API, exceto em casos extremos.
- **Estratégia SOA**: Adesão à estratégia corporativa de API-led connectivity para facilitar integrações.


### Requisitos Funcionais

#### US1 - Tiago Oliveira (1201360)
Como bibliotecário, quero criar um Livro, Autor e Género no mesmo processo, de forma atómica e consistente.

**Documentação detalhada**: [US1.md](ADD/US1.md) 

#### US2 - Pedro Costa (1201576)
Como bibliotecário, quero criar um Leitor e o respetivo Utilizador no mesmo pedido, garantindo atomicidade entre os microserviços de autenticação e leitores.

**Documentação detalhada**: [US2.md](ADD/US2.md)

#### US3 - Diogo Pereira (1221137)
Como leitor, quando retorno um livro, quero deixar um comentário de texto sobre o livro e avaliá-lo, de forma atómica e consistente.

**Documentação detalhada**: [US3.md](ADD/US3.md) - TODO

### Requisitos Não Funcionais

#### RNF1 - Availability
O sistema deve melhorar a sua disponibilidade.

#### RNF2 - Performance
O sistema deve aumentar o desempenho em 25% quando em situações de elevada procura (>Y pedidos por período).

#### RNF3 - Scalability
O sistema deve usar hardware de forma parcimoniosa, escalando apenas quando necessário.

#### RNF4 - Modifiability
Os clientes de software não devem ser afetados por eventuais alterações na API, exceto em casos extremos.

#### RNF5 - SOA Strategy
O sistema deve aderir à estratégia SOA da empresa, nomeadamente API-led connectivity.

---

## System-to-be

### Vista Lógica

#### Nível 1

![VL_Level1.jpg](Diagramas/Vista%20L%C3%B3gica/VL_Level1.jpg)

#### Nível 2

![VL_Level2.jpg](Diagramas/Vista%20L%C3%B3gica/VL_Level2.jpg)

#### Nível 3

![VL_Level3.jpg](Diagramas/Vista%20L%C3%B3gica/VL_Level3.jpg)

### Vista Física

#### Nível 2

![VF_Level2.jpg](Diagramas/Vista%20Física/VF_Level2.jpg)