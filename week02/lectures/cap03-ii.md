# Reflection (parte II)

## Kotlin Reflection: Exemplo Logger

- _**Logger**_: criar uma função de extensão chamada `Appendable.log(obj: Any)`.
  - Inclui a função `log` a todos os elementos do tipo `Appendable` (_e.g._, `StringBuilder`, `PrintStream`).
  - A função recebe um objeto e adiciona a um `Appendable` informações sobre o tipo do objeto, nome das propriedades e seus respetivos valores.
  - As informações são adicionadas através da função `appendLine()` do próprio `Apeendable`.
  - Exemplo para um objeto `Person("Maria", "Portugal")`:
    ```text
    Object of Type Person
     - country: Portugal
     - name: Maria
    ```

1) Definição da função de extensão:
  ```kotlin
  fun Appendable.log(obj: Any) {
      // To do
  }
  ```
2) Implementação da função:
  ```kotlin
  fun Appendable.log(obj: Any) {
    this.appendLine("Object of Type ${obj::class.simpleName}")
    obj::class.memberProperties.forEach { prop ->
      this.appendLine("  - ${prop.name}: ${prop.call(obj)}")
    }
  }
  ```

3) Por omissão, um objeto refletido não é acessível pela JVM (relativo à visibilidade).
   - Para torná-lo acessível para a JVM, pode-se alterar a acessibilidade da propriedade.
   - Propriedade `isAccessible: Boolean` do `KCallable` disponível em `kotlin.reflect.jvm`.
       - Isto altera a acessibilidade apenas para a JVM.

  ```kotlin
  fun Appendable.log(obj: Any) {
    this.appendLine("Object of Type ${obj::class.simpleName}")
    obj::class.memberProperties.forEach { prop ->
      prop.isAccessible = true
      this.appendLine("  - ${prop.name}: ${prop.call(obj)}")
    }
  }
  ```

- Exemplo está disponível em [sample04-logger](../sample04-logger)
    - Este exemplo é um subprojeto Gradle com alguns testes predefinidos.

### KFunction e KParameter

- `KFunction`: representa uma função.
  - Algumas propriedades:
    - `name`: nome da função.
    - `parameters: List<KParameter>`: lista de parâmetros.
    - `instanceParameter`: retorna um parâmetro que representa a instância `this` necessária para chamar este _callable_.
    - `returnType: KType`: corresponde a informações sobre os valores retornados.
      - `KType` possui propriedade `classifier` que indica o `KClassifier` do tipo. 
      - `KClassifier` é o tipo base de `KClass`.
- `KParameter`: representa um parâmetro passado a uma função.
    - Algumas propriedades:
        - `name`: nome do parâmetro que é declarado no código-fonte.
        - `kind`: categoria a qual o parâmetro pertence, como uma instância (INSTANCE) ou um valor (VALUE).
          - Há também a categoria _extension receiver_ (EXTENSION_RECEIVER)
        - `type`: tipo do parâmetro no sistema de tipos da linguagem.
- Exemplo que mostra tais propriedades: [week01/sample02-reflect-cmdline/SampleKParameter.kt](../../week01/sample02-reflect-cmdline/SampleKParameter.kt)
    - Ler o README.md da diretoria para compilar e executar.

### Implementação do Logger Getters

- Objetivo: criar a função de extensão `Appendable.logGetters(obj: Any)` para apresentar todos os métodos _getters_ de uma classe Java.
  - 
- Características de um método _getter_:
  - O nome começa com a string `get`;
  - Não tem parâmetros;
  - O retorno da função não é vazio (_Unit_);
    - _e.g._, `m.returnType.classifier != Unit::class` ou `m.returnType != typeOf<Unit>()`
  - Ter um único argumento correspondente ao paramêtro da instância (`instanceParameter`).
1) Definição da função de extensão:
    ```kotlin
    fun Appendable.logGetters(obj: Any) {
        this.appendLine("Object of Type ${obj::class.simpleName}")
        obj::class
            .members
            .filter { isGetter(it) }
            .forEach { func ->
                func.isAccessible = true
                appendLine("  - ${func.name.substring(3)}: ${func.call(obj)}")
            }
    }
    ```
2) Definição da função `isGetter` que verifica se um método é _getter_:

    ```kotlin
   fun isGetter(m: KCallable<*>): Boolean {
        return m.name.startsWith("get")
            && m.parameters.size == 1
            && m.instanceParameter == m.parameters[0]
            && m.returnType.classifier != Unit::class
    }
   ```
- Exemplo também está disponível em [sample04-logger](../sample04-logger)
    - Este exemplo é um subprojeto Gradle com alguns testes predefinidos.