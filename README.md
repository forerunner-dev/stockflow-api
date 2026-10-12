# StockFlow API

<div align="justify">

REST API for inventory management, built with Java and Spring Boot.

API REST de gerenciamento de estoque, feita em Java com Spring Boot.

## O que é uma API REST

Uma API é a porta pela qual outros sistemas conversam com uma aplicação, e REST é o estilo mais usado para desenhar essa porta na web. Numa API REST, cada coisa que o sistema gerencia é um **recurso**, identificado por um endereço (URL), e as ações sobre ele são expressas pelos **métodos do protocolo HTTP**: `GET` consulta, `POST` cria, `PUT` atualiza e `DELETE` remove. Os dados trafegam em JSON, e o resultado de cada operação é comunicado por um **código de status HTTP**, como 200 (sucesso), 201 (criado), 204 (removido, sem conteúdo), 400 (dados inválidos) e 404 (não encontrado). Cada requisição é independente e carrega tudo o que o servidor precisa para atendê-la, sem que ele guarde informações da conversa entre uma chamada e outra.

No StockFlow, o recurso é a pessoa, disponível em `/api/v1/person`. Um `GET` nesse endereço lista todas as pessoas, e um `GET` em `/api/v1/person/7` busca a pessoa de id 7. Um `POST` com o JSON da pessoa cria um novo registro e responde 201, um `PUT` em `/api/v1/person/7` atualiza o registro 7, e um `DELETE` no mesmo endereço o remove e responde 204. Como o contrato é feito só de URLs, métodos HTTP e JSON, qualquer cliente pode usar a API, seja um site, um aplicativo de celular ou outro sistema, sem depender da tecnologia em que ela foi escrita.

## Estrutura do projeto

O StockFlow é organizado em camadas, e cada camada tem uma única responsabilidade. Uma requisição HTTP chega primeiro ao **controller** (`controllers`), que só recebe os dados e devolve a resposta. O controller repassa o trabalho ao **service** (`services`), onde ficam as regras de negócio, como buscar um registro e responder "não encontrado" quando ele não existe. O service conversa com o banco por meio do **repository** (`repository`), e o banco é representado no código pela **entidade** (`model`), uma classe que espelha uma tabela. Entre o mundo externo e a entidade existem os **DTOs** (`dto`), objetos que definem exatamente o que entra e o que sai em JSON, e os **mappers** (`mapper`), que traduzem um formato no outro. Assim, a entidade nunca é exposta diretamente ao cliente, e a tabela pode mudar sem quebrar o contrato da API. Os erros são tratados num ponto central (`exception`), que transforma exceções em respostas HTTP padronizadas.

A API é versionada pela URL: `/api/v1/person` e `/api/v2/person` convivem lado a lado. Cada versão tem o seu controller, os seus DTOs (`dto/v1` e `dto/v2`) e o seu mapper, enquanto o service, a entidade e o repository são compartilhados, porque as regras de negócio são as mesmas e só o formato dos dados muda. As credenciais do banco não ficam em nenhum arquivo do repositório: o `application.yaml` apenas referencia variáveis de ambiente (`DB_URL`, `DB_USERNAME` e `DB_PASSWORD`), cujos valores ficam configurados na máquina onde a aplicação roda. Os testes unitários ficam em `src/test`, no pacote `unittests`, e verificam os mappers e as regras de validação sem precisar de banco de dados.

## Tecnologias

