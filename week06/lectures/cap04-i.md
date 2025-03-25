# Testes de Desempenho e Microbenchmarking

- _**Benchmarking**_ é um termo usado no contexto de medição de desempenho de uma aplicação ou sistema.
- Geralmente é utilizado um conjunto de testes que são executados múltiplas vezes num ambiente controlado.
  - Existem diversas unidades de medida como o tempo de execução, a vazão, o número de operações em ponto-flutuante, ...
  - As múltiplas execuções permitem obter dados estatísticos relevantes das unidades medidas, como a média, a variação e o intervalo de confiança.
- As medidas são usadas de forma relativa, visando comparar um ou mais artefactos de _software_.
- _**Microbenchmarking**_ corresponde a medidas de desempenho de pequenos e isolados pedaços de código ou funcionalidade.
- Veremos dois tipos de medida de desempenho:
1) uso das funções `System.currentTimeMillis()` e `System.nanoTime()` do Kotlin para medir o tempo de execução em milissegundos de um _teste alvo_;
2) JMH (_Java Microbenchmark Harness_): uma _framework_ para criar, executar e analisar _benchmarks_ nano/micro/mili/macro em linguagens JVM.

## Armadilhas Gerais na Medição de Desempenho

- Evite confiar em suposições.
  - Em vez disso, isole a parte específica para avaliar e meça o seu desempenho com a maior precisão possível.
- Evite focar na otimização antes de estar confiante na confiabilidade do seu componente de _software_. 
  - O objetivo principal é entregar um componente confiável, testado e totalmente funcional.
  - Somente após atingir essa base, o objetivo torna-se otimizá-lo.
- As medidas de tempo de execução para propósitos comparativos devem ser realizadas no mesmo ambiente controlado.
  - Mesmo computador;
  - Computador com o mínimo de sobrecarga possível.
- Considere também as seguintes armadilhas relativas a um _teste alvo_.

### Armadilhas num Teste Alvo

1. **_Microbenchmarking_ não é teste de unidade.**
   - Teste de unidade é usado para avaliar a correção da unidade (_e.g._, função).
   - Por exemplo, o JUnit adiciona sobrecarga através da reflexão.
   - _Microbenchmarking_ deve ser conduzido num ambiente isolado.
     - Sem ferramentas de desenvolvimento como um IDE. 
   - É recomendável executá-lo a partir de linha de comandos.
   - Deve-se também garantir que nenhum outro programa está a ser executado em simultâneo.
2. **Evite operações não relacionadas na medição.**
   - É importante separar o que quer medir.
   - Se deseja medir apenas a operação de uma função, por exemplo, deve-se excluir operações de pré e pós-processamento como:
     - instanciação de objetos como argumento;
     - operações com o valor retornado.
3. **A primeira execução inclui sobrecarga do compilador JIT e perde a otimização.**
   - Não se deve considerar apenas uma execução.
   - A primeira execução inclui a penalidade da tradução do compilador JIT. 
   - Deve-ser executar mais de uma vez, como 10 vezes.
   - Cálculos estatísticos sobre a medida de desempenho podem ser apresentados.
     - Média, desvio padrão, intervalo de confiança, são resultados que devem ser considerados.
   - Medida em milissegundos pode não ter precisão suficiente.
     - Para um teste pequeno, é importante utilizar uma resolução de medida mais baixa, como nanossegundos.
   - Várias ferramentas de _benchmarking_ incluem uma secção de aquecimento (_warm up_). 
4. **Evite E/S.**
   - Entrada e saída (E/S ou I/O) devem ser excluídos dos testes de desempenho.
     - Isole apenas pedaços de código que não tenham esse tipo de operação.
     - Exemplos de E/S:
       - _print_ em geral,
       - _input_ do utilizador,
       - leitura ou escrita de ficheiros,
       - acesso (leitura ou escrita) à base de dados.
   - Operações de _print_ podem ser substituídas pelo uso de um _StringBuilder_, por exemplo.
     - Ou simplesmente apenas associar o resultado da operação a uma constante ou variável.
   - Operações de _input_ podem ser substituídas pela afetação direta de uma constante ou constante.
   - No caso de acesso a ficheiros ou base de dados, pode-se substituir o acesso por um `mock` em memória.
   - Um potencial problema que pode acontecer com essas abordagens é a eliminação de _dead-code_.
     - Processos de otimização de código da JVM podem eliminar código quando eles não são usados.
       - Por exemplo, quando se usa o _StringBuilder_ e nada é feito com ele depois.
     - Ferramentas de _benchmarking_, como a JMH (veremos a seguir), incluem uma abordagem de **_Black Hole_**.
       - É usado um mecanismo para **consumir** os resultados e prevenir a eliminação de _dead-code_.
