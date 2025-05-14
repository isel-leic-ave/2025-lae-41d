# Sequências, Geradores e Funções Suspensas (parte II)

## Construção de Sequências

- A partir dos elementos: uso da função `sequenceOf()`.
  - Exemplo: `val evenSequence = sequenceOf(2, 4, 6, 8)`
- A partir de um `Iterable` (_e.g._, `List`): uso do método `asSequence()` de um `Iterable`.
  - Exemplo: `val evenSequence = listOf(2, 4, 6, 8).asSequence()`
- A partir de uma dada função: [`generateSequence()`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/generate-sequence.html)
  - Função que recebe uma função chamada para produzir os elementos da sequência sob demanda (_lazily_).
  - Esta função é chamada sempre que um novo elemento é pedido (implicitamente pelo `next()`) até que ela retorne `null`.
  - Exemplo simples: `val evenSequence = generateSequence(2){ if (it < 8) it + 2 else null}`
    - gera sequência `2, 4, 6, 8`.
- Ver exemplos com `generateSequence` em [TestWeatherWebApi.kt](../../week11/sample25-sequences/src/test/kotlin/pt/isel/TestWeatherWebApi.kt).
    - Teste `check rainy days in Lisbon`;
    - Teste `test load files with weather for Sidney since 2023`;
    - Teste `check interleave temperatures in celsius with position`.
- Ver exemplo do _interleave_ com `generateSequence` em [Extensions.kt](../../week11/sample25-sequences/src/main/kotlin/pt/isel/weather/Extensions.kt)
  - Função `interleaveGen`.

## Geradores e _Yield_

- Gerador [`sequence`](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.sequences/sequence.html).
  - **`yield`**: função que insere um único elemento na sequência.
- Um gerador refere-se a uma computação que:
  1. fornece (_yields_) valores ao chamador apenas quando requisitado.
  2. é retomado (finaliza o processo) após o valor produzido ter sido consumido pelo chamador.
- Considere o seguinte exemplo:
  ```kotlin
  fun main() {
    val seq = sequence {
      println(" -> Generating first")
      yield(1)
      println(" -> Generating second")
      yield(2)
      println(" -> Generating third")
      yield(3)
      println(" -> Done")
    }
    seq.forEach { num ->
      println("The next number is $num")
      println("Sleeping 1 second...")
      Thread.sleep(1000)
    }
  }
  ```
  - Neste exemplo, cada mensagem de "_Generating_" só é impressa quando o número da sequência é requisitado.
    - A cada 1 segundo.
    - Isto é, a sequência é gerada sob demanda.
    - O primeiro `yield()` está associado ao primeiro `next()`, o segundo `yield()` ao segundo `next()`, e assim sucessivamente.
  - Este exemplo está disponível em: [AppSuspendDemo.kt](../sample26-generator-sequences/src/main/kotlin/AppSuspendDemo.kt)
- Ver exemplo do _interleave_ com `sequence` em [Extensions.kt](../../week11/sample25-sequences/src/main/kotlin/pt/isel/weather/Extensions.kt)
  - Função `interleave`.

## Exercícios Selecionados

1. Exercício 1 do livro.
2. Exercício 2 do livro.
3. Implemente `lazyMap` e `lazyFilter` (TPC) que sejam equivalentes à versão _lazy_ de `map` e `filter` para `Sequence`. 
   - Considere duas implementações diferentes para cada:
      1. Use a implementação explícita da interface `Iterator`.
      2. Use a implementação através do gerador `sequence`.
   - Para o `lazyFilter`, usar os estados auxiliares `FilterIteratorState { NotReady, Ready, Finish}`.
   - [Solução lazyMap explícita](../sample26-generator-sequences/src/main/kotlin/LazyMapExpl.kt).
   - [Solução lazyMap com gerador sequence](../sample26-generator-sequences/src/main/kotlin/LazyMapSeq.kt).
4. Implemente `fun <T> Sequence<T>.concat(other: Sequence<T>) = Sequence<T>`.
5. Implemente `fun <T : Any?> Sequence<T>.collapse() = Sequence<T>`.
6. Exercício 3 do livro (TPC).
7. Exercício 4 do livro (TPC).