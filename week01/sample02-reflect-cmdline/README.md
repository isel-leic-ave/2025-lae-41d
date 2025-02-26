# Exemplos Simples da API Reflect de Kotlin

- Estes exemplos usam apenas a linha de comando para compilar e executar os códigos-fonte de demonstração.

## Classe de teste: Lamp

- Classe de teste, `Lamp`, que será usada para ser examidada nos exemplos.
- Compilação da classe de teste `Lamp.kt` com o compilador de Kotlin:

  ```bash
  kotlinc class-sample/Lamp.kt
  ```

## Métodos e Propriedades

- Demonstrações de intropecção da classe Lamp usando a API Reflect de Kotlin.
- Compilação de `SampleKclass.kt` com a API Reflect de Kotlin:

  ```bash
  kotlinc -cp . SampleKclass.kt
  ```

- Execução com a API Reflect e diretamente com a JVM:
  - Neste caso (Linux), as bibliotecas de Kotlin estão em `/usr/share/kotlin/kotlinc/lib`.
  - Para Windows, substituir `:` por `;`
  
```bash
java -cp '.:/usr/share/kotlin/kotlinc/lib/kotlin-stdlib.jar:/usr/share/kotlin/kotlinc/lib/kotlin-reflect.jar' SampleKclassKt
```

## Parâmetros de Funções

- Demonstrações de instropecção dos parâmetros das funções
- Compilação de `SampleKParameter.kt` com a API Reflect de Kotlin:

  ```bash
  kotlinc -cp . SampleKParameter.kt
  ```

- Execução com a API Reflect e diretamente com a JVM:
  - Neste caso (Linux), as bibliotecas de Kotlin estão em `/usr/share/kotlin/kotlinc/lib`.
  - Para Windows, substituir `:` por `;`

```bash
java -cp '.:/usr/share/kotlin/kotlinc/lib/kotlin-stdlib.jar:/usr/share/kotlin/kotlinc/lib/kotlin-reflect.jar' SampleKParameterKt
```