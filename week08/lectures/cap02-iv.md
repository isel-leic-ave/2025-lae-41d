# Noções Básicas de Tipo (parte IV)

- Esta parte relaciona o sistema de tipos da linguagem Kotlin ao sistema de tipos de Java. 

## Kotlin e o Sistema de Tipos de Java

- Kotlin distingue tipos _nullable_ e tipos não _nullable_.
  - Por omissão, um tipo é não nulo (_null safety_).
  - Para tornar os tipos nulos, deve-se usar `?` após o nome do tipo.
- Um tipo não _nullable_ pode ou não ser um tipo primitivo na JVM.
- Um tipo _nullable_ corresponde obrigatoriamente a um tipo de referência na JVM. 
- A seguinte tabela corresponde os tipos wrapper Java (dos tipos primitivos) e os tipos Kotlin _built-in_:

| Tipos wrapper Java | Tipo Kotlin |
|--------------------|-------------|
| Boolean            | Boolean?    |
| Byte               | Byte?       |
| Character          | Char?       |
| Float              | Float?      |        
| Integer            | Int?        |       
| Long               | Long?       |       
| Short              | Short?      |       
| Double             | Double?     |     

### Membros de Classe em Kotlin

- Conforme já visto, existem apenas dois tipos fundamentais de membros na JVM:
  - Campos (_fields_),
  - Métodos (_methods_).

#### Propriedades

- Não há propriedades na JVM: as propriedades são transformadas em campos e métodos.
  - _backing field_ (privados),
  - Métodos _getter_,
  - Métodos _setter_ (se mutável).
- Considere a seguinte classe em Kotlin com uma constante `name`:
  ```kotlin
  class Person (val name: String)
  ```
- O resultado do comando `java -p Person.class` é:
  ```javap
  public final class Person {
    private java.lang.String name;
    public Person(java.lang.String);
    public final java.lang.String getName();
  }
  ```
  - `private java.lang.String name`: é o _backing field_ privado;
  - `public final java.lang.String getName()`: é o método _getter_ do campo `name`.
    - Note que o método é `final`, _i.e._, não pode ser sobrescrito.
    - Por omissão, os métodos correspondentes aos _backing fields_ são `final`.
    - Para remover o `final`, deve-se usar o `open` antes da propriedade.
- Considere agora a seguinte classe em Kotlin com uma **variável** `name`:
  ```kotlin
  class Person (var name: String)
  ```
- O resultado do comando `java -p Person.class` é:
  ```java
  public final class Person {
    private java.lang.String name;
    public Person(java.lang.String);
    public final java.lang.String getName();
    public final void setName(java.lang.String);
  }
  ```
  - `public final void setName(java.lang.String)`: é o método _setter_ do campo `name`.
    - Isto porque agora a propriedade `name` é mutável.

#### Sintaxe de uma Propriedade

- Sintaxe de uma propriedade mutável:
  ```text
  var <propertyName>[: <PropertyType>] [= <property_initializer>]
    [<getter>]
    [<setter>]
  ```
- Sintaxe de uma propriedade imutável:
  ```text
  val <propertyName>[: <PropertyType>] [= <property_initializer>]
    [<getter>]
  ```
- São opcionais:
  - o tipo da propriedade se ele puder ser inferido;
  - o inicializador da propriedade;
  - os **acessores** _getter_ e _setter_.

#### Acessores Personalizados  

- A linguagem Kotlin permite ao programador personalizar os métodos _setter_ e _getter_:
  ```kotlin
  class Person(name: String, val surname: String) {
    var name: String = name
      set(value) {
        require(value.length <= 70)
        field = value
      }
      get(){
        return field.lowercase().replaceFirstChar{it.uppercase()}
      }
  }
  ```
  - Este exemplo modifica o _setter_ para aceitar apenas strings com tamanho menor ou igual a 70.
  - Também modifica o _getter_ para retornar o nome sempre com as letras em minúsculas, exceto a primeira.

