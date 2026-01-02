# O sistema deve utilizar hardware de forma parcimoniosa, escalando apenas quando necessário.

---

## Objetivo do Design

O objetivo principal deste design é garantir que o sistema escale automaticamente recursos computacionais (CPU, memória, número de instâncias) apenas quando a carga real exigir, evitando desperdício de hardware e custos desnecessários em períodos de baixa utilização. O sistema deve suportar crescimento orgânico sem necessidade de redesign da arquitetura.

---

## Quality Attribute Scenario

| **Elemento**          | **Descrição**                                                                                                                         |
|------------------------|---------------------------------------------------------------------------------------------------------------------------------------|
| **Estímulo**           | Aumento de carga no sistema (número de utilizadores, requests/segundo, volume de dados).                                            |
| **Fonte do Estímulo**  | Crescimento orgânico (mais bibliotecas usam sistema), picos sazonais (início de semestre letivo).                                   |
| **Ambiente**           | Sistema em produção, carga pode variar de 100 req/s (normal) até 2000 req/s (pico).                                                 |
| **Artefacto**          | Microserviços, Kubernetes cluster, RabbitMQ, bases de dados H2.                                                                        |
| **Resposta**           | Sistema escala automaticamente adicionando recursos, mantendo performance e disponibilidade.                                         |
| **Medida da Resposta** | Auto-scale de 0 a 10 pods em <2 minutos, suportar 10x carga base sem degradação, custo proporcional à carga.                        |

---

## Technical Memo

### Problema
Sistema deve suportar crescimento de utilizadores e carga sem necessidade de redesign da arquitetura. Escalabilidade deve ser **horizontal** (adicionar mais instâncias) e **elástica** (aumentar/diminuir automaticamente).

### Resumo da Solução
Arquitetura de **microserviços stateless** com:
- **Auto-escalonamento horizontal**: Kubernetes HPA (Horizontal Pod Autoscaler)
- **Database per Service**: Cada serviço escala independentemente
- **CQRS**: Escalar Query e Command sides separadamente
- **Messaging assíncrono**: RabbitMQ absorve picos de carga

### Fatores
- Carga varia significativamente (horários, sazonalidade)
- Budget limitado → custo deve ser proporcional à carga real
- Diferentes bounded contexts têm diferentes necessidades de escala
- Leituras >> Escritas (ratio 80/20)

### Solução

#### Táticas Aplicadas

**1. Serviços Stateless**
- Nenhum estado armazenado localmente nos pods
- Sessions geridas por JWT (stateless authentication)
- Uploads temporários em storage partilhado (não em disco local)
- Permite adicionar/remover réplicas sem perda de dados

**2. Horizontal Pod Autoscaler (HPA)**
- **Métricas**: CPU utilization (target 70%), custom metrics (requests/sec)
- **Query services**: Min 2, Max 10 réplicas
- **Command services**: Min 1, Max 5 réplicas
- **Scale up**: Quando CPU >70% por 30s consecutivos
- **Scale down**: Quando CPU <50% por 5 minutos

**3. Database per Service**
- Cada bounded context tem sua própria base de dados
- Elimina bottleneck de shared database
- Permite escalar BD independentemente (vertical ou horizontal)
- **Readers DB**: Read replicas para queries pesadas
- **Users DB**: Menor carga, instância menor

**4. CQRS - Escalabilidade Assimétrica**
- **Query side**: Mais réplicas (5 pods), read replicas de BD, cache Redis
- **Command side**: Menos réplicas (3 pods), master database apenas
- Otimização de recursos baseada em workload real

**5. Comunicação Assíncrona**
- RabbitMQ atua como **buffer** durante picos de carga
- Filas absorvem burst de mensagens
- Consumers processam a seu próprio ritmo
- Evita sobrecarga em cascata

**6. Partitioning de Dados**
- **Sharding por bounded context**: Cada serviço gere seu subset de dados
- Evita hot partitions
- Preparado para sharding horizontal futuro (ex: readers por região geográfica)

