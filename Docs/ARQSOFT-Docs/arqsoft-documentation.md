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

## Tactics

No projeto desenvolvido, foram aplicadas táticas de modificabilidade com foco na redução do acoplamento entre componentes, para ser possível integrar novas funcionalidades ou módulos sem modificações significativas no código préviamente existente. Para isso, utilizamos três principais táticas: Encapsulamento, Uso de Intermediários e Abstração de Serviços Comuns.

### Encapsulamento

O encapsulamento foi implementado ao desenvolver interfaces comuns, que descrevem os métodos que irão ser comuns a outras classes, sem revelar como cada implementação realiza as operações. Para esse efeito, cada classe será responsável por implementar a interface de forma independente, reduzindo o acoplamento entre componentes.

Pegando num exemplo mais prático, A interface `BookRepository` define métodos como `save`, `findByIsbn`, `findByTitle`, entre outros. Estes métodos são depois implementados nos repositórios específicos das bases de dados a ser utilizada, como por exemplo, no `BookRepositoryRelationalImpl` ou no `BookRepositoryMongoDBImpl`. A mesma tática é utilizada para os restantes requisitos de gerar ID's e de pesquisar um ISBN pelo título de um livro.

### Uso de Intermediários

Foi utilizado no projeto a camada de serviços como um intermediário entre as camadas superiores do projeto (camada de controladores) e as interfaces do repositório. Os serviços executam as operações das interfaces sem conhecer as implementações utilizadas. A implementação utilizada é configurada dinamicamente através de um ficheiro de configuração, permitindo assim selecionar a configuração mais adequada ao contexto do cliente. Mais uma vez, esta tática reduz o acoplamento e facilita a substituição de componentes internos sem impactar outras partes do sistema. 

Pegando num exemplo mais prático, a classe `BookService` apenas interage com a interface `BookRepository` e não conhece a implementação que será utilizada. Essa será definida na configuração da aplicação.


### Abstração

A abstração de serviços comuns foi aplicada de forma a padronizar comportamentos que são comuns a várias classes, facilitando a reutilização de código e promovendo o princípio "DRY Don’t Repeat Yourself".
Novamente, para implementar esta tática, foram criadas interfaces comuns para representar funcionalidades genéricas. Cada interface possui depois múltiplas implementações, adaptadas a diferentes requisitos.

---

## Reference Architectures

### Onion Architecture

O sistema apresenta características inspiradas na **Onion Architecture**, uma abordagem que organiza o código por camadas (camadas concêntricas) e direciona as dependências para o núcleo da aplicação (domínio).  
As camadas identificadas são:

- Core Domain (Camada mais interna)
  - **Propósito**: Concentrar a lógica do negócio e as regras que representam o núcleo funcional do sistema.
  - **Conteúdo**: Entidades de domínio, objetos de valor e regras de negócio.
> **Nota:** Nesta implementação, o domínio inclui também elementos de persistência (como anotações JPA ou lógica relacionada com bases de dados), o que quebra parcialmente o princípio de independência total face à infraestrutura.


- Application Services (Camada intermédia)
  - **Propósito**: Definir os casos de uso da aplicação, orquestrando a interação entre o domínio e as camadas externas.
  - **Conteúdo**: Serviços de aplicação, interfaces e lógica de coordenação.


- Interface/Adapters (Camada externa)
  - **Propósito**: Adaptar o domínio e os serviços da aplicação a diferentes interfaces externas, como a camada de apresentação ou os mecanismos de persistência.
  - **Conteúdo**: Controladores, repositórios e adaptadores para integração com APIs externas.


- Frameworks & Drivers (Camada mais externa)
  - **Propósito**: Conter os elementos dependentes de frameworks e tecnologias externas que suportam a execução do sistema.
  - **Conteúdo**: Configurações de base de dados, controladores REST, drivers, frameworks e componentes de infraestrutura.

    
### Clean Architecture