- **Identificador `field`**: refere-se ao _backing field_ (_e.g._, `name`).
  - Este identificador só pode ser usado nos acessores: _getter_ e _setter_.

- Um _backing field_ é gerado para uma propriedade que utiliza a implementação padrão de pelo menos um dos acessores (_getter_, _setter_).
  - Ou se um accessor personalizado referenciá-lo através do identificador `field`.
- No seguinte exemplo, não é gerado um _backing field_ para `fullName`: 
  ```kotlin
  class Person(val name: String, val surname: String) {
      val fullName: String 
          get() {return "${name} ${surname}"}
  }
  ``` 
- Isto pode ser verificado através da saída de `javap -p Person.class`:
  ```java
  public final class Person {
    private final java.lang.String name;
    private final java.lang.String surname;
    public Person(java.lang.String, java.lang.String);
    public final java.lang.String getName();
    public final java.lang.String getSurname();
    public final java.lang.String getFullName();
  }
  ```
- Neste exemplo, não há _backing field_ para `fullName` porque esta propriedade:
  - não tem _setter_ (é `val`);
  - não utiliza a implementação padrão do _getter_;
  - não utiliza _field_ na implementação do _getter_.

#### Declarações Top-level

- As declarações _top-level_ são mapeadas para os membros de uma classe correspondente ao nome do ficheiro proprietário com o sufixo `Kt`.
- Exemplo:
  ```kotlin
  const val DEFAULT_TIMEOUT = 5000
  const val APPLICATION_NAME = "MyApp"

  fun isEven(number: Int): Boolean {
     return number % 2 == 0
  }
  ```
- A compilação gera o ficheiro com o bytecode `AppKt.class`.
- A saída de `javap -p AppKt.class` é:
  ```java
  public final class AppKt {
    public static final int DEFAULT_TIMEOUT;
    public static final java.lang.String APPLICATION_NAME;
    public static final boolean isEven(int);
  }
  ```
- As declarações _top-level_ fazem parte da classe `AppKt`.
  - No caso de uma função de extensão, o resultado da saída do `javap -p` é o mesmo:
    ```kotlin
    const val DEFAULT_TIMEOUT = 5000
    const val APPLICATION_NAME = "MyApp"

    fun Int.isEven(): Boolean {
    return this % 2 == 0
    }
    ```
  - A diferença é apenas indicada pelo parâmetro de `isEven()`.
    - O parâmetro da função regular é `number`.
    - O parâmetro da função regular é `this` e `$this$isEven` é corresponde ao nome do parâmetro no bytecode.
      - Isto pode ser visto com o comando: `javap -v`.

### Declaração de Objeto

- O Kotlin oferece uma forma eficiente de declarar um _singleton_.
- _Singleton_ é um padrão que restringe a instanciação de uma classe e garante que apenas uma instância da classe existe.
- Em Kotlin, isto é feito através da palavra reservada `object`.
  - Isto permite definir uma classe e criar uma instância da mesma numa única vez.
- Vamos examinar a seguinte declaração de objeto (e):
  ```kotlin
  object ElapsedTimeCalculator {
      private val startTime: Long = System.currentTimeMillis()
      fun elapsedTime(): Long {
          val currentTime = System.currentTimeMillis()
          return currentTime - startTime
      }
  }
  ```
- O resultado do comando `javap -p ElapsedTimeCalculator.class` é:
  ```java
  public final class ElapsedTimeCalculator {
    public static final ElapsedTimeCalculator INSTANCE;
    private static final long startTime;
    private ElapsedTimeCalculator();
    public final long elapsedTime();
    static {};
  }
  ```
