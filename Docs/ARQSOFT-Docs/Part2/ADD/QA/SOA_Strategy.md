# O sistema deve aderir à estratégia SOA da empresa, nomeadamente API-led connectivity.

---

## Objetivo do Design

O objetivo principal deste design é garantir que o sistema adira à estratégia SOA (Service-Oriented Architecture) da empresa, implementando API-led connectivity através de microserviços bem definidos, com separação clara entre camadas de Experience API, Process API e System API, promovendo reutilização, composição e governança de serviços.

---

## Quality Attribute Scenario

| **Elemento**          | **Descrição**                                                                                                                         |
|------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| **Estímulo**           | Necessidade de integrar novo canal (mobile app, portal web, API pública) ou novo bounded context.                                   |
| **Fonte do Estímulo**  | Equipa de desenvolvimento, Product Owner, requisitos de integração com sistemas externos.                                           |
| **Ambiente**           | Sistema em produção, múltiplos clientes consumindo APIs (web, mobile, integrações B2B).                                             |
| **Artefacto**          | APIs REST, contratos de serviços, microserviços, message broker (RabbitMQ).                                                         |
| **Resposta**           | Novo serviço ou integração implementada reutilizando System APIs existentes, sem duplicar lógica de negócio.                        |
| **Medida da Resposta** | Tempo de desenvolvimento de nova integração <3 dias, reutilização de ≥70% de System APIs existentes.                                |

---

## Technical Memo

### Problema
Garantir que a arquitetura segue princípios SOA modernos, permitindo que diferentes canais (web, mobile, APIs públicas) consumam serviços de forma consistente, sem duplicar lógica de negócio. O sistema deve facilitar composição de serviços e evolução independente.

### Resumo da Solução
Implementar **API-led connectivity** através de três camadas lógicas:
- **System API Layer**: Microserviços de domínio (lms-readers-*, lms-authnusers-*) expõem APIs REST para operações CRUD
- **Process API Layer** (implícito): Orquestração de múltiplos System APIs via eventos (ex: US2 coordena Readers + Users)
- **Experience API Layer** (futuro): Agregação de dados para clientes específicos (web, mobile)

Comunicação entre serviços predominantemente **assíncrona via eventos** (RabbitMQ), com REST para queries síncronas.

### Fatores
- Múltiplos clientes precisam aceder aos mesmos dados (web, mobile, integrações)
- Lógica de negócio não pode estar duplicada em cada cliente
- Bounded contexts devem evoluir independentemente
- Necessidade de versionamento de APIs sem quebrar clientes existentes
- Observabilidade e governança de APIs

### Solução

#### Táticas Aplicadas

**1. Bounded Contexts como System APIs**
Cada bounded context expõe API REST bem definida:
- **Identity System API**: `/api/users` (authnusers-c/q)
- **Readers System API**: `/api/readers` (readers-c/q)
- **Catalog System API**: `/api/books`, `/api/authors`, `/api/genres` (bookgenre-c/q, authors-c/q)
- **Lendings System API**: `/api/lendings` (lendings-c/q)

**2. CQRS - Separação Command/Query**
- **Command APIs**: POST, PUT, DELETE (operações de escrita)
  - Retornam `202 Accepted` para processamento assíncrono
  - Publicam eventos via RabbitMQ
- **Query APIs**: GET (operações de leitura)
  - Maior número de réplicas (2-3 vs 1-2)

**3. Event-Driven Integration (Process API implícito)**
Em vez de Process API centralizado, **coreografia via eventos**:
- US1: BookGenre Command → `BookTempCreatedEvent` → Authors Command → `AuthorTempCreatedEvent` → BookGenre Command (fromTemptoBook) → `BookFinalizedEvent` → Authors Command (fromTemptoAuthor)
- US2: Readers Command → `ReaderTempCreatedEvent` → Users Command → `UserTempCreatedEvent` → Readers Command (persistTemporary)
- Cada serviço orquestra a sua parte do fluxo de negócio
- Desacoplamento temporal entre bounded contexts

**4. API Contracts e Versionamento**
- **DTOs bem definidos**: CreateReaderRequest, ReaderResponse, UserResponse
- **Versionamento URI** (futuro): `/v1/api/readers`, `/v2/api/readers`
- **Backward compatibility**: Novos campos opcionais, deprecated fields mantidos por 2 releases
- **Contratos de eventos**: EventSchema com versionamento (ex: `ReaderCreatedEvent_v1`)

