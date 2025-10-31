# Documentação ARQSOFT

Este documento serve para toda a documentação do projeto para a unidade curricular de Arquitetura de Software (ARQSOFT)

## System-as-is

### Vistas de Implementação

#### Nível 1

Vista de implementação de nível é sememlhante à vista lógica correspondente ao mesmo nível.

#### Nível 2
![VI_Level_2.jpg](System-as-is/Vista%20Implementa%C3%A7%C3%A3o/VI_Level_2.jpg)

#### Nível 3
![VI_Level_3.jpg](System-as-is/Vista%20Implementa%C3%A7%C3%A3o/VI_Level_3.jpg)

#### Nível 4
![VI_Level_4.jpg](System-as-is/Vista%20Implementa%C3%A7%C3%A3o/VI_Level_4.jpg)


### Vistas Lógica

#### Nível 1
![VL_Level_1.jpg](System-as-is/Vista%20L%C3%B3gica/VL_Level_1.jpg)

#### Nível 2
![VL_Level_2.jpg](System-as-is/Vista%20L%C3%B3gica/VL_Level_2.jpg)

#### Nível 3
![VL_Level_3_Folder.jpg](System-as-is/Vista%20L%C3%B3gica/VL_Level_3.jpg)


### Vistas Físicas

#### Nível 1
![VF_LVL1.jpg](System-as-is/Vista%20F%C3%ADsica/VF_Level_1.jpg)

#### Nível 2
![VF_LVL2.jpg](System-as-is/Vista%20F%C3%ADsica/VF_Level_2.jpg)


### Vistas de Processo

#### Nível 1
(Método CreateBook)

![VP_Level_1_CreateBook.jpg](System-as-is/Vista%20Processos/Create%20Book/VP_Level_1_CreateBook.jpg)

(Método XXXX_2)

(Método XXXX_3)


#### Nível 2
(Método CreateBook)

![VP_Level_2_CreateBook.jpg](System-as-is/Vista%20Processos/Create%20Book/VP_Level_2_CreateBook.jpg)

(Método XXXX_2)

(Método XXXX_3)


#### Nível 3
(Método CreateBook)

![VP_Level_3_CreateBook.jpg](System-as-is/Vista%20Processos/Create%20Book/VP_Level_3_CreateBook.jpg)

(Método XXXX_2)

(Método XXXX_3)


---
## Architectural Software Requirements (ASR)

### Contexto do Sistema
O sistema LMS (Library Management System) é um serviço backend responsável pela gestão de uma biblioteca. O sistema disponibiliza um conjunto de endpoints que permitem a interação com as suas funcionalidades principais, incluindo a gestão de livros, géneros, autores, leitores e empréstimos. Através destes serviços, é possível realizar operações de consulta, criação, atualização e eliminação dos diferentes recursos.

### Problemas
O sistema atualmente não suporta:

- **Extensibilidade**: a arquitetura atual não facilita a integração de novas funcionalidades ou módulos sem modificações significativas no código existente;
- **Configurabilidade**: o sistema apresenta uma falta de flexibilidade para modificar comportamentos e parâmetros do sistema em tempo de execução;
- **Confiabilidade**: faltam mecanismos robustos de tratamento de erros, monitorização e recuperação do serviço em caso de falhas.
- **Persistência de dados**: O sistema atual não permite persistir dados em diferentes modelos de SGBDs.


### Requisitos Funcionais

- O sistema deve permitir a persistência e gestão de dados em diferentes modelos de base de dados, como:
  - Modelos relacionais (H2 / SQL);
  - Modelos de documentos (Mongo DB);
  - Motor de pesquisa orientado a documentos (Elastic Search);
  - Armazenamento em cache (Redis):

- O sistema deve conseguir ir buscar o ISBN de um Livro através do seu título usando diferentes sistemas externos (Google Books API, Open Library Search API);

- O sistema deve gerar IDs em diversos formatos segundo as especificações providenciadas.

### Requisitos Não Funcionais

- **Extensibilidade**: O sistema deve permitir a adição de novos tipos de dados e funcionalidades sem impacto significativo na arquitetura existente.
- **Configurabilidade**: O sistema deve ser configurável mediante um arquivo de configuração, sem necessidade de alteracoes no código.
- **Confiabilidade**: O sistema deve garantir um mecanismo de recuperação para falhas dos serviços externos à aplicação.
- **Desempenho**: O tempo de resposta do sistema deve ser de curta duração. A pesquisa de dados deve ser otimizada, sendo realizada primeiro em Redis para reduzir a latência e melhorar o tempo de resposta. O sistema deve ser projetado para escalar horizontalmente conforme necessário.
- **Segurança**: O sistema deve garantir a autenticação e autorização de utilizadores, podendo adicionar novos a pedido dos clientes.


---
## Classificação dos Requisitos

### Tabela de fatores arquiteturais

A classificação dos requisitos e a definição das prioridades foram realizadas com base nos materiais disponiblizados na unidade curricular de arquitetura de software, mais especificamente, na tabela apresentada na página 5 da apresentação "ADD application example".