- Este resultado mostra que:
  - Há um campo `startTime` conforme a propriedade declarada.
    - Ele é tratado como constante da classe `static final`.
  - Há um método `elapsedTime()` conforme a função declarada.
  - Há uma constante `static final` chamada `INSTANCE` do tipo `ElapsedTimeCalculator`.
    - Esta constante irá guardar a única instância existente.
  - O método construtor é **privado**, de forma que é proibido instanciar a classe fora dela mesma.
  - Há um bloco estático denotado por `static {}`.
    - É usado para inicializar os campos da classe (estáticos), como o `INSTANCE`.

#### _Companion Object_

- É um tipo específico de declaração de objeto, mas é realizada dentro da própria classe.
- Um _companion object_ pode ser anónimo.
- Os seus membros são como os membros estáticos em Java.
- A seguir, um exemplo em Kotlin para representar as mesmas características da classe [Account.java](../../week05/sample09-modifiers/apps/Account.java) em Java.

```kotlin
class Account {
  private val created: Long
    get() = field // <=> get() { return field }

  init { // Initializer block: is called after the primary constructor
    Account.numberOfAccounts++ // <=> Account.numberOfAccounts++
    created = System.currentTimeMillis()
  }

  companion object {
    var numberOfAccounts = 0
      private set // excludes the setter method
  }
}
```
- Comparação das saídas de `javap -p Account.class` para o bytecode de [Account.kt](../../week08/sample19-properties/Account.kt) e [Account.java](../../week05/sample09-modifiers/apps/Account.java):
  - Java:
    ```java
    public class Account {
      private static int numberOfAccounts;
      private final long created;
      public static int getNumberOfAccounts();
      public Account();
      public long getCreated();
      static {};
    }
    ```
  - Kotlin:
    ```java
    public final class Account {
      public static final Account$Companion Companion;
      private final long created;
      private static int numberOfAccounts;
      public Account();
      private final long getCreated();
      public static final int access$getNumberOfAccounts$cp();
      static {};
    }
    ```
    - As principais diferenças são:
      - Em Kotlin, há um campo adicional `Account$Companion Companion`, que é um campo estático.
        - Ele guarda a instância _singleton_ da classe `Account$Companion` (mostrada a seguir).
      - Em Kotlin, o método `getNumberOfAccounts()` é traduzido para `access$getNumberOfAccounts$cp()`. 
        - Ele se refere ao método de acesso `getNumberOfAccounts()` em `Account$Companion`.
    - A classe `Account$Companion` possui um construtor privado e um público.
    ```java
    public final class Account$Companion {
      private Account$Companion();
      public final int getNumberOfAccounts();
      public Account$Companion(kotlin.jvm.internal.DefaultConstructorMarker);
    }
    ```

### Classes de Dados

- As classes de dados (_data class_) em Kotlin são utilizadas para armazenar dados e têm os seguintes requisitos:
  - O construtor primário deve ter pelo menos um parâmetro.
  - Todos os parâmetros do construtor primário devem ser marcados como _val_ ou _var_.
  - Não podem ser abstratas, _inner_ ou serem herdadas.
- Elas são marcadas com a palavra reservada `data`.
  ```kotlin
  data class UserData(val name: String, val age: Int)
  ```
- Para cada classe de dados, o compilador gera automaticamente funções adicionais:
  - sobrescrita da função `toString()`: permite imprimir a instância com uma saída legível;
  - sobrescrita da função `hashcode()`: código _hash_ é baseado apenas nos dados;
    - se os dados são iguais, o código hash é o mesmo;
  - sobrescrita da função `equals()`: compara as instâncias baseada nos _hashes_;
  - função `copy()`: faz cópia da instância permitindo alterar parte dos dados (ou todos);
  - funções `componentN()`: uma função _component_ para cada parâmetro do construtor primário.
