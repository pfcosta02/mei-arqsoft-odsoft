# Estratégias de Gestão de Features

Este documento descreve as estratégias de release implementadas no sistema: **Kill Switch**, **Beta Access**, **Gradual Release** e **Dark Launch**.

---

## 🛑 Kill Switch

Permite desativar instantaneamente uma funcionalidade para todos os utilizadores sem necessidade de redeploy.

### Endpoints

**Ativar Kill Switch** (desativa a funcionalidade para todos):
```
POST /admin/feature/killSwitch/activate
```

**Desativar Kill Switch** (reativa a funcionalidade):
```
POST /admin/feature/killSwitch/deactivate
```

### Comportamento
- Quando ativado, o método `isFeatureEnabledForUser()` retorna `false` para todos os utilizadores
- Tem prioridade sobre todas as outras estratégias (incluindo beta users)

### Testes e Evidências

Foram realizados dois testes:
1. Nova funcionalidade com Kill Switch ativo
2. Nova funcionalidade com Kill Switch desativado (utilizador incluído nos beta users)

Verificou-se que, como esperado:
- Com o Kill Switch ativo, o endpoint da nova funcionalidade retorna "feature not available for your user"
- Com o Kill Switch desativado e o utilizador incluído nos beta users, o acesso ao endpoint é permitido e o resultado é apresentado

**Com Kill Switch Ativo:**

![Kill Switch Ativo.png](imagens%2FKillSwitchActive.png)

**Com Kill Switch Desativo:**

![Kill Switch Desativo.png](imagens%2FKillSwitchDeactive.png)

---

## 📊 Gradual Release

Controla a percentagem de utilizadores com acesso à funcionalidade através de um rollout gradual.

### Endpoint

```
POST /admin/feature/rollout?percentage=<0-100>
```

### Exemplos
- `percentage=10` → 10% dos utilizadores têm acesso
- `percentage=50` → 50% dos utilizadores têm acesso
- `percentage=100` → Todos os utilizadores têm acesso (General Availability)
- `percentage=0` → Apenas beta users têm acesso

### Comportamento
- Seleção determinística baseada em hash do userId
- Beta users têm sempre acesso garantido independentemente da percentagem configurada
- O mesmo utilizador mantém sempre o mesmo estado (com ou sem acesso) para um dado valor de rollout

### Testes e Evidências

Foram realizados testes com diferentes configurações de rollout:
1. Rollout configurado a 100% com utilizador normal (não incluído nos Beta Users)

Verificou-se que, como esperado:
- Com rollout a 100%, todos os utilizadores (incluindo normais) têm acesso à funcionalidade

**Rollout a 100% + Utilizador Normal:**

![100% Rollout + Normal User.png](imagens%2FKillSwitchDeactive.png)

---

## 👥 Beta Access

Permite acesso exclusivo a utilizadores pré-definidos (beta testers), independentemente da configuração de rollout.

### Implementação
Lista estática de beta users definida no código (exemplo: `maria@gmail.com`).

### Comportamento
- Beta users têm acesso garantido à funcionalidade mesmo com rollout configurado a 0%
- Se o Kill Switch estiver ativo, os beta users também perdem acesso (Kill Switch tem prioridade máxima)

### Testes e Evidências

Foram realizados três testes:
1. Utilizador presente na lista de Beta Users com rollout a 0%
2. Utilizador **não** presente na lista de Beta Users com rollout a 0%
3. Utilizador Beta User com Kill Switch ativo

Verificou-se que, como esperado:
- Apesar do rollout ser 0%, o Beta User tem acesso à funcionalidade
- Um utilizador normal não tem acesso com rollout a 0%
- Mesmo sendo Beta User, o acesso é negado quando o Kill Switch está ativo

**Rollout a 0% + Beta User:**

![Kill Switch Desativo + 0% Rollout + Beta User.png](imagens%2FKillSwitchDeactive.png)

**Rollout a 0% + Utilizador Normal:**

![Kill Switch Ativo + 0% Rollout + Normal User.png](imagens%2FKillSwitchActive.png)

**Kill Switch Ativo + Beta User:**

![Kill Switch Ativo + Beta User.png](imagens%2FKillSwitchActive.png)

---

## 🌑 Dark Launch

Executa código em produção sem apresentar o resultado ao utilizador. Permite recolher métricas de forma invisível para validar comportamento antes do lançamento público.

### Diferença entre Feature Flag e Dark Launch

| Aspeto | Feature Flag | Dark Launch |
|--------|-------------|-------------|
| Execução do código | Apenas se habilitada | Sempre executado |
| Visibilidade ao utilizador | Utilizador vê o resultado | Utilizador **não** vê o resultado |
| Objetivo | Controlar acesso | Recolher métricas e validar comportamento |

### Endpoints

**Ativar Dark Launch:**
```
POST /admin/feature/darkLaunch/activate
```

**Desativar Dark Launch:**
```
POST /admin/feature/darkLaunch/deactivate
```

**Consultar métricas recolhidas:**
```
GET /admin/feature/darkLaunch/metrics
```

Exemplo de resposta:
```json
{
  "createReaderAdvanced": 1523
}
```

**Reiniciar métricas:**
```
POST /admin/feature/darkLaunch/metrics/reset
```

### Comportamento
1. **Dark Launch ativado:** A funcionalidade é executada em background e as métricas são incrementadas, mas o resultado não é apresentado ao utilizador
2. **Dark Launch desativado:** A funcionalidade executa normalmente e o resultado é apresentado ao utilizador

### Testes e Evidências

Foram realizados dois testes para validar o comportamento do Dark Launch:
1. Dark Launch desativado e consulta de métricas
2. Dark Launch ativado, execução da funcionalidade e consulta de métricas

Verificou-se que, como esperado:
- Com o Dark Launch desativado, não foi executado código adicional em produção e, portanto, não existem métricas de Dark Launch
- Com o Dark Launch ativado, o código adicional foi executado de forma invisível ao utilizador e as métricas foram recolhidas (validações de domínio de email e formato de telefone)

**Dark Launch Desativado + Consulta de Métricas:**

![Dark Launch desativo + Metricas.png](imagens%2FDarkLaunchDeactivate.png)

**Dark Launch Ativado + Consulta de Métricas:**

![Dark Launch ativo + Metricas.png](imagens%2FDarkLaunchActive.png)

---

## 📋 Resumo de Endpoints

| Estratégia | Endpoint | Método | Descrição |
|------------|----------|--------|-----------|
| Kill Switch ON | `/admin/feature/killSwitch/activate` | POST | Desativa funcionalidade para todos |
| Kill Switch OFF | `/admin/feature/killSwitch/deactivate` | POST | Reativa funcionalidade |
| Rollout | `/admin/feature/rollout?percentage=<0-100>` | POST | Define percentagem de utilizadores com acesso |
| Dark Launch ON | `/admin/feature/darkLaunch/activate` | POST | Ativa execução invisível |
| Dark Launch OFF | `/admin/feature/darkLaunch/deactivate` | POST | Desativa execução invisível |
| Consultar Métricas | `/admin/feature/darkLaunch/metrics` | GET | Retorna métricas recolhidas |
| Reiniciar Métricas | `/admin/feature/darkLaunch/metrics/reset` | POST | Reinicia contadores de métricas |