![Java](https://img.shields.io/badge/Java-25-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-7-59666C?style=flat-square&logo=hibernate&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8-4479A1?style=flat-square&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![JUnit](https://img.shields.io/badge/JUnit-25A162?style=flat-square&logo=junit5&logoColor=white)
![Git](https://img.shields.io/badge/Git-F05032?style=flat-square&logo=git&logoColor=white)

| Área | Tecnologia | Papel no projeto |
|---|---|---|
| Linguagem | [Java 25](#java-25) | Linguagem de todo o código, com records, `var` e streams |
| Base | [Spring Boot 4.1](#spring-boot-41) | Monta a aplicação e injeta as dependências |
| Web | [Spring Web MVC e Tomcat](#spring-web-mvc-e-tomcat-embutido) | Recebe as requisições HTTP e chama o controller certo |
| Web | [Jackson](#jackson) | Converte JSON em objetos Java e vice-versa |
| Dados | [Spring Data JPA e Hibernate](#spring-data-jpa-e-hibernate) | Mapeia classes em tabelas e gera o SQL |
| Dados | [MySQL, Connector/J e HikariCP](#mysql-8-mysql-connectorj-e-hikaricp) | Banco de dados, driver e pool de conexões |
| Qualidade | [Bean Validation](#bean-validation-hibernate-validator) | Recusa dados inválidos antes de chegar ao banco |
| Qualidade | [JUnit e AssertJ](#junit-e-assertj) | Testes unitários dos mappers e das validações |
| Operação | [SLF4J e Logback](#slf4j-e-logback) | Registra as operações no log |
| Ferramentas | [Maven](#maven) | Gerencia dependências, build e testes |
| Ferramentas | [Spring Boot DevTools](#spring-boot-devtools) | Reinicia a aplicação durante o desenvolvimento |
| Ferramentas | [Git e GitHub](#git-e-github) | Controle de versão e hospedagem do código |

### Java 25

Java é a linguagem do projeto: o código é compilado para bytecode e executado pela JVM (Java Virtual Machine), o que permite rodar a mesma aplicação em Windows, Linux ou macOS. A versão 25 é uma versão de suporte longo (LTS) e traz recursos modernos da linguagem.

No StockFlow, esses recursos aparecem em todo o código. Os DTOs são **records**, classes imutáveis declaradas em uma linha, que já vêm com construtor e métodos de acesso. O `var` deixa o compilador deduzir o tipo das variáveis locais, e as conversões de listas usam **streams** com **method references**, como em `.map(PersonMapper::toResponse)`, que transforma cada pessoa da lista no seu DTO de resposta.

### Spring Boot 4.1

O Spring Boot é o framework que monta a aplicação. Ele parte do Spring, que cria e conecta os objetos do sistema (a chamada injeção de dependências), e acrescenta configuração automática: ao detectar uma biblioteca no projeto, ele já a configura com valores padrão sensatos, sem exigir arquivos extensos de configuração.

No projeto, a classe `Startup` inicia tudo. O Spring encontra as classes anotadas com `@RestController`, `@Service` e as interfaces de repository, cria uma instância de cada uma e entrega as dependências pelo construtor: o controller recebe o service, e o service recebe o repository, sem que nenhuma classe precise criar a outra com `new`. As configurações específicas, como a conexão com o banco e o nível de log, ficam no `application.yaml`.

### Spring Web MVC e Tomcat embutido

O Spring Web MVC é o módulo que recebe requisições HTTP e as direciona para o método certo. O Tomcat é o servidor web que escuta a porta e entrega essas requisições ao Spring. Por estar embutido, ele sobe junto com a aplicação, sem precisar de instalação separada.

No StockFlow, as anotações `@GetMapping`, `@PostMapping`, `@PutMapping` e `@DeleteMapping` ligam cada endereço a um método do controller. A anotação `@PathVariable` lê valores da URL (como o `id` em `/api/v1/person/7`) e `@RequestBody` lê o JSON enviado no corpo. O `ResponseEntity` controla a resposta completa: o cadastro responde **201 Created** com o endereço do novo registro no cabeçalho `Location`, e a exclusão responde **204 No Content**.

### Jackson

O Jackson converte objetos Java em JSON e JSON em objetos Java. Ele trabalha automaticamente dentro do Spring Web MVC, sem precisar ser chamado no código.

Quando um cliente envia um cadastro, o Jackson lê o JSON do corpo e cria o `PersonRequestDTO` correspondente. Na resposta, ele faz o caminho inverso: transforma o `PersonResponseDTO`, ou uma lista deles, no JSON que o cliente recebe. Como os DTOs são records, o Jackson usa diretamente os seus componentes como campos do JSON.

### Spring Data JPA e Hibernate

JPA (Jakarta Persistence) é a especificação Java para mapear classes em tabelas de banco de dados, e o Hibernate é a implementação que faz esse trabalho de fato, gerando o SQL a partir das classes. O Spring Data JPA fica por cima dos dois e elimina o código repetitivo de acesso a dados: basta declarar uma interface, e ele gera a implementação.

No projeto, a classe `Person` é marcada com `@Entity` e espelha a tabela `person`; cada `@Column` define o nome, o tamanho e a obrigatoriedade da coluna, e o `@Id` com `@GeneratedValue` deixa o banco gerar o identificador. A interface `PersonRepository` estende `JpaRepository` e, sem nenhuma linha de implementação, oferece `findAll`, `findById`, `save` e `delete`. O `save` decide sozinho entre inserir e atualizar: se a entidade não tem id, ele faz um INSERT; se tem, um UPDATE. Nesta fase, a opção `ddl-auto: update` faz o Hibernate criar e ajustar as tabelas a partir das entidades.

### MySQL 8, MySQL Connector/J e HikariCP

O MySQL é o banco de dados relacional onde os registros ficam guardados. O Connector/J é o driver JDBC, a peça que permite a uma aplicação Java conversar com o MySQL. O HikariCP é o pool de conexões: em vez de abrir uma conexão nova a cada consulta, ele mantém um conjunto de conexões abertas e as reaproveita, o que torna o acesso ao banco muito mais rápido.

No StockFlow, os dados ficam no banco `stockflow_api`, com codificação `utf8mb4`, que aceita acentos e quaisquer caracteres. O endereço do banco, o usuário e a senha vêm das variáveis de ambiente referenciadas no `application.yaml`. O driver é declarado com escopo `runtime`, porque o código nunca o usa diretamente: quem o utiliza é o Hibernate, por meio do HikariCP, que o Spring Boot configura automaticamente.

### Bean Validation (Hibernate Validator)

Bean Validation é a especificação Java para declarar regras de validação com anotações, como `@NotBlank`, `@Size` e `@Email`. O Hibernate Validator é a implementação que lê essas anotações e verifica os dados.

No projeto, as regras ficam nos DTOs de entrada e espelham as colunas da tabela: nome e sobrenome são obrigatórios e têm até 80 caracteres, o endereço tem até 150, o gênero tem um caractere e, na versão 2, o email é obrigatório e precisa ter formato válido. A anotação `@Valid` no controller ativa essas regras: se algum campo for inválido, a requisição é recusada com **400 Bad Request** antes de chegar ao service, e nada é gravado no banco.

### SLF4J e Logback

O SLF4J é uma interface padrão de log, e o Logback é a implementação que de fato escreve as mensagens no console. O código fala apenas com o SLF4J, o que permite trocar a implementação sem alterar as classes.

No StockFlow, o `PersonServices` registra cada operação com mensagens como `Procurando a pessoa {}`, em que o `{}` é preenchido com o id da requisição. O nível de detalhe é controlado no `application.yaml`, na seção `logging.level`, que hoje exibe as mensagens de nível DEBUG do pacote do projeto.

### Spring Boot DevTools

O DevTools é uma ferramenta de desenvolvimento que reinicia a aplicação automaticamente quando o código é recompilado, o que acelera o ciclo de alterar e testar.

No projeto, ele é declarado como `optional` e com escopo `runtime`, o que significa que só atua durante o desenvolvimento e não é incluído no pacote final da aplicação.

### Maven

O Maven gerencia o build do projeto: baixa as bibliotecas declaradas, compila o código, roda os testes e empacota a aplicação. Tudo isso é descrito no arquivo `pom.xml`.

No StockFlow, o projeto herda do `spring-boot-starter-parent`, que define versões compatíveis entre si para todas as bibliotecas do Spring. Por isso as dependências são declaradas sem número de versão. Quando uma biblioteca indireta precisa de uma correção de segurança antes da próxima versão do Spring Boot, a versão é ajustada na seção `<properties>` do `pom.xml`, como acontece hoje com o Tomcat, o Jackson e o driver do MySQL.

### JUnit e AssertJ

O JUnit é o framework que executa os testes automatizados, e o AssertJ oferece as verificações em forma de frases encadeadas, como `assertThat(person.getId()).isNull()`, que são fáceis de ler e geram mensagens de erro claras.

Os testes do projeto ficam no pacote `unittests` e cobrem os mappers e as regras de validação das duas versões da API. Eles usam `@Nested` para agrupar os casos por método, `@DisplayName` para descrever cada cenário em uma frase e `@ParameterizedTest` para repetir o mesmo teste com várias entradas, como nomes com acentos ou emails mal formados. Por testarem classes isoladas, rodam em milissegundos e não precisam do banco de dados.

### Git e GitHub

O Git é o sistema de controle de versão que registra cada alteração do código, e o GitHub hospeda o repositório na internet.

No StockFlow, os commits seguem o padrão Conventional Commits, em que cada mensagem começa com o tipo da mudança, como `feat`, `fix`, `test` ou `chore`. A branch `main` guarda o projeto, e a branch `estudo` guarda os exercícios do curso, sem nunca ser mesclada. O `.gitignore` impede que configurações de IDE, arquivos de build e qualquer arquivo de credenciais sejam enviados ao repositório.

</div>
