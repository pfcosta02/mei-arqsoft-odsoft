# Persistir dados em diferentes SGBD

---

## Design objective

Suporte à persistência de dados através de múltipos SGBD's (relacionais e de documentos) selecionando o método através de ficheiro de configuração.


## Quality Attribute Scenario

| **Element**          | **Statement**                                                                                                                                                                                                            |
|----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Stimulus**         | Existe a necessidade de suportar outros tipos de SGBD de forma a sustentar a necessidades de diferetentes clientes.                                                                                                      |
| **Stimulus source**  | Cliente precisa que através de configuração seja possível adaptar a aplicação a diferentes SGBD's                                                                                                                        |
| **Environment**      | Atualmente, a necessidade de usar SGBD's para diferentes clientes requer a criação de várias entidades para persistência, e com isso aumentar a duplicação/repetição de código                                           |
| **Artifact**         | O software LMS, mais especificamente, a camada de persistencia de dados                                                                                                                                                  |
| **Response**         | Desenvolver uma solução baseada usando interfaces para diferentes implementações e utilizando a anotação @Profile do Spring Boot para configurar. Deve permitir selecionar entre vários SGBD's previamente configurados. |
| **Response measure** | Deve ser possível alterar o modelo de dados e SGBDs pretendido através de um ficheiro de configuração.                                                                                                                   |

---

### Constraints

- O requisito deve ser implementado com base no serviço REST já existente (LMS).
- O requisito deve ser implementado de forma a responder às necessidades do cliente de persistir dados em diferentes modelos de dados e SGBD. Devem ser suportadas as seguintes combinações:

    - **Modelo de dados relacional: SQL Server** juntamente com o **sistema de base de dados em cache Redis**;

    - **Modelo de dados de documentos (MongoDB)** juntamente com o **sistema de base de dados em cache Redis**;

    - **Modelo de dados de documentos (Elastic Search)**.

- A escolha de modelo de SGBD deve ser feita via ficheiro de configuração (application.properties), sem que haja necessidade de recompilação.
---

### Concerns

- **Flexibilidade da Configuração**: O sistema deve permitir a seleção da persistência de dados em diferentes modelos de dados através de configuração sem necessisade de realizar alterações de código;


- **Manutenção e Extensibilidade**: A implementação deve ser modular e extensível para que a adição de suporte a novos SGBD's seja simples e sem dificultar a manutenção a longo prazo;


- **Configuração e Usabilidade**: A configuração através do ficheiro `application.properties` deve ser intuitiva e de fácil acesso, de forma que as alterações possam ser aplicadas de maneira rápida e sem erros.

---

### Technical Memo

- **Problem**: 
  - Persistência de dados em diferentes modelos de dados (por exemplo, relacional, documento) e SGBD (SQL, MongoDB, Elastic Search, entre outras possibilidades)


- **Resumo da Solução**: 
  - Implementar uma solução configurável para que aplicação possa suportar diferentes tipos de modelos de dados e sistemas de gestão de banco de dados, como o SQL e MongoDB, integrando Redis nestas duas soluções, e Elastic Search.


- **Fatores**:
    - Suporte à flexibilidade de persistência: a capacidade de integração de diferentes SGBD's com estruturas de dados distintas, relacionais (ex: SQL) ou não relacionais (ex: MongoDB);
    - Facilidade de manutenção e extensão futura: manter a aplicação modular e flexível para suportar a troca ou inclusão de novos SGBD's através do ficheiro de configuração.


- **Solução**:
    - **Interface intermediaria** :Utilização de uma interface comum (repositório) que define os métodos de persistência. Cada SGBD implementa essa interface, permitindo abstrair as especificidades de cada tecnologia e simplificar a interação com a aplicação;
    - **Abstract Common Services**: Para facilitar a integração com novos tipos de base de dados, criar interfaces e classes abstratas que representam estratégias de persistência;
    - **Defer Binding**: Vai permitir selecionar o SGBD pretendido através do ficheiro de configuracao, permitindo flexibilidade sem recompilação;
    - **Encapsular**: A lógica de persistência está isolada num módulo independente, o que facilita a substituição ou adição de novas tecnologias sem impacto noutras partes do sistema.



- **Motivação**: Garantir flexibilidade e adaptabilidade do sistema de modo a usar o código base para diferentes clientes que podem requerer diferentes modelos de base de dados.


- **Alternativas**: Acoplar diretamente a lógica de persistência com implementações específicas de bases de dados, o que limitaria a flexibilidade e dificultaria futuras mudanças.


- **Questões pendentes**:
    - **O quão longo e difícil a refazer/adaptar a solução atual para adotar outros modelos de base de dados?**
    - **Quais são as partes do sistema que vão requerer modificações?**
    - **Qual o impacto de desempenho ao alternar entre modelos?**
    - **Como garantir consistência entre Redis e o SGBD principal?**