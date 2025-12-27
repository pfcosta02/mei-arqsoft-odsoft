# Geração de ID's

---

## Design objective

Suporte à geração de ID's em formatos customizáveis, podendo haver alteração do formato por configuração.


## Quality Attribute Scenario

| **Element**          | **Statement**                                                                                                                                                                                                                                                                      |
|----------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Stimulus**         | Existe a necessidade de gerar ID's para as entidades, em formatos específicos, de acordo com especificações definidas pelo cliente.                                                                                                                                                |
| **Stimulus source**  | Cliente define as diferentes regras de formatação de ID's para as diferentes entidades.                                                                                                                                                                                            |
| **Environment**      | Atualmente, o sistema codifica diretamente o formato de geração de IDs, utilizando identificadores do sistema de persistência, limitando a sua adaptabilidade.                                                                                                                     |
| **Artifact**         | O módulo de geração de ID's e as classes de domínio que vão ter os atributos de identificadores alterados.                                                                                                                                    |
| **Response**         | Alterar o atributo de identificação da entidades para um ID gerado e criação de uma interface para os metodos de geração de ID's, permitindo assim várias implementações de vários formatos de ID's. A escolha do formato a ser utilizado é realizada nos ficheiros de configuração |
| **Response measure** | Deve ser possível alterar o formato utilizado para a geração de IDs através do ficheiro de configuração, de forma rápida.                                                                                                                                                          |

---

### Constraints

- O requisito deve ser implementado com base no serviço REST já existente (LMS).
- O sistema deve suportar múltiplos algoritmos de geração de ID's. Devem estar implementados os seguintes:
    - Codificação em base65 de um número aleatório com 6 digitos;
    - Codificação baseada em timestamp, seguido de um sufixo aleatório de 6 digitos em hexadecimal;
    - Codificação baseada num timestamp em base 65, seguido de um sufixo aleatório de 6 digitos em base65.

- A implementação deve permitir que o formato de geração de ID's possa ser alterado via configuração sem a necessidade de alteração de codigo;
- A geração deve garantir unicidade e compatibilidade com os sistemas que consomem os ID's.


---

### Concerns


- **Flexibilidade da Configuração**: O sistema deve possibilitar a alteração entre diferentes formatos de geração de ID's através de ficheiro de configuração sem necessidade de alteração de codigo;
- **Manutenção e Extensabilidade**: A implementação deve ser modular e extensível para que novos formatos de geração de IDs possam ser adicionados com facilidade e sem afetar as implementações existentes;
- **Configuração e Usabilidade**: A escolha do formato para a geração de IDs deve ser configurada através do ficheiro `library.properties` e deve ser de fácil compreensão para que mudanças possam ser aplicadas de forma eficiente e sem erros.



---

### Technical Memo

- **Problem**:
    - Garantir que o sistema possa identificar dados de domínio de maneira independente da persistência e com requisitos específicos definidos pelo cliente, sem comprometer a lógica do sistema.


- **Resumo da Solução**:
    - Implementar um módulo de geração de ID's que gere identificadores, independentes do sistema de persistência ativo, com suporte a múltiplos formatos, configuráveis via ficheiro de configuração.


- **Fatores**:
    - Suporte à flexibilidade de definir diferentes formatos de geração de IDs por configuração;
    - Possibilidade de personalizar a lógica de geração de IDs de forma a facilitar a inclusão de novos formatos de ID's;
    - Facilidade de manutenção e personalização para atender a diferentes necessidades de clientes.


- **Solução**:
    - **Abstract Common Services**: Criar classes abstratas e interfaces para facilitar a adição de novas estratégias de geração de IDs, promovendo reutilização e desacoplamento;
    - **Defer Binding**: Permitir a escolha do algoritmo de geração de ID's com base num ficheiro de configuração;
    - **Encapsular**: Criação de um módulo independente para facilitar a troca de formato de geração de IDs;
    - **Restringir Dependencias**: Isolar a lógica de geração de IDs, evitando dependências diretas entre componentes e facilitando a extensão com novos formatos;


- **Motivação**:
    - Garantir flexibilidade e adaptabilidade do sistema de modo a usar o módulo criado para diferentes clientes que podem requerer diferentes formatos de identificadores.


- **Alternativas**:
    - Geração fixa de UUIDs (esta alternativa não oferece flexibilidade);
    - Gerar de ID's pelos sistemas de persistência (alternativa não configurável).


- **Questões pendentes**:
    - **Que partes do sistema precisam de ser modificadas para acomodar uma nova solução?**
    - **Qual o impacto de performance entre os diferentes métodos de geração de IDs?**
    - **Por definição algum método de geração de IDs deve ser assumido?**