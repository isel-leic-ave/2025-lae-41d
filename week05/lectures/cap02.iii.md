# Noções Básicas de Tipo (parte III)

## Fundamentos de Tipo: Boxing e Unboxing

- _Boxing_: processo de copiar um valor de um tipo primitivo para um objeto de tipo de referência.
- A seguir, a relação de um tipo primitivo Java para a sua classe Wrapper correspondente. 

Tipo primitivo    | Classe wrapper
------------------|--------------
boolean           | Boolean
byte              | Byte
char              | Character
float             | Float
int               | Integer
long              | Long
short             | Short
double            | Double

- Cada classe wrapper tem um método explícito para fazer _boxing_.
  - `static Boolean valueOf(boolean p)`
  - `static Integer valueOf(int p)`
  - `static Double valueOf(double p)`
  - ...
- _Autoboxing_: processo implícito de conversão de valor de tipo primitivo para objeto duma classe _wrapper_ correspondente.
  - Java e Kotlin, por exemplo, têm esse comportamento.
  - Cenários em que ocorre o _autoboxing_:
    - Quando um valor primitivo é passado como parâmetro para um método que espera um objeto ou uma instância da classe _wrapper_ correspondente. 
    - Quando um valor primitivo é atribuído a uma variável do tipo objeto ou ao _wrapper_ correspondente classe.
- _Unboxing_: é o processo inverso, onde um objeto de tipo referência é convertido para um valor do tipo primitivo.
  - Acontece, por exemplo, no caso de uma operação aritmética.
  - O método explícito de conversão de um objeto de ripo referência para seu respetivo tipo primitivo é:
    - `boolean booleanValue()`
    - `int intValue()`
    - `double doubleValue()`
- Exemplos disponíveis em: [week5/sample12-boxing](../sample12-boxing)
  - Em Java e Kotlin.

## Fundamentos de Tipo: Tipos Aninhados

- Classes aninhadas (_nested classes_): Java permite definir classes dentro de outra classe.
  - [Documentação](https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html).
- Dois tipos de classes:
  - estática: classe estática aninhada (_static nested class_).
  - não estática: classe interna (_inner class_).

  ```java
  class Outer {
      //...
      class Inner {
          //...
      }
      static class StaticNested {
          //...
      }
  }
  ```
- Uma classe aninhada (_e.g._, InnerClass ou StaticNested) é um membro da sua classe envolvente (_e.g._, Outer).
- As classes internas (não estáticas) têm acesso a outros membros da classe envolvente.
  - Mesmo que sejam declaradas privadas.
- As classes aninhadas estáticas não têm acesso a outros membros da classe envolvente. 
- Uma classe aninhada pode ser declarada como `private`, `public`, `protected` ou _default_.
- As classes externas ao aninhamento só podem ser declaradas como públicas ou _default_.
  - E não podem ser estáticas.
- Exemplos disponíveis em: [week05/sample13-nested-types](../sample13-nested-types)
  - Analisar as classes `A.class`, `A$B.class`, `Outer.class`, `Outer$Inner.class` e `Outer$StaticNested.class`,  com os comandos:
    - `javap -p <class>`, para ver os membros.
    - `javap -v <class>`, _verbose_ dos metadados.

## Tipos Base e Tipos Abstratos

- **Classes abstratas** e **interfaces** representam tipos abstratos.
  - Não podem ser diretamente instanciados.
- Comparação de declaração de classes abstratas e interfaces entre Java e Kotlin:
  - Java:
    ```java
    public abstract class A { public abstract void foo(); }
    public interface I { abstract void foo(); }
    ```
  - Kotlin:
    ```kotlin
    abstract class A { abstract fun foo() }
    interface I { fun foo() }
    ``` 

- Principais diferenças entre interfaces e classes abstratas em Java:
  1) Classes abstratas podem ter declarações de campos _per-instance_.
     - Todos os campos declarados numa interface são `static final`.
  2) Todos os métodos de uma interface são abstratos.
     - Logo, não podem ter implementação do corpo.
     - A partir do [Java 8](https://www.oracle.com/java/technologies/javase/8-whats-new.html), existe o modificador de um método de interface chamado `default`.
       - Ele permite incluir o corpo no método.
     - Nas classes abstratas pode haver métodos não abstratos (com corpo).
       - Métodos abstratos devem ter o modificador `abstract`.
  3) Interfaces não podem definir construtores, mas classes abstratas sim.
- Exemplo das diferenças entre classe abstrata e interface em Java: 
  - [week05/sample14-abstract-types/java/App.java](../sample14-abstract-types/java/App.java)