- Isto pode ser observado pela saída de `javap -p UserData.class`:
  ```java
  public final class UserData {
    private final java.lang.String name;
    private final int age;
    public UserData(java.lang.String, int);
    public final java.lang.String getName();
    public final int getAge();
    public final java.lang.String component1();
    public final int component2();
    public final UserData copy(java.lang.String, int);
    public static UserData copy$default(UserData, java.lang.String, int, int, java.lang.Object);
    public java.lang.String toString();
    public int hashCode();
    public boolean equals(java.lang.Object);
  }
  ```
 - O ficheiro [week08/sample19-properties/data-class/UserApp.kt](../../week08/sample19-properties/data-class/UserApp.kt) apresenta exemplos de uso das funções adicionadas pelo compilador.
   - Contém também um exemplo com uma classe regular chamada `UserClass` para fins de comparação.
   - As classes podem ser compiladas pelo comando `kotlinc UserApp`.
   - A execução do programa é dada por `kotlin UserAppKt`.
   - Adicionalmente, é um bom exercício comparar os resultados de `javap -p UserClass.class` e `javap -p UserData.class`. 

### Tipos Função

- Funções em Kotlin são de **primeira ordem** (_high-order functions_).
- **Funções regulares**: são as funções com nome declarado.
  ```kotlin
  fun sum1(a: Int, b: Int): Int {
    return a + b
  }
  ```
- **Literais de funções**: são funções não declaradas, que são passadas imediatamente como uma expressão.
  - **Expressão _lambda_**: usa a notação _arrow_ e a expressão é envolvida por chavetas.
    - No estilo `A, B -> C`.
    - _e.g._, `{ a: Int, b: Int -> a + b }`
    - Pode vir anotado (prefixo) por `(Int, Int) -> Int`, que é opcional.
      - e.g., `val sum : (Int, Int) -> Int = { a: Int, b: Int -> a + b }`
  - **Funções anónimas**: parece uma função regular, mas sem nome.
    - _e.g._, `fun(a: Int, b: Int): Int { return a + b }`
  - **Literais de funções com recetor** (_receiver_): no estilo `A.(B) -> C`, similar a uma função de extensão.
    - _e.g._, `Int.(Int) -> Int = { b -> this + b }` (ou `Int.(Int) -> Int = { this + it }`, para os casos de apenas um parâmetro).
    - _e.g._, `fun Int.(b: Int): Int = this + b`
- Os literais de funções são anónimos.
  - Para cada função anónima, o compilador gera o método `functionName$lambda$N(...)` em seu _bytecode_.
    - `functionName`: é o nome da função onde a função anónima foi declarada.
      - Pode ser composto por mais nomes separados por `$`, caso seja uma função aninhada.
    - `lambda`: marca que a função é anónima.
    - `N` é um número identificador da função.
- O código em [week08/sample20-function-types/App.kt](../../week08/sample20-function-types/App.kt) apresenta a declaração de funções variadas que realizam a soma de dois inteiros.
  1) sum1: função regular.
  2) sum2: função regular com uma única expressão.
  3) sum3: função anónima.
  4) sum4: função _lambda_ (há também o exemplo `concat()`).
  5) sum5: literais de função com receiver usando função anónima.
  6) sum6: literais de função com receiver usando expressão lambda.
- **Funções aninhadas**: funções declaradas dentro de outras.
  ```kotlin
  fun funcOuter() {
    fun funcInner1() {
        fun funcInner2() {
            // ...
        }
        // ...
    }
    // ...
  }
  ```
- Podemos analisar o bytecode destas funções através do resultado do comando `javap -p AppKt.class`:
```java
public final class AppKt {
  public static final int sum1(int, int);
  public static final int sum2(int, int);
  public static final void funcOuter();
  public static final void main();
  public static void main(java.lang.String[]);
  private static final void funcOuter$funcInner1$funcInner2();
  private static final void funcOuter$funcInner1();
  private static final long main$lambda$0(int, int);
  private static final int main$lambda$1(int, int);
  private static final java.lang.String main$lambda$2(java.lang.String, java.lang.String);
  private static final int main$lambda$3(int, int);
  private static final int main$lambda$4(int, int);
}
```
