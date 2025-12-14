# Buscar ISBN através do Título de um Book

---

## Design objective

O objetivo é projetar um mecanismo de busca de ISBN de livros a partir do título, que seja flexível, extensível e utilize diferentes serviços externos (ISBNdb, Google Books API e Open Library API), selecionando o serviço ativo através de ficheiro de configuração.

## Quality Attribute Scenario

| **Element**          | **Statement**                                                                                                                                                                                                              |
|----------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Stimulus**         | Existe a necessidade de obter o ISBN de um livro a partir do seu título, utilizando diferentes serviços externos, de acordo com especificações definidas pelo cliente.                                                     |
| **Stimulus source**  | Cliente define os diferentes serviços externos a serem utilizados, podendo adicionar um livro pelo título sem inserir manualmente o ISBN.                                                                                  |
| **Environment**      | Atualmente, o ISBN tem de ser inserido manualmente ao fazer um pedido de criação de um livro.                                                                                                                              |
| **Artifact**         | O módulo de consulta de ISBN's e ficheiro de configuração (application.properties).                                                                                                                                        |
| **Response**         | O sistema deve consultar o serviço externo definido na configuração e devolver o ISBN correspondente ao título fornecido no pedido. A escolha do sistema externo a ser utilizado é realizada nos ficheiros de configuração |
| **Response measure** | Deve ser possível alterar o sistema externo utilizado para a busca de ISBN's através do ficheiro de configuração, de forma rápida.                                                                                         |

---

### Constraints

- O requisito deve ser implementado com base no serviço REST já existente (LMS).
- O sistema deve suportar múltiplos sistemas externos de base de dados de livros. Devem estar implementados os seguintes:
    - Google Books API;
    - Open Library Search API;
    - Dois anteriores em simultâneo.

- A implementação deve permitir que o sistema utilizado possa ser alterado via configuração sem a necessidade de alteração de codigo;
- A consulta deve ser resiliente a falhas de serviços externos e garantir uma resposta adequada ao utilizador.

---

### Concerns


- **Flexibilidade da Configuração**: O sistema deve possibilitar a alteração entre diferentes sistemas externos através de ficheiro de configuração sem necessidade de alteração de codigo;
- **Manutenção e Extensabilidade**: A implementação deve ser modular e extensível para que novos sistemas externos possam ser adicionados com facilidade e sem afetar as implementações existentes;
- **Configuração e Usabilidade**: A escolha do formato para o sistema externo a ser utilizado deve ser configurada através do ficheiro `application.properties` e deve ser de fácil compreensão para que mudanças possam ser aplicadas de forma eficiente e sem erros.



---

### Technical Memo

- **Problem**:
    - Neccessidade do sistema poder consultar ISBN's através do título de um livro usando diferentes serviços externos, podendo alternar requisitos específicos definidos pelo cliente, sem comprometer a lógica do sistema.


- **Resumo da Solução**:
    - Implementar um módulo de consulta de ISBN's composto por uma interface comum a todos os serviços e implementações individuais. Deve haver suporte a múltiplos serviços externos, configuráveis via ficheiro de configuração.


- **Fatores**:
    - Suporte à flexibilidade de definir diferentes sistemas externos de busca de livros pelo ISBN por configuração;
    - Possibilidade de personalizar a lógica de busca de ISBN de forma a facilitar a inclusão de novos sistemas externos;
    - Facilidade de manutenção e personalização para atender a diferentes necessidades de clientes.


- **Solução**:
    - **Abstract Common Services**: Criar classes abstratas e interfaces para facilitar a adição de novas estratégias de busca de ISBN, promovendo reutilização e desacoplamento;
    - **Defer Binding**: Permitir a escolha do sistema externo a ser utilizado com base num ficheiro de configuração;
    - **Encapsular**: Criação de um módulo independente para facilitar a troca de sistema em utilização;
    - **Restringir Dependencias**: Isolar a lógica de busca de ISBN's pelo título, evitando dependências diretas entre componentes e facilitando a extensão com novos sistemas externos.


- **Motivação**:
    - Garantir flexibilidade e adaptabilidade do sistema de modo a usar o módulo criado para diferentes clientes que podem requerer diferentes sistemas externos;
    - Aumenta a confiabilidade, podendo combinar múltiplos sistemas para validar os resultados obtidos.


- **Alternativas**:
    - Utilização fixa de um único serviço (solução mais simples de implementar, mas fica dependente de um único fornecedor);
    - Cliente chamar diretamente as API's externas e criar livro no sistema com o ISBN retornado;
    - Implementação manual por cada tipo de consulta (solução mais complexa e difícil de manter).


- **Questões pendentes**:
    - **Como proceder em caso de falha do serviço externo?**
    - **Qual o impacto de performance entre os diferentes sistemas externos?**
    - **Por definição, algum sistema externo deve ser assumido?**
    - **Como garantir que os dados retornados de diferentes sistemas externos estão organizados de igual forma?**


