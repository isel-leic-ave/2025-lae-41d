# Introdução à JVM

## _Managed Runtime Environment_

- É um ambiente de execução virtual que se encontra entre o _hardware_ e o código do nível do utilizador.
- Também conhecido por:
  - _Managed Execution Environment_ 
  - informalmente, _virtual machine_ (VM)
  - ou _virtual runtime_
- Exemplos de MRE:
  - JVM: Java Virtual Machine
  - .NET
  - JavaScript Runtime
  - Ruby Runtime


### Linguagem de Programação

- Sistema de notação para escrever programas de computador.
- Regras de sintaxe (forma) e semântica (significado) definidas por uma linguagem formal.
- Gap semântico:
  - Linguagem de máquina: computador consegue executar.
    - Diretamente montada (_assembly_) em código de máquina.
    - Depende fortemente da arquitetura (e.g., x86, ARM, MIPS).
  - Linguagem de alto nível: computador precisa traduzir para código de máquina.

### Tradução de Programas

- Tradução é realizada por _software_.
- **Compilador**: programa traduz o código em linguagem de alto nível para código de máquina.
  - Etapas comuns: 
    - Análise léxica
    - Análise sintática
    - Análise semântica
    - Geração de código intermédio
    - Otimização
    - Geração de código final (máquina)
  - Execução é feita depois, através do código final (executável) e ligação dinâmica (_late linker_).
    - Compilação _Ahead-of-time_.
  - Tende a ser mais eficiente, porém há pouca portabilidade.
  - Exemplos de compiladores:
    - gcc (para C), gfortran (para FORTRAN), g++ (para C++).
- **Interpretador**: programa traduz instrução sob demanda de uma linguagem de alto nível.
  - Etapas comuns:
    - _Parser_: análise léxica + sintática.
    - Avaliação: análise semântica + geração de código final (máquina) + execução.
- **Híbrido**: compilação + interpretação.
  - Compilação: geração de código intermédio, _e.g._ bytecode.
  - Interpretação: máquina virtual interpreta código intermédio.
- **Compilação _Just-in-time_** (JIT).
  - Interpretação de bytecode tende a ser ineficiente.
  - Melhoria: bytecode (geralmente em secções) então é compilado dinamicamente.

## Ecossistema Java

- Linguagem de programação Java.
- _Java Virtual Machine_ (JVM): executa bytecode Java.
  - Ficheiros com extensão `.class`.
- **Linguagem de programação JVM**:
  - Linguagem que pode ser compilada para um bytecode Java (`.class`). 
  - Qualquer linguagem de programação que uma JVM pode executar.
  - _e.g._: Java, Kotlin, Clojure, Groovy, Scala.
- Oracle tem a marca registada Java.
  - Desde a aquisição da Sun Microsystems em 2009.
  - Tem também o OpenJDK a servir como implementação de referência oficial.
- Há outros distribuidores de JVM prontas para produção.
  - Amazon: Corretto
  - Azul Systems: Zulu
  - Eclipse: Adoptium
  - IBM: Semeru
- JRE: _Java Runtime Environment_.
  - JVM
  - _core libraries_
- JDK: _Java Development Kit_
  - Extende a JRE.
  - Inclui ferramentas de desenvolvimento:
    - `javac`: compilador para ficheiro _class_,
    - `javap`: inspeção de bytecode,
    - `jmeter`: teste de desempenho,
    - entre outras.

## JVM

- Possíveis unidades de distribuição de _software_ na JVM:
  - único ficheiro _class_;
  - coleção de ficheiros class (_e.g._, dentro dum ficheiro JAR, `.jar`).
- Um ficheiro _class_ contém:
  - bytecode Java: instrução JVM,
  - tabela de símbolos,
  - outras informações suplementares.
- JVM é independente de plataforma.
- Processo de compilação e execução:
  - Código-fonte é compilado e então gerado o ficheiro _class_;
    - Compilador Java: `javac`
    - Compilador Kotlin: `kotlinc`
  - JVM executa ficheiros _class_.
    - Portabilidade: arquiteturas e sistemas operativos diferentes.
    - Interoperabilidade: executa qualquer programa gerado por linguagens JVM.

## Class Loader, CLASPATH e Interoperabilidade

- Compilador gera um `.class` para cada **tipo** definido no código-fonte.
- Um **tipo** refere-se a qualquer construção no código-fonte que define um tipo, como:
  - uma classe,
  - uma _interface_,
  - um enum,
  - um tipo anotação.

### Dependência

- Código exemplo em Java: `Foo.java`

```java
class X { public void print() { System.out.println("I am X"); }}
class Y { public void print() { System.out.println("I am Y"); }}
class Z { public void print() { System.out.println("I am Z"); }}
```

- Código da aplicação: `App.java`

