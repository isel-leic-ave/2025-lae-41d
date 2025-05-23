# Garbage Collection e Ações de Limpeza (parte II)

## Tipos com Limpeza Especial

- Há determinadas classes que lidam com **recursos especiais não geridos pelo GC**.
- Exemplos são classes que implementam:
  - acesso a sistema de ficheiros,
  - conexões a base de dados,
  - comunicação em rede via _sockets_.
- Desempenham **tarefas especiais de limpeza** para libertar recursos nativos ao fim do seu ciclo de vida.
- Há dois diferentes métodos para a limpeza:
  1) Método `close`/`dispose`: chamado programaticamente pelo desenvolvedor.
  2) Método _finalizer_ ou _cleaner_: chamado implicitamente no caso de o programador esquecer de chamar o close/dispose explicitamente. 
     - É um método salvaguarda.
     - Garante a tarefa de limpeza.

### Closeable

- Recursos nativos são limitados.
- É preciso prevenir que eles sejam tomados desnecessariamente.
- É boa prática libertar estes recursos assim que eles não forem mais necessários.
- Exemplos de métodos especiais para este propósito:
  - `close`, especificado pela interface `Closeble` na JVM.
  - `dispose`, especificado pela interface `Disposable` em .Net.
- Estes métodos devem ser chamados explicitamente pelo programador.
  - Inclusive em situação de exceção, no bloco `finally`.

```kotlin
var writer: FileWriter? = null
try {
    writer = FileWriter("temp.txt")
    writer.write("...")
    // do other stuff
} finally {
    writer?.close()
}
```

#### Try-with-resources

- Utilidade que facilita e previne o erro de esquecer de chamar o `close`.
- Exemplo em Java:
    ```java
    try(Writer writer = new FileWriter("temp.txt")) {
        writer.write("...");
        // do other stuff
    }
    ```
- Exemplo em Kotlin (função de extensão `use`):
    ```kotlin
    FileWriter("temp.txt").use { writer ->
        writer.write("...")
        // do other stuff
    }
    ```
- Ver [documentação do _try-with-resources_ do Java](https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html).
- Ver [documentação do `use` do Kotlin](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/use.html).
- Tais exemplos são traduzidos pelo compilador para o bloco _try-finally_ ou _try-catch-finally_.

### Implementação da Interface `Closeable`

- Pode-se também implementar o seu próprio tipo _closeable_ herdando da interface `Closeable`.
- Exemplo do tipo `TempImage`, que guarda temporariamente uma imagem para ser tratada.
  - Quando termina o seu uso, a imagem é removida.
    - Esta etapa é implementada na sobrescrita do método `close`.
  - Se uma mesma imagem é instanciada através de `TempImage`, não deve ser feito o _download_ novamente.
    - Verificado pelo estado da propriedade `downloaded`.
- Exemplo da classe `TempImage` está disponível em: [TempImage.kt](../sample28-closeable/src/main/kotlin/TempImage.kt)
  - Testes de uso deste tipo estão disponíveis em: [TestTempImage.kt](../sample28-closeable/src/test/kotlin/TestTempImage.kt)