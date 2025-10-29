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
![VF_LVL1.jpg](System-as-is/Vista%20F%C3%ADsica/VF_LVL1.jpg)

#### Nível 2
![VF_LVL2.jpg](System-as-is/Vista%20F%C3%ADsica/VF_LVL2.jpg)


### Vistas de Processo

#### Nível 1
(Método XXXX_1)

![VP_Level_1_LMS.jpg](System-as-is/Vista%20Processos/VP_Level_1_LMS.jpg)

(Método XXXX_2)

(Método XXXX_3)


#### Nível 2
(Método XXXX_1)

![VP_Level_2_LMS.jpg](System-as-is/Vista%20Processos/VP_Level_2_LMS.jpg)

(Método XXXX_2)

(Método XXXX_3)


#### Nível 3
(Método XXXX_1)

![VP_Level_3_LMS.jpg](System-as-is/Vista%20Processos/VP_Level_3_LMS.jpg)

(Método XXXX_2)

(Método XXXX_3)


## Architectural Software Requirements (ASR)

### Contexto do Sistema
O sistema LMS (Library Management System) é um serviço backend responsável pela gestão de uma biblioteca. O sistema disponibiliza um conjunto de endpoints que permitem a interação com as suas funcionalidades principais, incluindo a gestão de livros, géneros, autores, leitores e empréstimos. Através destes serviços, é possível realizar operações de consulta, criação, atualização e eliminação dos diferentes recursos.

### Problemas
O sistema atualmente não suporta:

- Extensibilidade: a arquitetura atual não facilita a integração de novas funcionalidades ou módulos sem modificações significativas no código existente;
- Configurabilidade: o sistema apresenta uma falta de flexibilidade para modificar comportamentos e parâmetros do sistema em tempo de execução;
- Confiabilidade: faltam mecanismos robustos de tratamento de erros, monitorização e recuperação do serviço em caso de falhas.
- Persistência de dados: O sistema atual não permite persistir dados em diferentes modelos de SGBDs.


### Requisitos Funcionais

- O sistema deve permitir a persistência e gestão de dados em diferentes modelos de base de dados, como:
  - Modelos relacionais (H2 / SQL);
  - Modelos de documentos (Mongo DB);
  - Motor de pesquisa orientado a documentos (Elastic Search);
  - Armazenamento em cache (Redis):

- O sistema deve conseguir ir buscar o ISBN de um Livro através do seu título usando diferentes sistemas externos (Google Books API, Open Library Search API);

- O sistema deve gerar IDs em diversos formatos de acordo com as especificações providenciadas.

### Requisitos Não Funcionais

### Classificação dos Requisitos


## Attribute-driven design (ADD)

### QAS-01
[Persistir dados em diferentes SGBD.md](ADD/Persistir%20dados%20em%20diferentes%20SGBD.md)