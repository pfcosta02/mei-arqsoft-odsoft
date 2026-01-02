# Os clientes de software não devem ser afectados por eventuais alterações na API, excepto em casos extremos.

---

## Objetivo do Design

O objetivo principal deste design é garantir que o sistema seja fácil de modificar e evoluir ao longo do tempo, permitindo adicionar novas funcionalidades, alterar regras de negócio ou trocar tecnologias sem impactar clientes existentes. Alterações devem ser localizadas (baixo coupling), testáveis independentemente e deploy-friendly (continuous delivery).

---

## Quality Attribute Scenario

| **Elemento**          | **Descrição**                                                                                                                         |
|------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| **Estímulo**           | Necessidade de adicionar nova funcionalidade, alterar regra de negócio ou trocar tecnologia (ex: mudar de PostgreSQL para MongoDB).  |
| **Fonte do Estímulo**  | Equipa de desenvolvimento, Product Owner, evolução de requisitos de negócio.                                                        |
| **Ambiente**           | Desenvolvimento e manutenção contínua, múltiplas equipas a trabalhar em paralelo.                                                    |
| **Artefacto**          | Código fonte, bounded contexts, interfaces entre serviços, configurações.                                                            |
| **Resposta**           | Alteração implementada sem impactar outros bounded contexts, com testes e deploy independente.                                       |
| **Medida da Resposta** | <2 dias para adicionar nova entidade, <1 dia para alterar regra de negócio, zero downtime em deploys.                               |

---

## Technical Memo

### Problema
Sistema deve ser fácil de modificar e evoluir ao longo do tempo. Alterações devem ser localizadas (baixo coupling), testáveis independentemente e deploy-friendly (continuous delivery).

### Resumo da Solução
Aplicar **Domain-Driven Design (DDD)** com **Bounded Contexts** isolados, combinado com:
- **Onion Architecture**: Separação clara de responsabilidades internas
- **Microserviços**: Cada bounded context é um serviço independente
- **Database per Service**: Alterações de schema isoladas
- **Interfaces bem definidas**: Comunicação via eventos e REST APIs

### Fatores
- Múltiplas equipas trabalhando simultaneamente
- Requisitos de negócio evoluem frequentemente
- Necessidade de A/B testing e feature flags
- Deploy contínuo sem downtime

### Solução

#### Táticas Aplicadas

**1. Separação de Concerns (Bounded Contexts)**
- **Identity Context** (lms-authnusers): Gestão de Users, autenticação OAuth2/JWT
- **Readers Context** (lms-readers): Gestão de Readers (entidade de negócio)
- **Catalog Context**: Gestão de Books, Authors, Genres (futuro)
- **Lendings Context**: Gestão de empréstimos (futuro)

Cada contexto pode evoluir independentemente.

**2. Onion Architecture (Estrutura Interna)**
Cada microserviço segue camadas concêntricas:

```
api/ (controllers, REST endpoints)
  ↓
services/ (lógica de negócio)
  ↓
model/ (entidades de domínio)
  ↑
repositories/ (interfaces de persistência)
  ↑
infrastructure/ (implementações JPA, RabbitMQ publishers)
```

**Regras de dependência:**
- Camadas internas (model) **não dependem** de externas
- Camadas externas (infrastructure) **dependem** de interfaces definidas em camadas internas
- **Use interfaces**: Repositories, Publishers são interfaces implementadas em infrastructure

**3. Database per Service**
- Cada bounded context tem seu próprio schema
- Alterações de schema não impactam outros serviços
- Permite escolher tecnologia de persistência adequada (PostgreSQL, MongoDB, etc.)

**4. API Contracts (Event Schemas)**
- Eventos RabbitMQ com schema versionado (JSON Schema ou Avro)
- **Backward compatibility**: Novos campos opcionais
- **Deprecated fields**: Mantidos por pelo menos 2 releases

**5. Defer Binding (Configuração Externa)**
- **Spring Profiles**: dev, staging, prod
- **ConfigMaps Kubernetes**: Configurações externalizadas
- **Environment variables**: Credentials, endpoints
- Permite trocar implementações sem recompilação

**6. Use Interfaces (Dependency Inversion)**
Exemplo em `lms-readers-command`:
```java
// Camada services
public interface ReaderRepository {
    Reader save(Reader reader);
    Optional<Reader> findById(String id);
}

// Camada infrastructure
@Repository
public class ReaderRepositoryJpaImpl implements ReaderRepository {
    // Implementação JPA
}
```

