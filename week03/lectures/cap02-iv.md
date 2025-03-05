# Reflection (parte IV)

## Anotações

- As anotações em Kotlin e Java são uma forma de adicionar metadados ao código-fonte.
- Elas são utilizadas para fornecer informações adicionais que podem ser processadas em tempo de compilação ou em tempo de execução por _frameworks_, bibliotecas ou ferramentas de análise.
    - _e.g._, JUnit (testes), Spring (_framework_ para aplicações Java Enterprise).
- Anotações são fortemente tipadas.
- Anotações de Kotlin são compatíveis com anotações do Java.
    - Cada anotação é uma instância de um tipo que herda indiretamente de `java.lang.annotation.Annotation`.
    - Exemplo do `.class` correspondente à anotação `@Test` do JUnit:
      ```kotlin
      public interface org.junit.Test extends java.lang.annotation.Annotation { … }
      ```
    - `org.junit.Test` é um tipo que herda diretamente da interface `java.lang.annotation.Annotation`

### Anotações Built-in

- Algumas anotações comuns no Kotlin incluem:
    - `@JvmField`: Exclui um _getter/setter_ em um campo de um objeto, permitindo acesso direto.
        - Relativo a como as propriedades de Kotlin são compiladas (uso de _getters/setters_ e campos privados).
        - [Documentação sobre interoperabilidade entre Kotlin e Java.](https://kotlinlang.org/docs/java-to-kotlin-interop.html#properties)
    - `@JvmStatic`: Faz com que um membro de uma classe possa ser referenciado como um método estático (método pertence à classe e não à instância).
    - `@Deprecated`: Indica que um elemento não deve ser utilizado.
    - `@Volatile`: garante que um elemento será atualizado atomicamente (no contexto de concorrência).

### Definição de Anotações pelo Utilizador

- Em Kotlin, usa-se a palavra-chave `annotation class` seguida do nome da anotação.
- Exemplo: Autoria de uma função.
  ```kotlin
  @Retention(AnnotationRetention.RUNTIME)
  @Target(AnnotationTarget.FUNCTION)
  annotation class Autor(val nome: String, val data: String)
  ```
- Em Java, usa-se `@interface` seguido do nome da anotação.
  ```java
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  public @interface Autor {
    String nome();
    String data();
  }
  ```
- Exemplo, em Kotlin, da definição de uma anotação chamada `MapProp` para o domínio _Artist_.
    - Objetivo é mapear o nome de propriedades.
  ```kotlin
  @Target(AnnotationTarget.PROPERTY)
  @Retention(AnnotationRetention.RUNTIME)
  annotation class MapProp(val paramName: String)
  ```

### Componentes da Anotação

- `@Target`: define os tipos de elementos que podem usar a anotação (_e.g._: CLASS, FUNCTION, PROPERTY).
    - [Lista completa dos possíveis _targets_](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.annotation/-annotation-target/).
- `@Retention`: especifica se a anotação é armazenada nos ficheiros de classe compilados e se é visível através de reflexão em tempo de execução.
    - `RUNTIME`: anotações estão disponíveis em tempo de execução permitindo o uso de API de reflexão.
    - `SOURCE`: estão disponíveis apenas no código-fonte.
        - Usado para geração de código ou ferramentas de análise estáticas.
    - `CLASS/BINARY`: anotações são incluídas no _bytecode_ mas não estão disponíveis em tempo de execução.
- Parâmetros: as anotações podem ter parâmetros com valores padrão.

### Uso de Anotações

- As anotações podem ser aplicadas em diversas partes do código, assim como em Java.
- Exemplo de uso da anotação de autoria de uma função como dois parâmetros:
  ```kotlin
  class Teste {
      @Autor(nome = "Maria", data = "05/03/2025")
      fun metodoImportante() {
          // Implementação
      }
  }
  ```
- Exemplo de uso da anotação de uma propriedade no domínio _Artist_:
  ```kotlin
  class ArtistSpotify(
      val name: String,
      @MapProp("from") val country: String,
      val kind: String
  )
  ```

### Reflexão

- `KAnnotatedElement` representa um elemento que pode ser anotado.
  - `KClass`, `KCallable`, `KParameter`, `KType` herdam de `KAnnotatedElement`. 
- Algumas funções e propriedades de um elemento:
    - `annotations: List<T>`: retorna todas as anotações do elemento.
    - `findAnnotation(): T?`: retorna uma anotação de tipo especificado do elemento (ver exemplo).
    - `hasAnnotation(): Boolean`: indica se há uma anotação para um elemento.
- Exemplo: retornar a anotação "Autor" dos métodos que a possuem.
  ```kotlin
  import kotlin.reflect.full.findAnnotation
  
  fun main() {
      val metodos = Teste::class.members
      for (metodo in metodos) {
          val autor = metodo.findAnnotation<Autor>()
          if (autor != null) {
              println("Método: ${metodo.name}, Autor: ${autor.nome}, Data: ${autor.data}")
          }
      }
  }
  ```

### Exemplo NaiveMapper: Implementação III

- Eliminar a última restrição: as propriedades de origem e destino partilham o mesmo nome e tipo.
- A ideia é criar uma anotação para mapear os nomes da propriedade.
    - As propriedades `from` e `country`.
- Por exemplo, a classe ArtirtSpotify contém a anotação `@MapProp("from")` para a propriedade `country`.
  - Ao obter esta anotação, sabe-se então que `country` é o mesmo que `from`.
- No código do NaiveMapper, basta então substituir a verificação do nome, na Implementação II, de:
  ```kotlin
  destParam.name == it.name && destParam.type == it.returnType
  ```
- Para:
  ```kotlin
  ( destParam.name == (it.findAnnotation<MapProp>()?.paramName ?: it.name)
    && destParam.type == it.returnType )
  ```
    - `it` corresponde ao objeto de origem, no caso, do tipo `ArtistSpotify`.
    - `destParam` corresponde ao **parâmetro do construtor** da classe de destino, no caso, `Artist`.
- Implementação completa está disponível em [sample06-naivemapper-annotations](../sample07-naivemapper-recursive-and-generics)
    - Inclui testes com JUnit agora comparando todas as propriedades.

### Alvos Use-site

- As anotações podem variar consoante o tipo de elemento.
  - _e.g._, propriedade, parâmetro do construtor, campos (_fields_), entre outros.
- Pode-se, portanto, especificar o alvo _use-site_ (_use-site target_) para um elemento.
  - Uso do alvo antes da anotação com a seguinte sintaxe: `@alvo:Anotacao`.
  - Dois exemplos de alvos:
    - `property`: propriedade,
    - `param`: parâmetro do construtor.
  - Por exemplo, uma anotação que deve ser aplicada apenas à propriedade quando o elemento corresponde também a um parâmetro do construtor:
    ```kotlin
    class Account(
        @property:Color("Blue") @Color("Yellow") var balance: Long,
    )
    ```
- Por omissão, a ordem de aplicação dos alvos é: 
  - `param`, 
  - `property`
- No Exemplo apresentado:
  - A anotação `Color("Blue")` é aplicada à propriedade `balance` (acessível ao `memberProperties`).
  - A anotação `Color("Yellow")` é aplicada ao parâmetro do construtor (acessível ao `constructors`).
- Por exemplo, considere o seguinte código:
  ```kotlin
  Account::class
    .declaredMemberProperties
    .forEach { prop ->
        prop.annotations.forEach { annot ->
            println("${prop.name} has $annot")
        }
    }
  ```
  - A saída é apenas: `balance has @pt.isel.annotations.Color(label="Blue")` pois corresponde às anotações da propriedade `balance`.
    - A anotação `Color(label="YELLOW")` corresponde ao parâmetro do construtor `balance`.
  - Mais exemplos com a classe de exemplo `Account` e a anotação `Color` está disponível em: [sample06-naivemapper-annotations/src/test/kotlin/pt/isel/annotations](../sample06-naivemapper-annotations/src/test/kotlin/pt/isel/annotations)
    - Com vários tipos de testes para compreender vários aspetos de anotações na API de Reflexão do Kotlin.