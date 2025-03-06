# Reflection (parte I)

- _Reflection_ (Reflexão) é a capacidade de um programa examinar ou modificar a sua estrutura e o seu comportamento em tempo de execução.
    - _O programa pode ver o seu reflexo_.
    - É uma característica presente em diversas linguagens que usam ambientes de execução geridos (MRE).
      - Java, .NET, JavaScript, Python, ...
- Exemplo simples em Java disponível em: [sample02-reflect-cmdline/DumpMethods.java](../sample02-reflect-cmdline/DumpMethods.java)
  - Compilar: `javac DumpMethods.java`
  - Exemplo de execução: `java DumpMethods java.lang.Math`
  - Mostra os métodos do pacote passado como parâmetro para o programa.
    - No caso do exemplo, os métodos de `java.lang.Math`
- Exemplo de bibliotecas úteis que usam Reflection:
  - _loggers_, como Log4J e SLF4J.
  - _frameworks_ de testes, como Junit e TestNG.
  - _parsers_ JSON, como Gson e Jackson.
- Veremos a API para Relfection de:
  - Kotlin - biblioteca `kotlin-reflect.jar`
  - Java - pacote `java.lang.reflect`

## Kotlin Reflection

- API Kotlin para _Reflection_:
  - [Documentação](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.reflect/)
- `KClass`: tipo da unidade principal da reflexão em Kotlin.
  - Uma instância KClass é a representação de um tipo:
    - Primitivos,
    - Tipos definidos por classes, interfaces ou outros construtores.
- Operador `::`
  - Permite criar uma referência para uma classe (ou objeto) ou para um Callable (_e.g._, função).
  - Exemplos para uma classe chamada [`Lamp`](../sample02-reflect-cmdline/class-sample/Lamp.kt):
    - Referência para classe: `val lampClass: KClass<Lamp> = Lamp::class`
    - Referência para um objeto `val lamp1 = Lamp()`: `val lamp1Ref = lamp1::class`
- Algumas propriedades de `KClass`:
  - `members: Collection<KCallable<*>>` - coleção de _callables_, como funções e propriedades, acessíveis na classe. Não inclui construtores.
  - `memberProperties` - igual a `members` mas retorna apenas _callables_ de propriedades.
  - `memberFunctions` - igual  `members` mas retorna apenas _callables_ de funções.
  - `declaredMembersdeclared` - igual a `members` mas retorna apenas _callables_ declaradas na classe (não inclui superclasses).
  - `declaredMemberProperties` - igual a `declaredMembers` mas retorna apenas _callables_ de propriedades.
  - `declaredMemberFunctions` - igual  `declaredMembers` mas retorna apenas _callables_ de funções.
- Algumas funções de `KClass`:
  - `Kclass<T>.createInstance(): T` - cria uma nova instância da classe chamando o construtor sem parâmetros ou com todos os parâmetros com valor por omissão.
  - `isSubclassOf(base): Boolean` - retorna verdadeiro se a classe é a mesma ou é uma subclasse de _base_.
  - `isSuperlassOf(derived): Boolean` - retorna verdadeiro se a classe é a mesma ou é uma superclasse de _derived_.
- `KCallable<R>`: representa uma entidade _callable_, como uma função ou uma propriedade.
  - Subtipos:
    - `KProperty`: propriedade.
    - `KFunction`: função.
  - Algumas propriedades:
    - `name: String` - nome do _callable_ conforme o código-fonte.
    - `parameters: List<KParameter>` - lista de referências aos parâmetros deste _callable_ (veremos `KParameters` depois).
    - `returnType: KType` - o tipo de retorno do _callable_. `KType` representa um tipo.
  - Função `call`: chama o membro _callable_ com uma lista de argumentos e retorna o resultado.
    - Se é desejado obter valor ou chamar função de um objeto, o primeiro parâmetro (_receiver_) deve ser o objeto.
    - No caso de o membro ser uma função (`KFunction`):
      - Quantidade e tipo de cada parâmetro devem condizer com os tipos declaradas na função.

![Principais classificadores da API de Reflexão de Kotlin](https://yuml.me/isel/kotlin-reflect.svg)

- Exemplo com compilação e execução por linha de comando: 
  - [week01/sample02-reflect-cmdline](../sample02-reflect-cmdline)
  - Este exemplo contém demonstrações de uso da API Reflect de Kotlin.
- Exemplo com projeto Gradle pela IDE Intellij: 
  - [week01/sample03-reflect](../sample03-reflect)
  - Este exemplo contém mais demonstrações de uso da API Reflect de Kotlin, considerando algumas exceções.
