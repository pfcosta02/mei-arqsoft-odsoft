# O sistema deve melhorar a sua disponibilidade. 

---

## Objetivo do Design

O objetivo principal deste design é aumentar a disponibilidade do sistema, garantindo que ele esteja operacional de forma contínua e eficiente. Para alcançar essa melhoria, foram implementadas estratégias que visam minimizar o tempo de inatividade, melhorar a resiliência do sistema em casos de falha e assegurar a capacidade de suportar picos de demanda.

---

## Quality Attribute Scenario

| **Elemento**          | **Descrição**                                                                                                                         |
|------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| **Estímulo**           | Falha de um microserviço, do RabbitMQ ou de uma base de dados durante operação normal.                                              |
| **Fonte do Estímulo**  | Infraestrutura (falha de rede, hardware, bugs no software, sobrecarga).                                                             |
| **Ambiente**           | Sistema em produção, operação 24/7 com carga variável.                                                                              |
| **Artefacto**          | Microserviços (Command/Query), RabbitMQ, Bases de dados H2, Kubernetes cluster.                                             |
| **Resposta**           | Sistema continua operacional com degradação mínima. Serviços falhados são detetados e reiniciados automaticamente.                  |
| **Medida da Resposta** | Uptime ≥99.9%, Recovery Time <30 segundos, nenhuma perda de mensagens ou dados.                                                     |

---

## Technical Memo

### Problema
Garantir que falhas em componentes individuais (microserviços, message broker, bases de dados) não tornem todo o sistema indisponível ou causem perda de dados. O sistema deve ser resiliente a falhas parciais e recuperar automaticamente.

### Resumo da Solução
Adotar uma arquitetura de **microserviços distribuídos** com:
- **Isolamento de Processos**: Cada serviço em container isolado
- **Redundância Ativa**: Múltiplas réplicas de cada microserviço
- **Detecção e Recuperação Automática**: Health checks e auto-restart via Kubernetes
- **Messaging Durável**: Filas persistentes no RabbitMQ com acknowledgments
- **Outbox Pattern**: Garantia de publicação de eventos mesmo após falhas

### Fatores
- Falhas podem ocorrer a qualquer momento (rede, hardware, bugs)
- Tempo de inatividade impacta utilizadores e operações críticas
- Mensagens e eventos não podem ser perdidos
- Recuperação deve ser automática e rápida
- Degradação gradual é preferível a falha total

### Solução

#### Táticas Aplicadas

**1. Isolamento de Processos**
- Cada microserviço executa em container Docker isolado
- Falha de um serviço não afeta outros bounded contexts
- Namespaces Kubernetes separados (lms-dev, lms-db)

**2. Redundância Ativa**
- **Command services**: 3 réplicas cada
- **Query services**: 5 réplicas cada (leitura >> escrita)
- **RabbitMQ**: Cluster com 3 nós (quorum queues)
- **H2**: Múltiplas instâncias em memória por réplica

**3. Detecção de Falhas**
- **Liveness probes**: Kubernetes verifica se container está "vivo" a cada 10s
- **Readiness probes**: Verifica se serviço está pronto para receber tráfego

**4. Recuperação de Falhas**
- **Auto-restart**: Kubernetes reinicia pods falhados automaticamente
- **Retry logic**: Consumidores RabbitMQ com retry + exponential backoff

**5. Messaging Durável**
- **Durable Queues**: Filas persistem em disco (sobrevivem a restart do RabbitMQ)
- **Persistent Messages**: Mensagens marcadas como `persistent=true`
- **Publisher Confirms**: RabbitMQ confirma receção antes de producer continuar
- **Consumer Acknowledgments**: Mensagens só são removidas após processamento com sucesso

**6. Outbox Pattern**
- Garante atomicidade entre escrita local (DB) e publicação de evento
- Escrita na tabela `outbox_events` na mesma transação do dado de negócio
- `OutboxProcessor` (scheduled job) publica eventos pendentes periodicamente
- Resiliente a falhas: se publicação falhar, evento fica `PENDING` para retry

### Motivação
Num sistema distribuído, falhas são inevitáveis. A arquitetura de microserviços com isolamento permite que falhas sejam contidas, evitando cascata de erros. Redundância e auto-recuperação garantem que o sistema tolera falhas de componentes individuais sem intervenção manual.

O Outbox Pattern é crítico para garantir que eventos não sejam perdidos mesmo se RabbitMQ estiver indisponível no momento da transação.

### Alternativas

| Alternativa | Descrição | Razão para Rejeição |
|-------------|-----------|---------------------|
| **Sistema Monolítico** | Toda a aplicação num único processo | Falha única derruba todo o sistema, sem isolamento |
| **Publicação Direta de Eventos** | Publicar no RabbitMQ sem Outbox | Se RabbitMQ falhar durante transação, evento perde-se |
| **Sincronização Síncrona (REST)** | Comunicação REST entre serviços | Timeouts e falhas de rede causam falhas em cascata |
| **Single Instance** | Um único pod por serviço | Zero tolerância a falhas, downtime durante deploys |

### Decisões Arquiteturais

**D1. RabbitMQ com Durable Queues + DLQ**
- **Escolha**: RabbitMQ configurado com filas duráveis e Dead Letter Queues
- **Alternativa**: Kafka (mais complexo, overkill para volume atual)
- **Razão**: Garante entrega de mensagens mesmo após restart, DLQ permite análise de falhas

**D2. Kubernetes com HPA (Horizontal Pod Autoscaler)**
- **Escolha**: Auto-scaling baseado em CPU e métricas customizadas
- **Alternativa**: VMs estáticas sem auto-scaling
- **Razão**: Permite escalar automaticamente durante picos, reduz custos em períodos baixos

**D3. Outbox Pattern para Publicação de Eventos**
- **Escolha**: Tabela `outbox_events` + scheduled publisher
- **Alternativa**: Transactional Outbox com CDC (Change Data Capture)
- **Razão**: Mais simples de implementar, adequado para volume atual

### Riscos e Mitigações

| ID | Risco | Probabilidade | Impacto | Mitigação |
|----|-------|---------------|---------|-----------|
| **R1** | Falha total do cluster RabbitMQ | Baixa | Crítico | Cluster RabbitMQ com 3 nós + quorum queues, mensagens persistem em disco |
| **R2** | Falha total do cluster Kubernetes | Baixa | Crítico | Multi-AZ deployment, backups automáticos das bases de dados |
| **R3** | Base de dados H2 perde dados (em memória) | Média | Alto | Persistência em ficheiro para produção, backups periódicos, snapshots a cada 6h |
| **R4** | OutboxProcessor falha continuamente | Baixa | Médio | Alertas de monitorização, retry com exponential backoff, DLQ para eventos problemáticos |
| **R5** | Cascata de falhas por timeout | Média | Alto | Circuit Breaker pattern (a implementar), timeouts configurados em todos os consumers |

---

## Questões Pendentes

1. **Monitorização**: Como implementar dashboard de disponibilidade em tempo real? (Prometheus + Grafana?)
2. **Testes de Caos**: Como validar recuperação automática? (Chaos Monkey em ambiente de staging?)
3. **Multi-Region**: Arquitetura atual é single-region, como expandir para multi-region no futuro?
4. **Backup/Restore**: Qual a estratégia de backup para garantir RTO <1h e RPO <15min?

---

## Referências

Este Quality Attribute é implementado nos seguintes Use Cases:
- [US2 - Criar Reader + User](../UC/US2.md): Outbox Pattern, Messaging durável
- Todos os microserviços aplicam táticas de disponibilidade (isolamento, redundância, health checks)
