# Noções Básicas de Tipo (parte I)

- Agora, vamos formalizar vários conceitos sobre o **sistema de tipos da JVM**.
- Isto irá ajudar-nos a compreender vários aspetos da Reflexão, das linguagens JVM e do funcionamento da JVM.

## Sistema de Tipos

- **Sistema de Tipos**: conjunto de regras e princípios que especificam o modo como os tipos são definidos e se comportam.

## Fundamentos de Tipo

- Duas categorias de tipos:
  - Tipos primitivos
  - Tipos de referência

### Tipos Primitivos

- São os tipos mais básicos, também denotados como tipos _built-in_.
  - Não são objetos.
- Tipos numéricos: 
  - Integrais (`char`, `byte`, `short`, `int`, `long`),
  - Ponto flutuante (`float` e `double`).
- Tipo `boolean`.
- São representados por **literais** no código-fonte.
  - _e.g._, `'s'`, `42`, `1L`, `9.1f`, `true`

### Tipos de Referência

- 3 principais categorias de tipos de referência:
  1) **array**: o tipo representa uma referência para um array.
  2) **class**: o tipo representa uma referência para uma instância de uma classe.
  3) **interface**: o tipo representa uma referência para uma instância de uma classe que implementa a interface.
  
#### Array

- É uma estrutura de dados única da JVM que permite armazenar múltiplos valores do mesmo tipo num único contentor.
- Todos os componentes de um array têm o mesmo tipo, nomeadamente **tipo componente**.
  - Os componentes não têm nomes.
  - São acessados através de valores inteiros de índice que variam de 0 a _n-1_, onde _n_ é o número de componentes.
- Os tipos componentes podem ser outro tipo array.
  - O último tipo componente é chamado de **tipo elemento**.
- Exemplo de representação de um array de inteiros em Java: `int[] arr`
  - É um tipo de referência array cujo tipo componente (e elemento) é o tipo primitivo `int`.
- Em: `int[][] mat`
  - O tipo componente é `int[]`.
  - O tipo elemento é `int`.
  - Isto indica que `mat` é um array de arrays de `int`.
- O tipo de elemento de um tipo array é o tipo de dado fundamental armazenado no array (de qualquer dimensão).
  - Pode ser um tipo primitivo, um tipo classe ou um tipo interface.

#### Objeto

- Um **objeto** é uma instância de um tipo de referência.
- No entanto, o tipo declarado de um objeto nem sempre é o tipo de referência do objeto.
  - Isto acontece quando a classe/interface do tipo de referência herda direta ou indiretamente da classe/interface declarada.
  - No exemplo a seguir, `obj` é um objeto declarado com o tipo `A` (superclasse), mas o tipo de referência referencia um objeto do tipo `B`.
  ```kotlin
  open class A {}
  
  class B : A() {}
  
  fun main(){
    val obj: A = B()
  }
  ```
- Todo objeto tem um construtor.
  - Exceto objetos do tipo array.

#### Nomes de Tipos

- Os nomes dos tipos são constituídos de duas partes:
  - um nome de pacote opcional e
  - nome local.
- Considere o seguinte exemplo (ficheiro `Person.java`):
```java
package pt.isel;
class Person {}
```
- Se compilado com o comando (terminal): `javac Person.java`
- Pode-se verificar que o nome totalmente qualificado da classe é `pt.isel.Person`.
  - Através do comando: `javap -p Person.class`
  ```java
  class pt.isel.Person {
    pt.isel.Person();
  }
  ```

### Membros

- Um tipo JVM pode conter 0 ou mais **membros**.
  - Um tipo classe, por exemplo, inclui pelo menos um membro, o construtor.
- Os membros definem como um tipo pode ser utilizado e como ele funciona.
- Os membros podem ser:
  - Campos (_Fields_) ou
  - Métodos.
- Não existem propriedades no sistema de tipos da JVM.
  - Propriedades são essencialmente métodos com metadados adicionais.
  - Em Kotlin, por exemplo, uma propriedade tem um campo associado que não pode ser diretamente acessado.