5. **Minimize a sobrecarga da chamada do sistema.**
   - Usar o `System.nanoTime()` para medir o tempo envolve uma chamada de sistema que tem o seu próprio custo.
   - No caso de um teste pequeno, esse custo pode ser relevante e afetar as medições do teste alvo.
   - Nestes casos, o melhor é repetir o teste alvo por várias iterações e obter uma média aritmética como tempo total de uma execução.
     - O tempo total de execução é dividido pelo número de iterações realizadas.
     - Tipicamente, esse número de iterações é grande, como 1 milhão de iterações.
       - Lembre-se que 1 nanossegundo corresponde a $10^{-9}$ segundos ou a $10^{-6}$ milissegundos.

## JMH

- _Java Microbenchmark Harness_: uma _framework_ para criar, executar e analisar _benchmarks_ nano/micro/mili/macro em linguagens JVM.
  - Tutorial da ferramenta: https://jenkov.com/tutorials/java-performance/jmh.html
- Criação de projeto com Gradle:
  - Criar diretoria `jmh` em `src` do projeto original com o Gradle.
  - Criar o programa para o `microbenchmarking`.
  - Exemplo em Kotlin para testar a função `nextPrime()` em [week06/sample17-simple-bench-and-jmh/](../sample17-simple-bench-and-jmh):
  ```kotlin
  package pt.isel

  import ...

  @BenchmarkMode(Mode.AverageTime) // Measure average execution time per operation
  @OutputTimeUnit(TimeUnit.NANOSECONDS)
  @State(Scope.Benchmark)
  open class NextPrimeJmh {
  private val n = 10000L

    @Benchmark
    fun benchNextPrimeWithBH(blackhole: Blackhole) {
        blackhole.consume(nextPrime(n))
    }
    @Benchmark
    fun benchNextPrime() {
        val p = nextPrime(n)
    }
  }
  ```

- Modos de operação:

| Modo           | Designação              | Descrição                                                                                                  |
|----------------|-------------------------|------------------------------------------------------------------------------------------------------------|
|_Throughput_    | Vazão                   | Mede o número de operações por segundo.                                                                    |
|_AverageTime_   | Tempo médio             | Mede o tempo médio que o método de benchmark alvo demora a ser executado.                                  |
|_SampleTime_    | Tempo de amostragem     | Mede o tempo que o método de benchmark alvo demora a ser executado, incluindo o tempo máximo, mínimo, etc. |
|_SingleShotTime_| Tempo de execução único | Mede o tempo que demora a executar um único método de benchmark (incluindo seu tempo de arranque).         |
|_All_           | Todos                   | Mede todos os anteriores.                                                                                  |

- Unidades de tempo:
  - NANOSECONDS
  - MICROSECONDS
  - MILLISECONDS
  - SECONDS
  - MINUTES
  - HOURS
  - DAYS

- Estado do _benchmark_:
  - Podem ser especificadas variáveis (que representado o estado) que serão passadas ao _benchmark_.
  - Escopos de estado:
    - _Thread_: cada _thread_ que executa o _benchmark_ irá criar a sua própria instância do objeto de estado.
    - _Group_: cada grupo de _threads_ que executa o _benchmark_ criará a sua própria instância do objeto de estado.
    - _Benchmark_: todos os _threads_ que executam o _benchmark_ partilham o mesmo objeto de estado.

- Configuração das dependências:
  - incluir os seguintes plugins (versão 0.7.3 do JMH):
    ```kts
    plugins {
      id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
      id("me.champeau.jmh") version "0.7.3"
    }
    ```

- Compilação com o Gradle a partir da raiz do projeto:
  ```bash
  ./gradlew jmhJar
  ```
  
- Execução em linha de comando a partir de um projeto Gradle:
  ```bash
  java -jar build/libs/<projectName>-jmh.jar -i <nIterations> -wi <warmupIterations> -f <fork> -r <timeOnIteration> -w <timeOnWarmupIteration>
  ```
  - Alguns parâmetros:
    - `-f <fork>`: fork corresponde a quantidade de grupos de execução;
    - `-i <nIterations>`: nIterations é a quantidade de iterações de cada medição;
    - `-wi <warmupIterations>`: warmupIterations é a quantidade de iterações de cada medição no _warm up_;
    - `-r <timeOnIteration>`: tempo (em segundos) que o método de benchmark fica na iteração (faz média aritmética);
    - `-w <timeOnWarmupIteration>`: tempo (em segundos) que o método de benchmark fica na iteração do _warm up_;
  - Exemplo de execução com o `NextPrimeJmh`: 
    - Comando: `java -jar build/libs/sample17-simple-bench-and-jmh-jmh.jar -f 1 -i 4 -r 2 -wi 4 -w 2`
    - 1 fork (argumento `-f 1`),
    - 4 iterações de medida (argumento `-i 4`) realizada durante 2 segundos (argumento `-r 2`),
    - 4 iterações de _warmup_ (argumento `-wi 4`) realizado durante 2 segundos (argumento `-w 2`).