O sistema partilha diversos princípios da **Clean Architecture**, nomeadamente a separação de responsabilidades e a orientação das dependências para o domínio.  
No entanto, a implementação **não cumpre totalmente** os princípios desta arquitetura, devido a:
- Acoplamento entre entidades de domínio e mecanismos de persistência (ex.: uso de anotações JPA);
- Dependências diretas entre o domínio e a infraestrutura, reduzindo a sua independência.

Por este motivo, a arquitetura deve ser considerada **inspirada na Clean Architecture**, mas **não uma implementação pura** da mesma.


### Modular Monolith

O projeto demonstra elementos de uma **Modular Monolith Architecture**, organizando o sistema em pacotes específicos de domínio que agregam componentes de negócio, serviços, controladores e repositórios relacionados.  
Esta estrutura promove uma separação lógica entre áreas funcionais e evidencia uma intenção clara de modularização dentro de um único processo de execução.

No entanto, os módulos mantêm **dependências significativas entre si**, resultando num **acoplamento notável entre componentes** e numa limitação da sua independência.  
Adicionalmente, a presença de **lógica de persistência no domínio** reforça a ligação entre as camadas, reduzindo a aderência aos princípios de isolamento típicos de arquiteturas modulares.

Deste modo, a implementação aproxima-se mais de uma **Layered Architecture tradicional**, embora mantenha **inspiração nos princípios da Onion e da Clean Architecture**.

---

## Patterns

### Repository Pattern

O sistema segue o padrão Repository Pattern, utilizando repositórios como um intermediário entre a lógica de negocio e o armazenamento de dados.

O proposito principal desde padrão é fornecer uma forma estruturada e padronizada de aceder, gerir e manipular dados, ao mesmo tempo que abstrai os detalhes das tecnologias de armazenamento de dados.
Além disso, também promove uma separação clara de responsabilidades, tornando o projeto mais manutenível, testável e adaptavel a novas bases de dados,


### Strategy Pattern

O sistema usa o Strategy Pattern, permitindo escolher, por exemplo, qual o algoritmo de geração de ids ou qual a base de dados a utilizar.

Este padrão permite definir um conjunto de algoritmos ou comportamentos, separá-los por classes e escolher qual utilizar via ficheiro de configuração. 

Isto é util quando queremos ter variabilidade.

### Factory Pattern

No projeto é seguido o Factory Pattern, onde são utilizadas factories para a geração de ids e para a pesquisa de isbn por título.

O Factory Pattern é utilizado para encapsular a lógica de criação de objetos, permitindo que o sistema escolha qual implementação instanciar em runtime.
No caso deste projeto, usando o exemplo do IsbnProviderFactory, é utilizado este padrão para escolher dinamicamente a implementação do IsbnProvider, baseado na configuração. 

Assim, permite flexibilidade e extensibilidade.

---

## Alternatives of Design

### Event-Driven Architecture (EDA)
Esta é alternativa é uma arquitetura em que os componentes do sistema comunicam entre si através de eventos, em vez das habituais chamadas diretas.
Cada componente atua como emissor (publisher) ou assinante (subscriber) de eventos, promovendo baixo acoplamento devido ao facto dos componentes não precisam conhecer diretamente uns aos outros, comunicando-se apenas através de eventos. Esta arquitetura promove também alta flexibilidade na integração de novos serviços e funcionalidades.

Esta alternativa, como todas, têm também os seus desafios. Um deles pode ser a complexidade na gestão de eventos, especialmente em grandes projetos, ou projetos altamente distribuídos, pois o fluxo de eventos pode tornar-se difícil de rastrear.

A sua implementação seria a seguinte:
- Seria implementado um barramento central de eventos (Event Bus) onde os serviços podem publicar e subscrever eventos relacionados a operações que lhes compete;
- Um serviço de assinantes e publicadores onde cada serviço atua como assinante dos eventos relevantes ao seu domínio e publicador dos eventos que resultam das suas operações, permitindo assim comunicação assíncrona entre componentes;
- Usando esta arquitetura também é possível ter uma configuração dinâmica onde num ficheiro de configuração é possível definir os serviços que irão estar ativos e como devem reagir a determinados eventos, permitindo assim flexibilidade no sistema.