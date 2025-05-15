# Sequências, Geradores e Funções Suspensas (parte III)

## Funções Suspensas

- As funções `sequence` e `yield` envolvem o uso de funções suspensas.
- Considere a assinatura da função `sequence`:
  ```kotlin
  fun <T> sequence(block: suspend SequenceScope<T>.() -> Unit): Sequence<T>
  ```
- Explicação do parâmetro `block: suspend SequenceScope<T>.() -> Unit`
    - `block` representa o corpo de código fornecido pelo programador para gerar elementos da sequência.
        - É uma **função de suspensa** (_suspend function_), permitindo que outras funções suspensas ou operações assíncronas ocorram durante a geração da sequência.
        - Dentro deste bloco, pode-se chamar funções suspensas, incluindo `yield()`.
    - `SequenceScope<T>` é um recetor no _lambda_, ou seja, os métodos e propriedades de `SequenceScope<T>` podem ser acessados diretamente dentro do bloco.
        - Fornece um método **_yield_** para produzir elementos na sequência.
- A assinatura de `yield` é:
  ```kotlin
  abstract suspend fun yield(value: T)
  ```
    - É uma **função suspensa**.
    - Fornece um valor para o `Iterador` da sequência que está a ser construída e suspende até que o próximo valor seja solicitado.

### Definição de Função Suspensa

- Uma **função suspensa** é uma função que pode ser **pausada** (_suspended_) e **retomada** (_resumed_) depois.
    - Isto ocorre **sem bloquear** o _thread_ em que está a ser executada.
- Sempre que uma função suspensa chama outra função suspensa, é criado um **ponto de suspensão**.
    - Um ponto de suspensão corresponde a instruções no seu corpo que podem pausar a execução da função para ser retomada mais tarde.
- Na JVM, uma **função suspensa é traduzida pelo compilador Kotlin** para um método com um **parâmetro adicional** do tipo `Continuation<T>`.
- Este parâmetro tem dois propósitos:
    1. Receber o resultado da função suspensa que está a ser invocada conforme o **_Continuation-Passing Style_ (CPS)**.
    2. Gerar a implementação da suspensão, transformando efetivamente o ponto de suspensão numa continuação que pode posteriormente retomar o fluxo de execução.

## Continuation Passing Style (CPS)

- É um estilo de programação onde, em vez de retornar um resultado diretamente, uma função recebe uma "continuação" e a chama com esse resultado.
  - Uma "continuação" é uma função que representa o que fazer com o resultado.
- Isto serve para manipular controle de fluxo, implementar chamadas assíncronas, ou lidar com operações que podem falhar, entre outros casos.
- Normalmente, uma função em Kotlin (ou em muitas linguagens) retorna um valor:
  ```kotlin
  fun soma(a: Int, b: Int): Int {
      return a + b
  }
  ```
- Em CPS, essa função não retorna diretamente o resultado, mas recebe uma continuação (uma função) que será chamada com o resultado:
  ```kotlin
  fun somaCPS(a: Int, b: Int, cont: (Int) -> Unit) {
      val resultado = a + b
      cont(resultado)
  }
  ```
- A seguir, um exemplo de uso da função `somaCPS`:
```kotlin
fun main() {
    somaCPS(3, 4) { resultado ->
        println("O resultado da soma é $resultado")
    }
}
```
- Pode-se encadear várias operações em CPS:
```kotlin
fun main() {
    somaCPS(3, 4) { resultado1 ->
        println("Primeira soma: $resultado1")
        somaCPS(resultado1, 10) { resultado2 ->
            println("Segunda soma: $resultado2")
        }
    }
    // Primeira soma: 7
    // Segunda soma: 17
}
```

### Interface Continuation

- Pode-se verificar que toda função suspensa é transformada pelo compilador para incluir, na JVM, o parâmetro do tipo `Continuation<T>`.
  - Ver o bytecode da função `fetch` em [ContinuationFetchApp.kt](../sample26-generator-sequences/src/main/kotlin/ContinuationFetchApp.kt)
- Definição principal da interface [`Continuation`](https://github.com/JetBrains/kotlin/blob/whyoleg/dokka2-sync-stdlib/libraries/stdlib/src/kotlin/coroutines/Continuation.kt#L16):
    ```kotlin
    public interface Continuation<in T> {
        public val context: CoroutineContext
        public fun resumeWith(result: Result<T>)
    }
    ```
- Membros:
  - `val context: CoroutineContext`: é o contexto da co-rotina que corresponde a esta continuação.
    - O contexto inclui _jobs_ e _dispatcher_ da co-rotina (_threads_ de execução).
  - `fun resumeWith(result: Result<T>)`: retoma a execução da co-rotina correspondente passando um `Result<T>`.
    - O resultado pode ser o valor de tipo `T`, em caso de sucesso, ou falha (exceção).
  - Há também métodos auxiliares (funções de extensão):
    - `Continuation<T>.resume(value: T)`
      - Corresponde a: `resumeWith(Result.success(value))`
    - `Continuation<T>.resumeWithException(exception: Throwable)`
      - Corresponde a: `resumeWith(Result.failure(exception))`
- Exemplo da criação de uma instância `Continuation<String>` com um contexto de co-rotina vazio (síncrono):
  ```kotlin
  val cont = Continuation<String> (
          context = EmptyCoroutineContext,
          resumeWith = { result: Result<String>  ->
              /* the callback to the continuation code */
          }
      )
  ```
- Exemplo da implementação da função `fetchCps` usando a interface `Continuation`: 
  - [ContinuationFetchApp.kt](../sample26-generator-sequences/src/main/kotlin/ContinuationFetchApp.kt)
- Neste exemplo há 3 implementações principais:
  - `fetch()`: a função suspensa normal com uso implícito da interface `Continuation`;
  - `fetchCps()`: função com o uso explícito da interface `Continuation` passando uma continuação;
  - Transformação de `fetchCps` numa função de suspensão usando `suspendCoroutine`.

### Ilustração do CPS para Sequence e Yield

- Ver exemplo simples de implementação em: [VerySimpleYieldBuilderApp.kt](../sample26-generator-sequences/src/main/kotlin/VerySimpleYieldBuilderApp.kt)
  - Baseado no código do [SequenceBuilder do Kotlin](https://github.com/JetBrains/kotlin/blob/whyoleg/dokka2-sync-stdlib/libraries/stdlib/src/kotlin/collections/SequenceBuilder.kt).
  - Uso de `typealias SimpleContinuation = () -> Unit` ao invés da interface `Continuation`, mas com o mesmo propósito da abordagem CPS.