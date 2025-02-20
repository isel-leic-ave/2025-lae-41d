- Compilação de `SampleKclass.kt` com a API Reflect:

  ```bash
  kotlinc -cp . SampleKclass.kt
  ```

- Execução com a API Reflect e diretamente com a JVM:
  - Neste caso (Linux), as bibliotecas de Kotlin estão em `/usr/share/kotlin/kotlinc/lib`.
  - Para Windows, substituir `:` por `;`
  
```bash
java -cp '.:/usr/share/kotlin/kotlinc/lib/kotlin-stdlib.jar:/usr/share/kotlin/kotlinc/lib/kotlin-reflect.jar' SampleKclassKt
```