# As a reader, upon returning a Book, I want to leave a text comment about the Book and grading it (0-10)

---

## Objetivo do Design

O objetivo principal deste design é fornecer a visão para a criação de um **Leitor (Reader)** e do respetivo **Utilizador (User)**, permitindo que ambos sejam geridos no mesmo processo, garantindo consistência nos dados, segurança na autenticação e eficiência operacional.

---

## Cenários de Atributos de Qualidade

| **Elemento**          | **Descrição**                                                                                                                   |
|------------------------|---------------------------------------------------------------------------------------------------------------------------------|
| **Estímulo**           | O leitor faz um PATCH numa devolução de lending, incluindo comentário (texto livre) e classificação (0-10).                     |
| **Fonte do Estímulo**  | Cliente HTTP (via API REST) autenticado como um leitor que é proprietário do lending.                                           |
| **Ambiente**           | Sistema em produção, acessível por API REST; RabbitMQ disponível para publicação de eventos.                                    |
| **Artefacto**          | Módulos de gestão de dados de Lending e autenticação.                                                                           |
| **Resposta**           | Lending atualizado com returnedDate, commentary e rating. Evento publicado em Outbox na mesma transação. Em caso de erro, transação revertida. |
| **Medida da Resposta** | Conclusão em menos de 1 segundos e sem inconsistências entre entidades.                                                         |

---

## Memorando Técnico

### Problema
Na sprint anterior, foi desenvolvida a aplicação LMS com arquitetura monolítica. Apesar de funcional, esta abordagem centralizada limita **desempenho, disponibilidade, escalabilidade e elasticidade**, tornando difícil lidar com picos de carga e evoluir funcionalidades de forma independente.

### Resumo da Solução
Adotar uma arquitetura **descentralizada/distribuída baseada em microserviços**, aplicando:
- **Messaging (RabbitMQ)** para comunicação assíncrona entre serviços e para acionar compensações em caso de falha.
- **Outbox** para publicação confiável de eventos, garantindo atomicidade entre escrita local e envio da mensagem.
- **CQRS** para separar responsabilidades de leitura e escrita, permitindo otimizar consultas de Lendings, que são mais frequentes que operações de escrita.

### Fatores
- Validação de entrada (rating 0-10, comentário opcional). 
- Transação única que atualiza lending e cria evento em Outbox. 
- Suporte a versionamento optimista via If-Match para evitar race conditions. 
- Publicação confiável de eventos (Outbox Pattern + Polling). 

### Solução
- **Messaging**: Utilizar RabbitMQ para troca de eventos e compensações.
- **Outbox**: Garantir publicação atómica dos eventos de criação.
- **CQRS**: Separar operações de leitura e escrita para otimizar consultas.

### Motivação
A utilização de UPDATE nativo + refresh garante que a transação é única e atómica, evitando lógica 
bifurcada. O padrão Outbox assegura que o evento é publicado de forma confiável, mesmo em caso de 
falha do broker. O suporte a versionamento optimista (If-Match) previne conflitos de concorrência 
sem recurso a locks pessimistas.

### Alternativas
- **Sistema Monolítico**: Um único serviço com todas as funcionalidades (descartado por falta de escalabilidade e isolamento).

### Questões Pendentes
- Como garantir que as compensações sejam tratadas corretamente em todos os serviços?
- Como lidar com falhas no broker durante a publicação de eventos?
- Como validar a propriedade do lending (apenas o leitor proprietário pode devolver)?
- Rating deve permitir valores decimais ou apenas inteiros? 
- Qual é o tamanho máximo do comentário? 
- Como notificar o leitor da confirmação de devolução?

---

Vista de Processo

```mermaid
    autonumber

    actor Reader
    participant LendingController
    participant LendingService
    participant LendingRepository
    participant EntityManager
    participant OutboxRepository
    participant MessageBroker
    participant OutboxPoller

    Reader ->> LendingController: PATCH /api/lendings/{year}/{seq}
    activate LendingController

    LendingController ->> LendingController: Validar If-Match header
    LendingController ->> LendingController: Extrair userId do Authentication
    LendingController ->> LendingController: Validar feature flag

    LendingController ->> LendingService: returnLending(lendingNumber, commentary, rating, expectedVersion)
    activate LendingService

    LendingService ->> LendingRepository: returnLendingAndGetUpdated(...)
    activate LendingRepository

    LendingRepository ->> LendingRepository: markReturned (UPDATE nativo com versionamento)
    LendingRepository ->> EntityManager: refresh(managedEntity)

    alt Versão mismatch
        LendingRepository -->> LendingService: OptimisticLockException
    else Sucesso
        LendingRepository -->> LendingService: Lending (com commentary, rating atualizados)
    end
    deactivate LendingRepository

    LendingService ->> OutboxRepository: save(LendingOutbox)
    activate OutboxRepository
    OutboxRepository ->> OutboxRepository: Persiste evento com published=false
    deactivate OutboxRepository

    LendingService ->> LendingController: Retorna Lending atualizado
    deactivate LendingService

    LendingController ->> Reader: 200 OK + ETag + LendingView
    deactivate LendingController

    par Assincronia
        OutboxPoller ->> OutboxRepository: findByPublishedFalse()
        OutboxPoller ->> MessageBroker: convertAndSend(lendingsExchange, LENDING_RETURNED, payload)
        OutboxPoller ->> OutboxRepository: save(event com published=true)
        OutboxPoller ->> OutboxRepository: Marca como publicado
```