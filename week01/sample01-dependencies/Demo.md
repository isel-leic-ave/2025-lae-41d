# Demonstrações

## Dependência de classes no Java

- Compilar classes X, Y e Z em Java:

```bash
javac Foo.java
```

- Compilar aplicação que depende das classes `X` e `Y` em tempo de compilação: 

```bash
javac App.java
```

- Correr a aplicação:

```bash
java App
```

- Remover classe `Z.class` não causa problema na compilação, pois não é dependência.

```bash
rm Z.class
javac App.java
```

- Remover a classe `Y.class` não causa problema na execução, pois o _class loader_ da JVM não irá usar esta classe em tempo de execução.

```bash
rm Y.class
java App
```

- Para o caso de usar, a JVM lança a exceção `NoClassDefFoundError`:

```bash
java App 0
```

- Alterar a mensagem impressa da classe `X` e recompilar `Foo.java`:

```bash
javac Foo.java
```

- Correr `App` terá o resultado da impressão da nova mensagem mesmo sem recompilar `App.java`: 

```bash
java App
```

## Interoperabilidade entre Java e Kotlin

- Compilar aplicação Kotlin que depende de classe `X`:

```bash
kotlinc -cp . App.kt
```

- Correr aplicação Kotlin com a JVM:

```bash
java -cp '.:/usr/share/kotlin/kotlinc/lib/kotlin-stdlib.jar' AppKt
```