- Acessibilidade de membros:
  - Cada membro é associado a modificados, como `public`, `private` e `protected`.
- _Per-instance_ _vs._ _Per-type_:
  - Membro _per-instance_: requer uma instância para aceder ao membro. 
    - É um membro da instância (objeto).
    - Um _receiver_ é um objeto no qual um membro de instância está a ser acessado.
  - Membro _per-type_: não é usada uma instância e o membro é acedido diretamente pelo tipo.
    - É um membro do tipo.
  - Em Java, membros são _per-instance_ por omissão.
    - Para torná-los _per-type_, o tipo deve ser precedido pela palavra-chave `static` na declaração do membro.
  - Ver exemplo em [week04/sample08-packages-and-init/pt/isel/App.java](../sample08-packages-constants/pt/isel/App.java).
    - Compilação (a partir de `week04/sample08-packages-and-init`): `javac pt/isel/App.java` 
    - Execução (a partir de `week04/sample08-packages-and-init`): `java pt.isel.App`
    - `perTypeMethod()` é um método declarado como _static_.
      - Pode ser chamado por `App.perTypeMethod()`
      - Ou por `perTypeMethod()`, se dentro da classe.
    - `perInstanceMethod()` precisa de um objeto _receiver_ para ser invocado.
      - Em Java, um objeto é criado através da palavra-chave `new`.
      - `new App("app1")` instancia um primeiro objeto.
      - `new App("app2")` instancia um segundo objeto.
    - O método especial `main` é o método que inicia a execução do programa.
      - Note que ele deve ser `static`.

#### Campos (Fields)

- Um campo é uma unidade de armazenamento identificada por nome e com um tipo declarante.
- Essencialmente, os campos controlam como a memória é alocada.
  - Campos estáticos (_static_) são alocados apenas uma vez.
  - Campos _per-instance_ são alocados sempre que um objeto é criado.
  - Campos estáticos são inicializados para o seu valor _default_.
    - Pode ser inicializado na declaração;
    - Para tipo numérico, o valor _default_ é `0`;
    - Para tipo boolean, the valor _default_ é `false`;
    - Para referências a objetos, o valor _default_ é `null`.

#### Campos imutáveis
- **Constantes**: valores dos campos são calculados em tempo de compilação.
  - Valores não podem ser alterados posteriormente.
- Em Java, usa-se a palavra reservada `final` como modificador do membro.
- Considere o exemplo:
  ```java
  package pt.isel;
  class Constants {
      final int BITS_OF_10KB = 8 * 10 * 1024;
  }
  ```
- A análise com o comando `javap -v pt.isel.Constants` mostra que o resultado da operação já se encontra computado (81920).
  ```text
  final int BITS_OF_10KB;
    descriptor: I
    flags: (0x0010) ACC_FINAL
    ConstantValue: int 81920
  ```
- **Campos _write-once_**:
  - Todo campo `final` deve ser inicializado antes do construtor completar.
    - Se também for `static`, precisa ser inicializado na declaração do campo.
      - Pois um campo estático está associado ao tipo classe onde construtor não faz sentido.
      - Também pode ser inicializado num bloco inicializador estático.
  - Em resumo, há 3 opções de inicialização de um campo apenas `final`:
    - na declaração,
    - no construtor,
    - num bloco inicializador de instância.
  - Para um campo `static final`, há 2 opções de inicialização:
    - na declaração,
    - num bloco inicializador estático.
  - O exemplo em [week04/sample08-packages-constants/pt/isel/Constants.java](../sample08-packages-constants/pt/isel/Constants.java) mostra o uso desses casos.
    - Aos campos `created` e `createdStatic` são associados valores correntes do tempo através de `System.currentTimeMillis()`.  
    - No método `main`, adicionalmente, é usado o `Thread.sleep(3000)` para esperar 3 segundos.
      - O objetivo é mostrar que, uma vez que os campos são afetados, eles não são alterados posteriormente, mesmo após os 3s.
      - Por isso, os campos são caracterizados como _write-once_.