# Linguagens e Ambientes de Execução: Apresentação da UC

## Descrição e Conteúdo

- Muitos _softwares_ atuais são destinados à execução num ambiente _runtime_ mediado por _software_.
  - Managed Runtime Environment (MRE)
  - Ambientes de execução virtual 
- Máquina Virtual Java (JVM)
  - Originalmente concebida para executar _software_ escrito em Java
  - Agrega atualmente outras linguagens de programação relevantes, como:
    - Kotlin, Scala, Groovy ou Clojure
- .Net Runtime: MRE da Microsoft 
  - Características e funcionalidades semelhantes às da JVM
  - Abrange as linguagens C#, F# e Visual Basic
- Nosso foco: **Máquina Virtual Java (JVM)**
- Conteúdo da [Unidade Curricular](https://isel.pt/leic/linguagens-e-ambientes-de-execucao):
  1. Comparar e utilizar diferentes construções comuns em **linguagens de programação modernas**, enquadrando diferentes paradigmas de programação, e o seu **suporte no ambiente de execução**.
  2. Entender os **principais constituintes de um ambiente de execução** para linguagens de alto nível, e saber comparar diferentes abordagens de sistemas de tipos destes ambientes.
  3. Usar **metadados** em tempo de execução (**reflexão**) para examinar tipos e usar **metaprogramação** para analisar e **transformar programas em tempo de execução**.
  4. Analisar o **desempenho** de programas _managed_ e usar eficientemente o **suporte automático de gestão de memória** (_garbage collection_).

## Bibliografia

- Notas de aula no Github.
- Documentação online das tecnologias utilizadas.
- Livro principal:
    - Miguel Gamboa. _The Managed Runtime Environment: Diving into the JVM with Kotlin_. 2025. Disponível em: http://leanpub.com/kotlinonjvm

- Conteúdo:
  - Capítulo 1: Introdução à JVM
  - Capítulo 2: Tipos Básicos
  - Capítulo 3: Reflexão
  - Capítulo 4: Testes de desempenho e _Microbenchmarking_
  - Capítulo 5: Bytecode
  - Capítulo 6: Metaprogramação
  - Capítulo 7: Sequências, Geradores e Funções Suspensas
  - Capítulo 8: _Garbage Collection_ e Ações de Limpeza

## Metodologia

- Aulas: apresentação do conteúdo e demonstrações práticas
  - Algumas aulas serão dedicadas para apoio de laboratório  
- Conteúdo das aulas será disponibilizado no **Github** da turma
  - https://github.com/isel-leic-ave/2025-lae-41d
- Comunicação: **Slack**
  - https://isel-leic-lae.slack.com

## Avaliação

- **Teste Escrito** (T):
  - Individual
  - Ao final do semestre
- **Trabalhos Práticos** (TP)
  - 1 trabalho individual
  - 1 trabalho em grupo de 3 alunos:
    1) Parte 1: Sistemas de tipos Java e Reflexão;
    2) Parte 2: Metaprogramação e Desempenho;
    3) Parte 3: Processamento _Lazy_.
  - Realizados durante o semestre
  - Avaliados em discussão: últimas duas semanas
    - Semanas 26-mai e 2-jun 
- **Nota final**: média entre T (50%) e TP com discussão final (50%)
  - Cada uma das componentes da avaliação (T e CP) tem que ter uma avaliação superior ou igual a 9,50 valores (já considera arredondamento)
