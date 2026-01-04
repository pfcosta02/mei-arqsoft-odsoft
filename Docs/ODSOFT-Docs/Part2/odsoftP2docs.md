# Documentação de ODSOFT (Organização de Software)

## Análise às Pipelines

### Checkout
O stage Checkout é responsável por realizar a obtenção do código-fonte do repositório, garantindo que a pipeline tenha acesso à versão mais recente do projeto para execução dos próximos passos.

### Set Maven Path
Configura o Maven (versão 3.9.11) no PATH do agente Jenkins, assegurando que todos os comandos Maven utilizem a versão correta da ferramenta.

### Clean + Build
Executa o ciclo inicial do Maven para limpar artefatos antigos e compilar o projeto, validando se o código compila corretamente antes de avançar no pipeline.

### Consumer Contract Tests (Pact)
Executa testes de contrato utilizando Pact, garantindo que o serviço esteja em conformidade com os contratos esperados pelos consumidores (modo producer).

### Package
Empacota a aplicação e extrai a versão do projeto (PROJECT_VERSION), que será usada para versionar a imagem Docker.

### Build Docker Image
Cria a imagem Docker da aplicação utilizando a versão gerada pelo Maven, seguindo o padrão:
<docker-registry>/authnusers-c:<versão>

### Push Docker Image
Publica a imagem Docker no registry configurado, tornando-a disponível para deploy no Kubernetes.

### Deploy
Garante a existência da infraestrutura base no Kubernetes (namespace, services e recursos compartilhados).

Se a estratégia escolhida for Blue/Green:
- Identifica a cor atualmente ativa (blue ou green)
- Define a nova cor de deploy
- Atualiza o deployment da nova cor com a nova imagem
- Aguarda o deployment ficar pronto antes de prosseguir

### Test Instance
Executa testes de validação (smoke tests / health checks) na nova instância, antes de qualquer tráfego de produção ser direcionado para ela.
Se os testes falharem, o tráfego não é alterado, garantindo segurança total.

### Switch Traffic to New Instance
Atualiza o Service do Kubernetes para redirecionar o tráfego de produção de forma atômica para a nova versão (blue ou green), garantindo zero downtime.

### Undeploy Old Service
Remove o deployment antigo (cor anterior) após a confirmação de que a nova versão está ativa e funcionando corretamente.
