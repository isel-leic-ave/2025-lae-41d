# Garbage Collection e Ações de Limpeza (parte III)

## Tipos com Limpeza Especial: Finalização e _Cleaners_

- Soluções do tipo _try-with-resources_ não garantem 100% que o método `close` seja chamada adequadamente.
  - Programador pode esquecer de explicitamente chamar o `close`.
  - Programador pode não usar a estrutura `try-with-resources`.

### Mecanismo de Finalização

- Primeira alternativa implementada na JVM.
- Atualmente, apresenta algumas **desvantagens**.
  - Encontra-se **_deprecated_**.
  - Mas ainda pode ser utilizado por questões de compatibilidade.
  - A tendência é ser completamente removido no futuro.
  - Alternativa a seu uso: **Cleaner API** (veremos a seguir).
- A **finalização** permite que um objeto execute código após ser considerado inacessível, mas antes de a sua memória ser liberta da _heap_.
- Tempo de vida de um objeto 'finalizável':
  1) Objeto é criado;
  2) Objeto perde a referência;
  3) **GC adiciona objeto a uma fila de finalização** (apenas para objetos marcados como finalizado);
  4) Objeto é finalizado;
  5) **GC desaloca objeto da _heap_**.
- Esta [documentação](https://www.oracle.com/technical-resources/articles/javase/finalization.html) explica detalhes de como o mecanismo de finalização é implementado em Java.
- Um objeto que implementa a finalização requer **pelo menos dois ciclos de GC para libertar a sua memória**.
  - É necessário um ciclo para identificar o objeto como inalcançável;
  - E um segundo ciclo para desalocar a sua área de memória do objeto após a sua finalização ser executada.
  - Isto adiciona sobrecarga e, deve ser evitado ao máximo o seu uso.
- Um objeto é marcada para finalização se a sua classe sobrescreve o método `finalize` de `java.lang.Object`.
  - Exemplo de implementação do `finalize`: [TempImageFinalize.kt](../sample29-cleaners/src/main/kotlin/TempImageFinalize.kt)
  - O teste `testing finalize method` em [TestTempImage.kt](../sample29-cleaners/src/test/kotlin/TestTempImage.kt) cria um objeto `TestImageFinalize` sem chamar o close.
    - Para ver o efeito do GC, foi introduzida a chamada explícita a ele através de `System.gc()`.
    - Note que esta funcionalidade deve ser evitada em programas de produção, pois pode causar problemas de desempenho. 
- **Principais desvantagens**:
  - Acrescenta sobrecarga ao GC, usando ao menos dois ciclos de GC para libertar memória.
    - Além disso, retém todos os objetos alcançáveis pelo objeto que finaliza.
  - A JVM usa uma única thread dedicada para chamar os métodos `finalize`.
    - Se um `finalize` bloqueia, os outros `finalize` precisam esperar para serem executados.
    - Exceções lançadas através do método `finalize` não podem ser apanhadas pela aplicação.
      - Elas estão fora do fluxo normal de execução da aplicação.

### API Cleaner

- A API Cleaner oferece uma alternativa ao mecanismo de finalização.
- Ela permite registar uma ação de limpeza executada por um _thread_ específica.
  - Esta ação é executada pelo GC quando _phantom reachable object_ perde a referência.
  - _Phantom reachable object_: objeto que tem o(s) recurso(s) que precisa(m) ser limpo(s) em caso de perda de referência.
- Descrição básica da API Cleaner:
  - É necessário criar um objeto do tipo `Cleaner`.
    - Método `Cleaner create()`: cria um _thread_ para processar o _phantom reachable object_ e chama as suas ações de limpeza.
    - Método `Cleaner create(ThreadFactory threadFactory)`: faz o mesmo, mas com um `ThreadFactory` ao invés de criar um _thread_.
  - `Cleanable register(Object obj, Runnable action)`: regista um objeto e uma ação de limpeza que é do tipo `Runnable`.
  - `Cleanable` tem um método `clean()` que executa ações de limpeza e então remove o _cleanable_ do registo.
    - Pode-se chamar explicitamente o método `clean()` quando o objeto é fechado (_closable_) ou não é mais necessário.
    - Se o método `close()` não é chamado, a ação de limpeza é chamada implicitamente pelo `Cleaner` (concorrentemente no seu _thread_).
- Exemplo de uso da API Cleaner com o _TempImage_: [TempImageCleanable.kt](../sample29-cleaners/src/main/kotlin/TempImageCleanable.kt).
- O teste `testing Cleanable` em [TestTempImage.kt](../sample29-cleaners/src/test/kotlin/TestTempImage.kt) cria um objeto `TestImageCleanable` sem chamar o close.
