# O sistema deve aumentar a sua performance em 25% em situações de elevada procura (>Y pedidos por período).

---

## Objetivo do Design

O objetivo principal deste design é garantir que o sistema responda rapidamente às requisições dos utilizadores, especialmente operações de leitura (que representam >80% do tráfego), e que consiga processar pelo menos 25% mais pedidos em situações de elevada procura comparativamente à arquitetura monolítica anterior.

---

## Quality Attribute Scenario

| **Elemento**          | **Descrição**                                                                                                         |
|------------------------|-----------------------------------------------------------------------------------------------------------------------|
| **Estímulo**           | Utilizador executa operação de consulta (ex: listar readers, pesquisar livros) ou operação de escrita (criar author). |
| **Fonte do Estímulo**  | Bibliotecário ou utilizador final através da API REST.                                                                |
| **Ambiente**           | Sistema em produção com carga normal (100-500 req/s) e picos (até 1000 req/s).                                        |
| **Artefacto**          | Query services (leitura), Command services (escrita), RabbitMQ.                                          |
| **Resposta**           | Sistema responde com latência baixa e throughput elevado, mesmo durante picos de carga.                               |
| **Medida da Resposta** | Latência p95 <200ms para queries, <500ms para commands, Throughput ≥500 req/s por serviço query.                      |

---

## Technical Memo

### Problema
Garantir que o sistema responde rapidamente às requisições dos utilizadores, especialmente operações de leitura (que representam >80% do tráfego). Operações de escrita devem ser processadas de forma assíncrona para não bloquear utilizador.

### Resumo da Solução
Aplicar padrão **CQRS** para otimizar leituras e escritas separadamente, combinado com:
- **Comunicação Assíncrona**: Desacoplar operações de escrita da resposta ao utilizador
- **Outbox Pattern**: Publicação de eventos sem bloquear transação principal
- **Database per Service**: Evitar contenção entre bounded contexts

### Fatores
- Leituras são mais frequentes que escritas (~80/20)
- Utilizadores esperam respostas rápidas (<1s)
- Operações de escrita podem ser assíncronas (eventual consistency)
- Picos de carga durante horários de abertura da biblioteca

### Solução

#### Táticas Aplicadas

**1. CQRS (Command Query Responsibility Segregation)**
- **Command side**: Serviços otimizados para escrita (validações, lógica de negócio)
  - readers-c, Authors_C, lendings_c
  - Normalização da base de dados, integridade referencial
- **Query side**: Serviços otimizados para leitura (denormalização, vistas materializadas)
  - readers-q, Authors_Q, lendings_q
  - Modelos de leitura simplificados, índices otimizados
- **Sincronização**: Via eventos RabbitMQ (eventual consistency)

**3. Comunicação Assíncrona**
- **Messaging (RabbitMQ)**: Operações de escrita publicam eventos e retornam imediatamente
- **202 Accepted**: Command services retornam status code 202 (processamento assíncrono)
- **Eventual consistency**: Query models atualizados em background (latência <100ms típica)

**4. Outbox Pattern**
- Publicação de eventos não bloqueia transação principal
- `OutboxProcessor` scheduled job publica em background
- Sem overhead de 2PC (two-phase commit)

**5. Database per Service**
- Cada bounded context tem sua própria base de dados
- Elimina contenção entre serviços
- Permite otimizações específicas (índices, partitioning)

**6. Connection Pooling**
- HikariCP configurado com pool size adequado (10-20 conexões por pod)
- Reduz overhead de criação de conexões
- Timeout configurations para evitar conexões penduradas

### Motivação
CQRS permite otimizar leituras e escritas de forma independente. Query services podem usar denormalização e caches agressivos sem impactar integridade dos dados (mantida no Command side).

Comunicação assíncrona desacopla performance do utilizador da performance de processamento interno. Utilizador recebe resposta rápida (202 Accepted) enquanto processamento ocorre em background.

### Alternativas

| Alternativa | Descrição | Razão para Rejeição |
|-------------|-----------|---------------------|
| **CRUD tradicional** | Mesma base de dados para leitura e escrita | Queries complexas impactam escritas, difícil otimizar ambos |
| **Comunicação Síncrona REST** | Command service chama User service via REST | Latência acumulada, timeouts, acoplamento temporal |
| **Shared Database** | Todos os serviços partilham mesma BD | Contenção, locks, acoplamento forte |

### Decisões Arquiteturais

**D1. CQRS com Sincronização por Eventos**
- **Escolha**: Command e Query separados, sincronizados via RabbitMQ
- **Alternativa**: CQRS com Event Sourcing completo
- **Razão**: Event Sourcing adiciona complexidade desnecessária para requisitos atuais

**D2. RabbitMQ para Messaging**
- **Escolha**: RabbitMQ AMQP
- **Alternativa**: Kafka (mais throughput), Redis Streams
- **Razão**: RabbitMQ mais simples, suficiente para volume atual (<10k msg/s)

[//]: # (### Métricas de Performance)

[//]: # ()
[//]: # (| Operação | Target | Current &#40;Baseline&#41; | Otimização |)

[//]: # (|----------|--------|-------------------|------------|)

[//]: # (| **GET /api/readers** &#40;lista&#41; | <100ms p95 | 250ms | Redis cache → <50ms |)

[//]: # (| **GET /api/readers/{id}** | <50ms p95 | 80ms | Redis cache → <20ms |)

[//]: # (| **POST /api/readers** | <500ms p95 | 800ms | Async + Outbox → <200ms |)

[//]: # (| **Search queries** | <200ms p95 | 400ms | Índices + cache → <100ms |)

[//]: # (### Riscos e Mitigações)

[//]: # ()
[//]: # (| ID     | Risco | Probabilidade | Impacto | Mitigação |)

[//]: # (|--------|-------|---------------|---------|-----------|)

[//]: # (| **R1** | Lag elevado na sincronização CQRS | Baixa | Médio | Monitorizar latência eventos, alertas se >500ms, scaling RabbitMQ |)

[//]: # (| **R2** | Query model desincronizado | Baixa | Alto | Reconciliation job diário, monitorização de discrepâncias |)

[//]: # (---)

## Questões Pendentes

1. **Rate limiting**: Necessário implementar para prevenir abuso de APIs?
2. **CDN**: Considerar CDN para assets estáticos (fotos de readers)?
3. **Database tuning**: Quais índices adicionais criar nas query databases?

---

## Referências

Este Quality Attribute é implementado nos seguintes Use Cases:
- [US1 - Criar Book + Author + Genre](../US1.md): CQRS, Async messaging, Outbox Pattern
- [US2 - Criar Reader + User](../US2.md): CQRS, Async messaging, Outbox Pattern
- [US3 - Deixar comentário e avaliação ao retornar um Book ](../US3.md): CQRS, Async messaging, Outbox Pattern