Trocar JPA por MongoDB requer apenas nova implementação, sem alterar services.

### Motivação
Onion Architecture garante que lógica de negócio (model, services) é independente de frameworks (Spring, JPA). Isso permite:
- Testar lógica sem infraestrutura (unit tests puros)
- Trocar tecnologias sem reescrever negócio
- Evoluir modelo de domínio sem quebrar APIs

Bounded Contexts isolados permitem que equipas trabalhem em paralelo sem merge conflicts ou acoplamento.

### Alternativas

| Alternativa | Descrição | Razão para Rejeição |
|-------------|-----------|---------------------|
| **Layered Architecture** | Camadas tradicionais (Controller → Service → DAO) | Forte acoplamento com frameworks, dificulta testes |
| **Shared Database** | Múltiplos serviços acessam mesma BD | Alterações de schema requerem coordenação de todas equipas |
| **Monorepo sem boundaries** | Todo código num único repositório sem separação | Acoplamento acidental, difícil testar isoladamente |
| **Direct REST calls** | Serviços chamam-se mutuamente via REST | Acoplamento temporal, dificulta versionamento |

### Decisões Arquiteturais

**D1. Onion Architecture para Estrutura Interna**
- **Escolha**: Model → Services → Infrastructure → API
- **Alternativa**: Layered Architecture, Hexagonal Architecture (Ports & Adapters)
- **Razão**: Onion é mais simples que Hexagonal, mais focado em DDD que Layered

**D2. Event-Driven Communication**
- **Escolha**: Eventos RabbitMQ para comunicação entre bounded contexts
- **Alternativa**: REST síncrono, gRPC
- **Razão**: Desacoplamento temporal, versionamento mais fácil (novos consumers não quebram producers)

**D3. Database per Service**
- **Escolha**: PostgreSQL separado por bounded context
- **Alternativa**: Schema separation (shared database), NoSQL per service
- **Razão**: Isolamento completo, permite escolher tecnologia adequada por contexto

### Exemplo de Modificabilidade

**Cenário**: Adicionar novo campo `birthDate` em Reader

| Camada | Alteração | Impacto |
|--------|-----------|---------|
| **Model** | Adicionar `private LocalDate birthDate;` | Isolado em Reader entity |
| **Repository** | Nenhuma alteração (JPA automático) | Zero |
| **Service** | Validação de idade em `ReaderService.create()` | Isolado em service layer |
| **API** | Adicionar campo em `CreateReaderRequest` DTO | Backward compatible (opcional) |
| **Event** | Adicionar `birthDate` em `ReaderCreatedEvent` | Consumers antigos ignoram campo |
| **Database** | Migration SQL: `ALTER TABLE readers ADD COLUMN birth_date DATE;` | Isolado em Readers DB |

**Resultado**: Alteração em 1-2 dias, sem impacto em outros bounded contexts.

### Riscos e Mitigações

| ID | Risco | Probabilidade | Impacto | Mitigação |
|----|-------|---------------|---------|-----------|
| **R1** | Breaking changes em eventos | Média | Alto | Schema registry, versionamento de eventos, testes de contrato |
| **R2** | Acoplamento acidental entre bounded contexts | Média | Alto | Code reviews rigorosos, arquitetura fitness functions, dependency analysis |
| **R3** | Duplicação de código entre serviços | Alta | Baixo | Aceitável (prefer duplication over coupling), shared libraries apenas para utils |
| **R4** | Complexidade de debugging em eventos | Média | Médio | Distributed tracing (Jaeger), correlation IDs em todos os eventos |
| **R5** | Divergência de estilos entre equipas | Alta | Baixo | Code style guides, linters, templates de projeto |

---

## Questões Pendentes

1. **Schema Registry**: Implementar Confluent Schema Registry para validação de eventos?
2. **API Gateway**: Adicionar API Gateway para versionamento centralizado de APIs?
3. **Shared Kernel**: Quais classes podem ser partilhadas (ex: Value Objects comuns)?
4. **Contract Testing**: Usar Pact ou Spring Cloud Contract?

---

## Referências

Este Quality Attribute é implementado nos seguintes Use Cases:
- [US2 - Criar Reader + User](../UC/US2.md): Bounded contexts isolados, Onion Architecture
- Todos os microserviços seguem Onion Architecture para facilitar modificações