### Motivação
Microserviços stateless permitem escalabilidade horizontal ilimitada. HPA garante que recursos são alocados apenas quando necessário, otimizando custos.

Database per Service é crítico: em arquiteturas partilhadas, BD torna-se bottleneck mesmo com aplicação escalada.

### Alternativas

| Alternativa | Descrição | Razão para Rejeição |
|-------------|-----------|---------------------|
| **Escalabilidade Vertical** | Aumentar CPU/RAM de servidores | Limitado por hardware máximo, custo exponencial, downtime |
| **Shared Database** | Todos os serviços na mesma BD | BD torna-se bottleneck, dificulta escala independente |
| **Serviços Stateful** | Estado local em cada pod | Impossibilita scale-down sem perda de dados, complexidade de sincronização |
| **Synchronous REST** | Comunicação síncrona entre serviços | Backpressure em cascata, timeouts durante picos |

### Decisões Arquiteturais

**D1. Kubernetes HPA com Métricas Customizadas**
- **Escolha**: HPA baseado em CPU + requests/min (via Prometheus adapter)
- **Alternativa**: Auto-scaling manual, KEDA (Kubernetes Event Driven Autoscaling)
- **Razão**: HPA integrado no K8s, simples de configurar, adequado para workload atual

**D2. Database Read Replicas para Query Services**
- **Escolha**: PostgreSQL com 1 master + 2 read replicas
- **Alternativa**: Database clustering (Patroni), NoSQL database
- **Razão**: Simplicidade, suporta read scaling sem complexidade de clusters

**D3. RabbitMQ com Múltiplas Queues**
- **Escolha**: Queue separada por bounded context e tipo de evento
- **Alternativa**: Single queue, Kafka partitions
- **Razão**: Isolamento, permite scaling independente de consumers

### Plano de Escalabilidade

| Componente | Estado Atual | Carga 2x | Carga 5x | Carga 10x |
|------------|--------------|----------|----------|-----------|
| **Query Services** | 5 pods | 10 pods | 20 pods | 40 pods + Redis cluster |
| **Command Services** | 3 pods | 5 pods | 10 pods | 15 pods |
| **RabbitMQ** | 3 nós | 3 nós | 5 nós | 7 nós + partitioning |
| **H2 Database** | Em memória | File-based | Sharding | Migração para PostgreSQL/MySQL |
| **Redis** | 1 instance | 3 nós cluster | 5 nós cluster | 7 nós + sharding |

**Legenda**: M=Master, R=Read Replica

### Riscos e Mitigações

| ID | Risco | Probabilidade | Impacto | Mitigação |
|----|-------|---------------|---------|-----------|
| **R1** | Database H2 torna-se bottleneck | Média | Alto | Cache Redis agressivo, sharding futuro, migração para PostgreSQL se necessário |
| **R2** | RabbitMQ não consegue processar picos | Baixa | Alto | Clustering RabbitMQ, monitorização de queue depth, alertas |
| **R3** | Custo de infraestrutura cresce descontrolado | Média | Médio | Limites de HPA (max pods), alertas de custo, scale-down agressivo |
| **R4** | Lag na sincronização CQRS durante picos | Média | Médio | Scaling de consumers, priorização de mensagens críticas |
| **R5** | Esgotamento de recursos Kubernetes | Baixa | Crítico | Resource limits configurados, multi-AZ deployment, cluster autoscaler |

---

## Questões Pendentes

1. **Database Sharding**: Quando migrar para sharding horizontal? (>10M readers?)
2. **Multi-Region**: Estratégia para expandir para múltiplas regiões geográficas?
3. **Cost optimization**: Como otimizar custos em períodos de baixa carga? (scale-to-zero?)
4. **Load testing**: Qual ferramenta usar para validar escalabilidade? (k6, JMeter, Gatling?)

---

## Referências

Este Quality Attribute é implementado nos seguintes Use Cases:
- [US2 - Criar Reader + User](../UC/US2.md): CQRS permite escala assimétrica, messaging absorve picos
- Todos os microserviços são stateless e suportam HPA