| Fator                       | Qualidade do Cenário                                                                                   | Variabilidade                                                                                          | Impacto nos Stakeholders                                                                              | Frequência/Probabilidade | Risco/Gravidade | Prioridade |
|-----------------------------|--------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------|--------------------------|-----------------|------------|
| Persistência de Dados       | O sistema precisa persistir dados em diferentes sistemas de gestão de base de dados, por configuração. | Suporte a diferentes sistemas de suporte bases de dados (SQL + Redis, MongoDB + Redis, Elastic Search) | Falhas ou erros na persistência irá impactar a disponibilidade e consistência de dados na aplicação.  | **Regular**              | **Alto**        | **4**      |
| Consulta de ISBN por Título |O sistema deve permitir a consulta de ISBNs com base no título do livro, utilizando diferentes serviços externos, por configuração.             | Suporte a Google Books API e Open Library Search API.                                                  | Falhas na integração podem afetar funcionalidades de pesquisa e criacao de dados, prejudicando a usabilidade.           | **Ocasional**            | **Baixo**       | **2**      |
| Geração de IDs              | O sistema gera IDs em formatos personalizados, sem afetar operações existentes, por configuração.      | Suporte a múltiplos formatos de IDs                                                                    | Falhas podem limitar um cliente que pode precisar de um formato de ID personalizado.                  | **Regular**              | **Médio**       | **3**      |


---
## Attribute-driven design (ADD)

### Quality Attribute Scenario - Persistir dados em diferentes SGBDs
[Persistir dados em diferentes SGBD.md](ADD/Persistir%20dados%20em%20diferentes%20SGBD.md)

### Quality Attribute Scenario - Geração de ID's
[Geração de ID's.md](ADD/Gera%C3%A7%C3%A3o%20de%20ID%27s.md)

### Quality Attribute Scenario - Buscar ISBN pelo título do Livro



---
## Architectural Design Alternatives and Rational

### Tactics

No projeto desenvolvido, foram aplicadas táticas de modificabilidade com foco na redução do acoplamento entre componentes, para ser possível integrar novas funcionalidades ou módulos sem modificações significativas no código préviamente existente. Para isso, utilizamos três principais táticas: Encapsulamento, Uso de Intermediários e Abstração de Serviços Comuns.

#### Encapsulamento

O encapsulamento foi implementado ao desenvolver interfaces comuns, que descrevem os métodos que irão ser comuns a outras classes, sem revelar como cada implementação realiza as operações. Para esse efeito, cada classe será responsável por implementar a interface de forma independente, reduzindo o acoplamento entre componentes.

Pegando num exemplo mais prático, A interface `BookRepository` define métodos como `save`, `findByIsbn`, `findByTitle`, entre outros. Estes métodos são depois implementados nos repositórios específicos das bases de dados a ser utilizada, como por exemplo, no `BookRepositoryRelationalImpl` ou no `BookRepositoryMongoDBImpl`. A mesma tática é utilizada para os restantes requisitos de gerar ID's e de pesquisar um ISBN pelo título de um livro.

#### Uso de Intermediários

Foi utilizado no projeto a camada de serviços como um intermediário entre as camadas superiores do projeto (camada de controladores) e as interfaces do repositório. Os serviços executam as operações das interfaces sem conhecer as implementações utilizadas. A implementação utilizada é configurada dinamicamente através de um ficheiro de configuração, permitindo assim selecionar a configuração mais adequada ao contexto do cliente. Mais uma vez, esta tática reduz o acoplamento e facilita a substituição de componentes internos sem impactar outras partes do sistema. 

Pegando num exemplo mais prático, a classe `BookService` apenas interage com a interface `BookRepository` e não conhece a implementação que será utilizada. Essa será definida na configuração da aplicação.


#### Abstração

A abstração de serviços comuns foi aplicada de forma a padronizar comportamentos que são comuns a várias classes, facilitando a reutilização de código e promovendo o princípio "DRY Don’t Repeat Yourself".
Novamente, para implementar esta tática, foram criadas interfaces comuns para representar funcionalidades genéricas. Cada interface possui depois múltiplas implementações, adaptadas a diferentes requisitos.


### Reference Architectures

#### Onion Architecture

#### Clean Architecture

#### Modular Monolith


### Patterns

### Alternatives of Design

---
## Mutation Tests

### Primeiro teste de mutação

O primeiro teste de mutação foi feito ao projeto base, e o resultado obtido foi o seguinte:

![BaseProjectMutationTestsResult.png](assets/BaseProjectMutationTestsResult.png)

Os resultados indicam que apenas 22% das mutações geradas foram mortas, o que significa que alguns defeitos podem não ser cobertos pelos testes existentes.

Além disso, 33% de cobertura de linhas de código nas classes mutadas mostra que ainda existe uma parte significativa do código que continua por testar.

### Segundo teste de mutação

O segundo teste de mutação foi feito já ao projeto em desenvolvimento, e o resultado obtido foi o seguinte:

![ARQSOFTProjectMutationTestsResults_1.png](assets/ARQSOFTProjectMutationTestsResults_1.png)

Os resultados do segundo teste mostram melhorias relativamente aos resultados do projeto base. 

É possivel ver que 57% das mutações geradas foram mortas, o que mostra um aumento de mais de 30% em relação aos resultados do primeiro teste.

Em relação à cobertura de linhas de código nas classes mutadas, passamos a ter uma cobertura de 76%, ou seja, um aumento de mais de 40%.