- Se inspecionarmos os metadados de uma classe abstrata e de uma interface com a ferramenta `javap -p`, temos que:
  - Na classe abstrata há sempre um construtor, mesmo que ele não seja declarado.
  - Na interface não há construtores, os campos são `public static final` e os métodos não _default_ são `public abstract`.

### Instância de um Tipo Abstrato

- Para instanciar um objeto indiretamente de uma classe abstrata, é necessária uma classe que a estenda.
  - Herança através da palavra reservada `extends`.
  - Para chamar construtor ou campo da superclasse, usar palavra reservada `super`.
- Para instanciar um objeto a partir de uma interface, é necessária uma classe que a implemente.
  - Relação realizada através da palavra reservada `implements`.
- Exemplo em Java:
  ```java
  class CA extends A { public void foo() {}}
  class CI implements I { public void foo() {}}
  ```
- Exemplo em Kotlin:
  - É necessária a anotação com a palavra reservada `override` na declaração das funções.
  ```kotlin
  class CA : A() { override fun foo() {} }
  class CI : I { override fun foo() {} }
  ```
- Se membros da superclasse (classe base) e subclasse têm mesmo nome, há ambiguidade.
  - Pode-se usar o `super` e `this` na subclasse para resolver a ambiguidade.
  - No caso de um objeto externo às classes (base e subclasses), o acesso entre os membros difere-se:
    - Os campos resolvem-se pelo tipo base.
    - Os métodos resolvem-se pelo tipo do objeto que foi definido na instanciação.
  - No exemplo em [week05/sample15-virtual-methods/App.java](../sample15-virtual-methods/App.java):
    - O campo `name` no método `printName(Person obj)` refere-se ao tipo base.
      - No primeiro caso, o tipo do objeto é `Person` e o valor impresso é _Maria_.
      - No segundo caso, o tipo do objeto é `Student` e o valor impresso é _null_ (pois `name` da classe base foi definido como _null_ na subclasse).
    - O método `print()`, que é sobrescrito, refere-se ao tipo do objeto.
      - No primeiro caso, o tipo do objeto é `Person` e o `print()` corresponde a `Person`.
      - No segundo caso, o tipo do objeto é `Student` e o `print()` corresponde a `Student`.

### Sobrecarga e Sobrescrita de Métodos

- **Parâmetros formais**: definição formal das entradas de um método.
- **Argumentos**: valores associados aos parâmetros de um método.
- **Sobrecarga** (_overload_): métodos podem ter mesmo nome, mas parâmetros formais diferentes.
  - Podem diferir na quantidade de parâmetros e sequência de tipos definidos.
- **Sobrescrita** (_override_): numa herança, métodos da subclasse podem ter mesmo nome e parâmetros formais e, portanto, sobrescrevem/substituem métodos da superclasse.
  - Métodos que sobrescrevem métodos da superclasse são chamados de **métodos virtuais**.
- No exemplo em [week05/sample15-virtual-methods/App.java](../sample15-virtual-methods/App.java), o método `print()` é virtual.

## Classes Anónimas

- **Classes anónimas**: classes sem nome usadas para declarar a classe e instanciar um objeto desta classe ao mesmo tempo.
- Exemplo em Java (usando a classe abstrata `A` e interface `I` dos exemplos anteriores):
  ```java
  public class App {
    public static void main(String[] args) {
      final A ca = new A() {
         public void foo() {}
      };
      final I ci = new I() {
         public void foo() {}
      };
    }
  }
  ```
- Exemplo equivalente em Kotlin:
  - Com o uso de expressões `object`.
  ```kotlin
  fun main() {
    val ca: A = object : A() {
       override fun foo() { }
    }
    val ci: I = object : I {
       override fun foo() { }
    }
  }
  ```
- Em Java, para cada classe anónima, o compilador gera as classes referentes a `ca` e `ci`.
  - `App$1.class`
  - `App$2.class`
  - É o nome da classe onde as classes anónimas são definidas com o sufixo `$` e um número cardinal a começar por 1.
-  Em Kotlin, para cada classe anónima, o compilador gera as classes geradas são:
  - `AppKt$main$ca$1.class`
  - `AppKt$main$ci$1.class`
  - É similar a Java, mas também inclui o nome da função e das variáveis (constantes, no caso).
- Em [week05/sample16-anonymous-types/java/](../sample16-anonymous-types/java/) há outro exemplo de classes anónimas em Java.
- Em [week05/sample16-anonymous-types/kotlin/](../sample16-anonymous-types/kotlin/) há o mesmo exemplo de classes anónimas em Kotlin.