**5. API Gateway (considerado)**
- **Não implementado atualmente**: Clientes acedem diretamente cada microserviço
- **Futuro**: Spring Cloud Gateway para:
  - Single entry point
  - Rate limiting
  - Autenticação centralizada (OAuth2/JWT)
  - Routing dinâmico

**6. Service Registry e Discovery**
- **Kubernetes Service Discovery**: DNS interno resolve `lms-readers-query.lms-dev.svc.cluster.local`
- **Sem Eureka/Consul**: Kubernetes nativo suficiente

### Motivação
API-led connectivity promove **reutilização** e **composição**. Em vez de cada cliente implementar lógica (ex: "criar reader precisa criar user"), essa orquestração está encapsulada nos serviços via eventos.

Separação System API (CRUD) vs Process API (workflows) permite que:
- System APIs sejam reutilizados em múltiplos workflows
- Novos canais (mobile app) reutilizem System APIs existentes
- Bounded contexts evoluam sem impactar outros

### Alternativas

| Alternativa | Descrição | Razão para Rejeição |
|-------------|-----------|---------------------|
| **API Gateway centralizado** | Todos os requests passam por gateway único | Overhead de latência, single point of failure, complexidade adicional |
| **Saga Orquestrada (Process API)** | Serviço dedicado orquestra workflows | Overhead para fluxos simples (2 passos), preferimos coreografia |
| **GraphQL Federation** | Gateway GraphQL agrega múltiplas APIs | Complexidade, stack diferente (REST já estabelecido) |
| **Monolito com Facade** | API única exposta, monolito interno | Não resolve escalabilidade, acoplamento interno |

### Decisões Arquiteturais

**D1. REST para System APIs, Eventos para Process Logic**
- **Escolha**: Cada bounded context expõe REST API, comunicação inter-serviços via RabbitMQ events
- **Alternativa**: REST síncrono entre serviços, gRPC
- **Razão**: REST familiar para clientes, eventos desacoplam bounded contexts

**D2. Coreografia de Eventos (sem Process API dedicado)**
- **Escolha**: Utilização de eventos temporários
- **Alternativa**: Process API orquestrador centralizado
- **Razão**: Simplicidade para workflows de 2-3 passos, evita overhead de serviço adicional

**D3. Versionamento URI (futuro)**
- **Escolha**: `/v1/api/readers`, `/v2/api/readers` quando necessário
- **Alternativa**: Header-based versioning (`Accept: application/vnd.lms.v2+json`)
- **Razão**: URI versioning mais explícito, fácil testar com curl/Postman

**D4. OAuth2/JWT para Autenticação**
- **Escolha**: JWT stateless, validado por cada microserviço independentemente
- **Alternativa**: Session-based, API keys
- **Razão**: Stateless adequado para microserviços, sem shared session store

### Riscos e Mitigações

| ID | Risco | Probabilidade | Impacto | Mitigação |
|----|-------|---------------|---------|-----------|
| **R1** | Breaking changes em APIs quebram clientes | Média | Alto | Versionamento obrigatório, backward compatibility por 2 releases, testes de contrato |
| **R2** | Falta de API Gateway dificulta observabilidade | Média | Médio | Implementar API Gateway no futuro, distributed tracing (Jaeger) |
| **R3** | Proliferação de endpoints dificulta descoberta | Baixa | Médio | Documentação OpenAPI/Swagger centralizada, service catalog |
| **R4** | Eventos duplicados causam inconsistências | Média | Alto | Idempotency keys em todos os consumers, deduplicação |
| **R5** | Falta de governança em contratos de eventos | Alta | Médio | Schema registry (futuro), validação de eventos, testes de contrato |

---

## Questões Pendentes

1. **API Gateway**: Quando implementar? Requisitos: >10 microserviços ou >5 clientes externos?
2. **OpenAPI/Swagger**: Como centralizar documentação de todas as APIs?
3. **Rate Limiting**: Necessário por API ou por tenant? Implementar onde (gateway vs cada serviço)?
4. **Contract Testing**: Usar Pact ou Spring Cloud Contract?
5. **Schema Registry**: Implementar para validação de eventos? (Confluent Schema Registry, Avro?)

---

## Referências

Este Quality Attribute é implementado nos seguintes Use Cases:
- [US1 - Criar Book + Author + Genre](../US1.md): Process logic via eventos, System APIs REST
- [US2 - Criar Reader + User](../US2.md): Process logic via eventos, System APIs REST
- [US3 - Deixar comentário e avaliação ao retornar um Book ](../US3.md): Process logic via eventos, System APIs REST
- Todos os microserviços expõem REST APIs seguindo princípios SOA
- Comunicação assíncrona via eventos implementa Process API de forma distribuída
