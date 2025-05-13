# Sequências, Geradores e Funções Suspensas (parte I)

## Introdução a Sequências

- _Collection Pipeline_: padrão de programação em que se organiza alguma computação como uma sequência de operações, onde cada operação produz uma coleção que alimenta a seguinte.
  - Em Kotlin, há as coleções (interface `Collection` que herda de um `Iterable`)
  - Em Kotlin, há também as sequências (interface `Sequence`)
  - Em Java, _streams_ (interface `Stream`)
- **Sequências**: são um tipo de Collection Pipeline com algumas características diferentes das coleções tradicionais.
  - Elas **produzem** os seus elementos durante a iteração. 
- A maioria das bibliotecas de linguagens de programação que provêem **sequências** concordam em ter:
  - **Geradores**: funcionalidades para gerar os elementos.
  - Primitiva **_yield_**: para inserir um elemento.
- Em Kotlin, Sequences são implementadas via funções suspensas.
  - Uso de co-rotinas: _suspend functions_
- Documentação em Kotlin: [_Sequences_](https://kotlinlang.org/docs/sequences.html)

## Características de Sequências

- A seguir, serão apresentadas as principais características de **sequências**.
- Serão também apresentados exemplos de códigos utilizando a _API World Weather Online_ disponível em http://api.worldweatheronline.com
  - Esta API provê um histórico do clima de diversas cidades pelo mundo, com informações como:
    - temperatura,
    - descrição do clima,
    - humidade, 
    - precipitação, 
    - velocidade do vento,
    - cobertura de nuvens.
  - Os dados podem ser obtidos diretamente da API _online_ ou de uma _cache_ local (armazenada em ficheiro na diretoria _resources_).
  - A classe de dados [Weather.kt](../sample25-sequences/src/main/kotlin/pt/isel/weather/Weather.kt) contém os principais dados selecionados da API.
    - Além de duas propriedades sem _backing field_: _isSunny_ e _isRainy_, implementadas como funções _getter_.
    - Contém também uma função de extensão de uma `String` que faz o _parser_ de uma linha dum CSV e gera um objeto `Weather`.
  - A classe [WeatherWebApi.kt](../sample25-sequences/src/main/kotlin/pt/isel/weather/WeatherWebApi.kt) contém métodos para:
    - acessar a API Web;
    - gravar dados da API Web em ficheiro (_cache_ local);
    - acessar a API Web e fazer _parser_ dos dados para uma lista de Weather (`List<Weather>`);
    - acessar dados da cache local (ficheiro) e fazer _parser_ dos dados para uma lista de Weather (`List<Weather>`).

### Legibilidade

- Os **pipelines de sequências** permitem que os programadores encadeiem transformações nos dados.
  - A saída de cada cálculo se torna a entrada para a etapa seguinte (pipeline).
  - Isto permite uma boa **legibilidade** com código.
- Exemplo de um pipeline de sequência em Kotlin que obtém 5 temperaturas de dias ensolarados.
    ```kotlin
    val top5temps = pastWeather
        .filter(Weather::isSunny)
        .map(Weather::celsius)
        .take(5)
    ```
- Comparação de legibilidade com a abordagem imperativa:
  ```kotlin
  val top5temps = mutableListOf<Int>()
  for (w in pastWeather) {
      if (w.isSunny) {               // <=> filter
          top5temps.add(w.celsius)   // <=> map
          if (top5temps.size >= 5) { // <=> take
              break
          }
      }
  }
  ```
  
### Nomes de Operações

- Os nomes das operações das sequências tendem a ter diferentes terminologias entre as diferentes linguagens de programação que implementam _Sequences_.
- Exemplo em C#:
    ```cs
    var top5temps = pastWeather
        .Where(weather => weather.IsSunny)
        .Select(weather => weather.Celsius)
        .Take(5)
    ```
  - Onde:
    - `Where` faz o filtro (filter);
    - `Select` faz o mapeamento (map).

### _Composability_ (Componibilidade): encadeamento de métodos _vs._ funções aninhadas

- Os exemplos de sequências apresentados até então são encadeamento de métodos (_method chaining_).
  - O objeto recetor é implicitamente passado como um argumento a cada método _call_ seguinte.
  - Cada método retorna um objeto que será o objeto recetor do próximo _call_, permitindo o encadeamento.
- Existe também a abordagem das _funções aninhadas_ (_nested functions_).
- Linguagens como Scheme e Closure utilizam essa abordagem.
- Exemplo de um pipeline de sequências em Closure:
  ```closure
  (take 5
      (map :celsius
          (filter :isSunny pastWeather)))
  ``` 

### Avaliação _eager_ _vs._ _lazy_

#### Abordagem _eager_
- Cada função produz o seu resultado _in-memory_ encadeado como a entrada para a chamada seguinte.
  - Collections em Kotlin usa abordagem _eager_.
  - Interface _Iterable<T>_.
  - É uma abordagem dita **horizontal**.
- Exemplos da implementação das funções de extensão dum Iterable `filter`, `map` e `take` em Kotlin (_standard_):
  ```kotlin
  public inline fun <T> Iterable<T>.filter(predicate: (T) -> Boolean): List<T> {
    val destination = ArrayList<T>()
    for (element in this)
      if (predicate(element))
          destination.add(element)
    return destination
  }
  public inline fun <T, R> Iterable<T>.map(transform: (T) -> R): List<R> {
    val destination = ArrayList<R>(collectionSizeOrDefault(10))
    for (item in this)
      destination.add(transform(item))
    return destination
  }
  public inline fun <T> Iterable<T>.take(n: Int): List<T> {
    val destination = ArrayList<T>(n)
    var count = 0
    for (item in this) {
      destination.add(item)
      if (++count == n)
          break
    }
    return destination.optimizeReadOnlyList()
  }
  ```
#### Abordagem _lazy_

- Os valores são avaliados o mais tarde possível e as sequências de valores podem ser infinitas.
  - Sequences em Kotlin usa abordagem _lazy_.
  - Interface `Sequence<T>`.
  - É uma abordagem dita **vertical**.
- Pode-se converter qualquer coleção para uma sequência usando a função `asSequence()`.
- Implementação _standard_ em Kotlin da função `map`:
```kotlin
  public fun <T, R> Sequence<T>.map(transform: (T) -> R): Sequence<R> {
      return TransformingSequence(this, transform)
  }
  internal class TransformingSequence<T, R>
  constructor (private val sequence: Sequence<T>, private val transformer: (T) -> R) : Sequence<R>
  {
    override fun iterator(): Iterator<R> = object : Iterator<R> {
      val iterator = sequence.iterator()
      override fun next(): R {
        return transformer(iterator.next())
      }
    
      override fun hasNext(): Boolean {
        return iterator.hasNext()
      }
    }
  }
```
- Dois tipos de operações:
  - **intermédias**: retornam uma nova sequência produzida de forma tardia (_lazily_).
    - e.g., `filter`, `map`, `take` e `drop`.
  - **terminais**: não retornam uma sequência, finalizando assim o encadeamento de sequências.
    - e.g., `forEach`, `toList`, `count` e `sum`.
- Em outras palavras, a abordagem _lazy_ produz os valores da sequência verticalmente apenas quando chega a um terminal.
- **Vantagens**:
  - Tende a melhorar desempenho em casos de processamento de subconjunto de dados (e.g., como uso de `take`).
  - Tende a usar menos memória: não produz todos os dados intermédios.
    - Também há melhora de desempenho porque produz menos objetos para o Garbage Collector.
- Ver exemplo em [WeatherWebApi.kt](../sample25-sequences/src/main/kotlin/pt/isel/weather/WeatherWebApi.kt):
  - Teste 1: `select top 5 temperatures in Sidney with pipeline`
  - Teste 2: `select top 5 temperatures in Sidney with pipeline lazy`
  - Comparar Teste 1 e Teste 2 no modo debug com breakpoint em cada função (usar _Resume Program_ para avançar)
  - Pode-se também comparar os valores da variável `count`, que conta quantas vezes cada função foi chamada.
- Antes de Kotlin, já havia suporte para a abordagem _lazy_ através do pacote `java.util.stream` através de sua interface principal `Stream`.
  - Uma `Collection` pode ser convertida para `Stream` através do método `stream()`.
- Mesmo exemplo com `Stream` em Java:
  - Teste 3: `select top 5 temperatures in Sidney with pipeline lazy in Java`

### Extensibilidade

- Cada tecnologia de sequência possui uma interface que especifica como elementos são percorridos e acedidos.
  - Precisa provê ao menos um método para **avançar** ao próximo elemento e uma propriedade/método para **acessar** o elemento corrente.
- A extensibilidade de `Sequence<T>`, em Kotlin, é implementada sobre um `Iterator<T>`.
  - É uma interface genérica, com o parâmetro de tipo `T` que representa o tipo de elementos da sequência.
  - Esta interface tem um único método, `iterator()`, que devolve um `Iterator`.
- Iterator é uma interface que tem dois métodos: 
  - `hasNext()`: retorna true se existirem mais elementos na sequência a iterar e false caso contrário.
  - `next()`: devolve o próximo elemento da sequência.
- Exemplo base: gerar uma sequência que **intercala** (_interleave_) duas sequências.
  - Exemplo: sequência 1: (13, 18, 20, 27, 25) e sequência 2: (18, 15, 21, 19, 23) produz a sequência intercalada (13, 18, 18, 15, 20, 21, 27, 19, 25, 13).
- O esqueleto de implementação da classe é:
  ```kotlin
  fun <T> Sequence<T>.interleave(other: Sequence<T>) : Sequence<T> {
    return InterleavingSequence(this, other)
  }
  
  class InterleavingSequence<T>(self: Sequence<T>, other: Sequence<T>) : Sequence<T> {
      // ...
      override fun iterator(): Iterator<T> {
          return object : Iterator<T> {
              override fun hasNext(): Boolean {
                  // ...
              }
              override fun next(): T {
                  // ...
              }
          }
      }
  }
  ```
- Ver implementação em: [Extensions.kt](../sample25-sequences/src/main/kotlin/pt/isel/weather/Extensions.kt), função `interleaveExt`.
  - Com definição explícita da classe.
  - Teste: `check interleave explicit temperatures in celsius`.


### Abordagem de acesso: _pull_ _vs._ _push_

- Até o momento, vimos a abordagem baseada em _pull_.
  - Os elementos são puxados/retirados da sequência.
  - Sequence/Iterator usam esta abordagem.
    - Pode ser também usada com Stream em Java.
- Em `Stream`, de Java, também é possível usar a abordagem _push_.
  - Os elementos são "empurrados" para serem consumidos por um _consumer_.
- Em `Stream`, o método `tryAdvance(Consumer action)` pode ser implementado com a abordagem _push_.
  - `tryAdvance` é um método de um `Spliterator`, um tipo especial de _Iterator_.
    - Move para o próximo elemento que está disponível para o _spliterator_ e tenta executar uma ação (_Consumer_) sobre ela. 
      - Retorna verdadeiro (_true_).
    - Se não há próximo elemento, retorna falso.
    - A ação (_Consumer_) pode ser definida por uma expressão _lambda_.
  - Para percorrer os elementos de um spliterator no modo `tryAdvance`, pode-se utilizar a estrutura `while` de forma imperativa.
    ```kotlin
    val iter = pastWeather.spliterator()
    while (iter.tryAdvance { w -> println(w) }) {
        /* while block, in this case, is empty */
    }
    ```
  - Para transformar um `Spliterator` em `Stream`, pode-se utilizar a seguinte conversão:
    - `StreamSupport.stream(aSpliteratorObject, false)`
    - O primeiro argumento é um objeto `Spliterator` com o método `tryAdvance` implementado.
    - O segundo argumento indica se o `Stream` é paralelo ou não (nota: não iremos estudar o caso paralelo).

- Ver exemplos:
  - Teste `select top 5 temperatures in Sidney with imperative in tryAdvance`.
  - Implementação do `interleave` como função de extensão do Java Stream com `tryAdvance`: [Extensions.kt](../sample25-sequences/src/main/kotlin/pt/isel/weather/Extensions.kt)

## Geradores e _Yield_

- Gerador: 
  - [`sequence`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/sequence.html).
    - **`yield`**: função que insere um único elemento na sequência.