```java
public class App {
  public static void main(String[] args) {
    System.out.println("Press ENTER to proceed.");
    System.console().readLine();
    if(args.length == 0) new X().print();
    else bar();
  }
  public static void bar() {
     Y someY = new  Y();
    someY.print();
  }

}
```
- Quando `Foo.java` é compilado por `javac Foo.java`, gera na diretoria corrente:
  - `X.class`
  - `Y.class`
  - `Z.class`

- Dependências em **tempo de compilação**:
  - Uma classe depende dos tipos que são referenciados em seu código-fonte.
  - `javac` resolve dependência a partir do CLASSPATH.
    - O CLASSPATH especifica a diretoria dos ficheiros `.class` necessários na compilação e execução.
    - Por omissão, o CLASSPATH é a diretoria corrente.
  - No exemplo:
    - `App.java` depende das classes `X.class` e `Y.class`.
      - `App.java` pode ser compilado por `javac App.java`.
      - `Z.class` não é dependência e poderia ser removido. 
- Dependências em **tempo de execução**:
  - Uma classe depende apenas dos tipos usados em seu fluxo de execução.
  - O **class loader** é responsável por carregar os tipos do ficheiro `.class` para a JVM.
    - _Delayed loading_: carrega apenas o que é necessário para garantir a execução do programa.
    - Tipicamente chamado pelo compilador JIT.

### Interoperabilidade: Exemplo entre Java e Kotlin

- Um programa escrito em Kotlin pode utilizar as classes do ficheiro `.class` gerados pelo Java.
  - E vice e versa.
- Exemplo de programa em Kotlin, `App.kt`:

  ```kotlin
  fun main() {
    println("Press ENTER to proceed.")
    readLine()
    X().print()
  }
  ```

- Quando `App.kt` é compilado com `kotlinc -cp . App.kt`:
  - Gera-se o ficheiro AppKT.class na diretoria local.
- `kotlinc` é o compilador de Kotlin.
  - O parâmetro `-cp .` indica o CLASPATH explicitamente.
  - Em `kotlinc`, a diretoria corrente não é o CLASSPATH por omissão. 
- Execução com a JVM:
  - Precisa indicar também no CLASSPATH os tipos _standards_ do Kotlin.
    - Por exemplo, no Ubuntu, costuma estar instalado em `/usr/share/kotlin/kotlinc/lib/kotlin-stdlib.jar`.
  - Comando: `java -cp '.:/usr/share/kotlin/kotlinc/lib/kotlin-stdlib.jar' AppKt`
    - `:` separa os dois _paths_: `.` e `/usr/share/kotlin/kotlinc/lib/kotlin-stdlib.jar`.
    - No Windows, o separador é `;` ao invés de `:`.

## Repositório Central e Gestão de Dependência

- **Repositórios Centrais**: armazenam bibliotecas externas tipicamente utilizadas em aplicações em desenvolvimento.
  - Para Java, o mais comum é o _Maven Central Repository_.
  - Para .NET, NuGet.
  - Para Javascript, NPM.
- **Gestor de dependências**: permite declarar, resolver, and expor bibliotecas necessárias para um projeto de software. 
  - Usam um repositório central de bibliotecas.
  - Além do Maven, **Gradle**, Ivy ou Grape também são usados para gerir dependências do Repositório Central do Maven.

### Gradle

- É um _build tool_ e um gestor de dependências para _software_.
  - Suporta linguagens como Java, Kotlin, Javascript e C/C++.
- Principais conceitos:
  ![https://docs.gradle.org/current/userguide/img/gradle-basic-1.png](https://docs.gradle.org/current/userguide/img/gradle-basic-1.png)
- _Task_: unidade básica de trabalho, como compilar código ou executar testes.
- Estrutura de projetos:
  - Considera projetos únicos ou multi-projetos.
  - Exemplo:
    ```tree
    project-name
    ├── gradle                              
    │   ├── libs.versions.toml              
    │   └── wrapper
    │       ├── gradle-wrapper.jar
    │       └── gradle-wrapper.properties
    ├── gradlew                             
    ├── gradlew.bat                         
    ├── settings.gradle.kts              
    ├── subproject-a
    │   ├── build.gradle.kts             
    │   └── src                             
    └── subproject-b
        ├── build.gradle.kts             
        └── src
    ```
- _Gradle build script_: ficheiro `app/build.gradle.kts`
- Principais secções de configuração:
  - **_plugins_**: usados para o processo de construção do programa.
  - **_repositories_**: define os repositórios externos para buscar as bibliotecas para a cache do projeto.
  - **_dependencies_**: as bibliotecas externas necessárias para o projeto.
    - _**implementation**_: utilizado ao compilar o código-fonte principal ou de teste e ao executar o projeto ou os testes. 
      - Estas dependências são adicionadas ao classpath do compilador e da JVM. 
    - _**testImplementation**_: apenas necessário para compilar código-fonte de teste e executar testes.