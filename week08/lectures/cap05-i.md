# Bytecode

- Representação intermédia: entre código-fonte e código nativo (linguagem de máquina, _e.g._ x86).
- Diferenças relativamente à linguagem de máquina:
  - Não usa registos;
  - _Type safety_: algumas operações devem ser realizadas sobre valores de tipos compatíveis.
  - Tem funcionalidades orientadas a objeto.
- Bytecode é formado por OpCode + argumentos (ou operandos).
  - Opcode é representado por um byte: por isso o nome "bytecode".
  - Argumentos podem ser:
    - Estáticos: fixos, diretamente no _.class_. 
    - Dinâmicos: argumento é obtido do _Evaluation Stack_ (será estudado nesta aula).
- Documentação: [Especificação da JVM](https://docs.oracle.com/javase/specs/jvms/se22/jvms22.pdf) (_Java SE 22 Edition_).

## Arquitetura da JVM

### 1. Subsistema _Class Loader_
- Carregamento:
    - O Class Loader carrega ficheiros _.class_ (que contêm bytecode) para a memória.
    - Existem três tipos de carregadores de classe: _Bootstrap_, _Extension_ e _Application_.
- Vinculação (_linking_):
    - **Verificação**: garante que o bytecode é compatível e seguro.
    - Preparação: aloca memória para variáveis estáticas e as inicializa com valores padrão.
    - Resolução: substitui referências simbólicas por referências diretas na memória.
- Inicialização:
  - Atribui valores iniciais às variáveis estáticas e executa blocos de inicialização estáticos.


### 2. Áreas de Dados em Tempo de Execução

- A JVM define várias áreas de dados utilizadas durante a execução de um programa. 
- Algumas destas áreas de dados são criadas no arranque da JVM e são destruídas apenas quando ela é terminada. 
- Outras áreas de dados são por thread (_per-thread_). 
  - As áreas de dados por thread são criadas quando um thread é criado e destruídas quando o thread é terminado.
1. Registos PC (_per-thread_):
   - Cada thread tem a seu próprio registo PC (_program counter_), que rastreia a instrução atual.
2. Pilhas JVM - JVM _Stacks_ (_per-thread_):
   - Cada _thread_ tem a sua própria pilha, que armazena _frames_ de pilha.
   - Um _frame_ de pilha é a estrutura de dados que compõe cada elemento da pilha JVM.
   - Pilha é LIFO (_Last-in First-out_) com operações básicas de _push_ (adicionar) e _pop_ (remover).
3. _Heap_:
   - Armazena objetos e as suas variáveis de instância.
     - Quando a palavra reservada `new` é usada no código, por exemplo.
     - O objeto é armazenado na heap e uma referência a ele é armazenada na pilha JVM.
   - É partilhada por todos os _threads_.
   - É gerida pelo _Garbage Collector_.
4. Área de Método:
    - Armazena metadados de classe, variáveis estáticas e código de método.
    - Partilhada por todos os _threads_. 
    - Aloca o _Runtime Constant Pool_, o conjunto de constantes resolvidas em tempo de execução. 
5. Pilhas de Método Nativo (_per-thread_):
   - Semelhante às pilhas da JVM, mas para métodos nativos (escritos em outras linguagens, como C e C++).
     - Também chamado de _C Stack_.
   - Cada _thread_ tem a sua pilha de método nativo.

### 3. Execution Engine

- Formado por 3 componentes:
  - **Interpretador**:
    - Lê e executa bytecode linha por linha. 
    - Mais lento, mas rápido para código executado poucas vezes. 
  - **Compilador JIT**:
    - Compila bytecode frequentemente executado em código nativo. 
    - Melhora o desempenho para código executado repetidamente.
  - **_Garbage Collector_**:
    - Libera automaticamente a memória ocupada por objetos não utilizados. 
    - Ajuda a prevenir vazamentos de memória.

### 4. Interface de Método Nativo - JNI (_Java Native Interface_)

- Permite que o código Java chame e seja chamado por bibliotecas e aplicações nativas. 
- Usado para acessar recursos específicos do sistema operativo.

### Resumo do Fluxo de Execução

1. O Class Loader carrega o bytecode (_.class_) para a memória. 
2. O bytecode é verificado, preparado e resolvido. 
3. A JVM executa o bytecode usando o interpretador ou o compilador JIT. 
4. O _Garbage Collector_ gerencia a memória do _Heap_. 
6. O JNI permite a interação com código nativo, se necessário.

## _Frames_ da Pilha da JVM

- Um _frame_ é alocado a partir da pilha JVM do _thread_ que o cria.
- Um _frame_ é utilizado para:
  - armazenar dados e resultados parciais, 
  - executar as ligações dinâmicas (_dynamic linking_), 
  - retornar valores para os métodos,
  - despachar exceções.
- **Um novo _frame_ é criado cada vez que um método é invocado**. 
- Um frame é destruído quando a invocação do método é concluída.
  - Quer seja de forma normal ou abrupta (gera uma exceção não detetada).
- Cada _frame_ contém:
  - o seu próprio conjunto de **variáveis locais**, 
  - a sua própria _**Evaluation Stack**_ (ou _Operand Stack_),
  - uma **referência ao _Runtime Constant Pool_** da classe do método atual.

## Resumo Geral das Áreas de Dados em Tempo de Execução

![Imagem SVG que mostra as áreas de dados em tempo de execução da JVM.](./jvm-memory-areas.svg)

## Análise do Bytecode

- Com o comando `javap -p -v pt.isel.SimpleClassKt`, pode-se ver as informações adicionais do bytecode.
  - O argumento `-v` indica _verbose_, para mostrar informações adicionais;
  - O argumento `-p` indica _private_, para mostrar informações de todos os membros, incluindo os privados.
- Informações que podem ser vistas:
  - _Runtime Constant Pool_ de uma classe;
  - Códigos (em bytecode) dos métodos de uma classe;
  - Campos da classe;
  - Variáveis locais de um método;
  - Tamanho máximo do _Evaluation Stack_.
- Considere o seguinte código em Kotlin:
    ```kotlin
    package pt.isel
    
    class SimpleClassKt {
        fun sayHello() {
            println("Hello")
        }
    }
    ```

### Constant Pool

- Contém constantes necessárias para correr o código.
  - Ajudam a reduzir a redundância no código.
  - _e.g._, nomes qualificados de classes, métodos e campos usados na classe, strings dos códigos dos métodos.
- Saída do `javap -v pt.isel.SimpleClass` (parte do _Constant Pool_):
```text
Constant pool:
   #1 = Utf8               pt/isel/SimpleClass
   #2 = Class              #1             // pt/isel/SimpleClass
   #3 = Utf8               java/lang/Object
   #4 = Class              #3             // java/lang/Object
   #5 = Utf8               <init>
   #6 = Utf8               ()V
   #7 = NameAndType        #5:#6          // "<init>":()V
   #8 = Methodref          #4.#7          // java/lang/Object."<init>":()V
   #9 = Utf8               this
  #10 = Utf8               Lpt/isel/SimpleClass;
  #11 = Utf8               sayHello
  #12 = Utf8               Hello
  #13 = String             #12            // Hello
  #14 = Utf8               java/lang/System
  #15 = Class              #14            // java/lang/System
  #16 = Utf8               out
  #17 = Utf8               Ljava/io/PrintStream;
  #18 = NameAndType        #16:#17        // out:Ljava/io/PrintStream;
  #19 = Fieldref           #15.#18        // java/lang/System.out:Ljava/io/PrintStream;
  #20 = Utf8               java/io/PrintStream
  #21 = Class              #20            // java/io/PrintStream
  #22 = Utf8               println
  #23 = Utf8               (Ljava/lang/Object;)V
  #24 = NameAndType        #22:#23        // println:(Ljava/lang/Object;)V
  #25 = Methodref          #21.#24        // java/io/PrintStream.println:(Ljava/lang/Object;)V
```

- `#n` é a referência numérica.
- Algumas possíveis entradas são:

| Nome                 | Descrição                                                                                  |
|----------------------|--------------------------------------------------------------------------------------------|
| `Utf8`    | Um fluxo de bytes na codificação UTF8 de caracteres                                        |
| `NameAndType`         | Descreve um par nome e tipo, separados por dois pontos `:`.                                |
| `Class`              | Nome completo da classe.                                                                   |
| `Fieldref`           | Define um campo através do par `Class` e `NameType`, separados por ponto.                  |
| `Methodref`          | Define um método através do par `Class` e `NameType`, separados por ponto.                 |
| `InterfaceMethodref` | Define um método de uma interface através do par `Class` e `NameType`, separados por ponto. |
| `String`             | Uma constante string. Refere-se a uma entrada `Utf8` que contém caracteres.                |
| `Integer`            | Uma constante inteira de 4 bytes.                                                          |
| `Float`              | Uma constante em ponto flutuante de 4 bytes.                                               |
| `Long`               | Uma constante inteira de 8 bytes.                                                          |
| `Double`             | Uma constante em ponto flutuante com precisão dupla de 8 bytes.                            |

### Descritores de Tipos

| Descritor     | Tipo                                                       |
|---------------|------------------------------------------------------------|
| B             | Byte                                                       |
| C             | Char                                                       |
| D             | Double                                                     |
| F             | Float                                                      |
| I             | Int                                                        |
| J             | Long                                                       |
| L<type name>; | Tipo referência (_e.g._, `Ljava/lang/String;` para String) |
| S             | Short                                                      |
| V             | Void                                                       |
| Z             | Boolean                                                    |
| [             | Array-of                                                   |

### Campos

- Considere a seguinte classe `Point.kt`:
    ```kotlin
    class Point(val x: Double, val y: Double) {
        fun getDistance(p: Point): Double {
            return (Math.sqrt(Math.pow(p.x - x, 2.0) + Math.pow(p.y - y, 2.0)))
        }
    }
    ```
- Os campos da classe são apresentados da seguinte forma pelo comando `javap -v -p pt.isel.Point`:
    ```text
      private final double x;
        descriptor: D
        flags: (0x0012) ACC_PRIVATE, ACC_FINAL
    
      private final double y;
        descriptor: D
        flags: (0x0012) ACC_PRIVATE, ACC_FINAL
    ```
- O descritor corresponde a `D` (Double), o tipo de cada campo.
- As _flags_ indicam a acessibilidade dos campos.

### Código do Método

- São listados os bytecodes dos códigos de todos os métodos, incluindo os construtores.
- O código do método `sayHello()`:
```text
  public final void sayHello();
    descriptor: ()V
    flags: (0x0011) ACC_PUBLIC, ACC_FINAL
    Code:
      stack=2, locals=1, args_size=1
         0: ldc           #13                 // String Hello
         2: getstatic     #19                 // Field java/lang/System.out:Ljava/io/PrintStream;
         5: swap
         6: invokevirtual #25                 // Method java/io/PrintStream.println:(Ljava/lang/Object;)V
         9: return
      LineNumberTable:
        line 5: 0
        line 6: 9
```
- Descritor `()V` indica que método não recebe nenhum parâmetro (`()`) e retorna _void_ (`V`). 
- Tem o _Evaluation Stack_ de tamanho máximo 2 (`stack=2`).
- Apenas uma variável local (`locals=1`), que é o `this`, e é também o argumento do método (`args_size=1`).
  - Os argumentos são variáveis locais.
  - Logo, _locals_ sempre será _args_size_ mais algum valor.
- `LineNumberTable` é para propósito de _debug_.
  - Faz a correspondência entre as linhas do código-fonte e as linhas do bytecode. 
  - Por exemplo, linha 5 do código-fonte corresponde à linha 0 do bytecode.
  - Por exemplo, linha 6 do código-fonte corresponde à linha 9 do bytecode (o retorno _Void_).

- Para a classe `Point.class`, podemos analisar o bytecode do construtor:
```text
  public pt.isel.Point(double, double);
    descriptor: (DD)V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=3, locals=5, args_size=3
         0: aload_0
         1: invokespecial #9                  // Method java/lang/Object."<init>":()V
         4: aload_0
         5: dload_1
         6: putfield      #13                 // Field x:D
         9: aload_0
        10: dload_3
        11: putfield      #16                 // Field y:D
        14: return
      LineNumberTable:
        line 3: 0
```
- `locals=5`: são 5 variáveis locais, `this`, `x`, `y` (campos), `x`, `y` (parâmetros do construtor).
- `args_size=3`: `this`, `x`, `y` (parâmetros do construtor).
- `stack=3`: o construtor tem o _Evaluation Stack_ de tamanho máximo 3. 

### Variáveis Locais

- Tabela de variáveis locais: cada variável é armazenada num _slot_.
```text
      LocalVariableTable:
        Start  Length  Slot  Name   Signature
            0      15     0  this   Lpt/isel/Point;
            0      15     1     x   D
            0      15     3     y   D
```
- "Name": nome da variável local;
- "Signature": descritor do tipo;
- "Slot": local de armazenamento da variável local;
  - `this` está sempre no _slot_ 0.
- "Start" e "Length": correspondem ao escopo das instruções do bytecode da variável local;
  - No exemplo, `this` está no escopo da instrução de bytes 0 a 14 (tamanho 15) do bytecode.

### _Evaluation Stack_

- É a pilha de execução de um método.
- Os elementos da pilha correspondem a valores de constantes, variáveis ou campos das instruções do bytecode.
- A pilha é LIFO.
- Note que a _Evaluation Stack_ **não** é o mesmo que a _JVM Stack_.
  - _JVM Stack_ é a pilha de métodos cujo elemento é um _frame_.
  - Cada _frame_ contém uma _Evaluation Stack_.
- Operações realizadas na _Evaluation Stack_: `[out1, ..., outN] → [in1, ..., inM]`
  1) Desempilha `out1, ..., outN` (`N` pode ser 0, _i.e._, não há valores a desempilhar);
  2) Realiza operação (tipicamente sobre os valores desempilhados);
  3) Empilha `in1, ..., inM` (`M` pode ser 0, _i.e._, não há valores a empilhar);
- Exemplo 1:
  - opcode: `add`
  - Stack: `[val1, val2] → [res]`
  - Descrição: desempilha `val1` e `val2`, realiza a soma entre `val1` e `val2`, empilha o resultado `res` da soma.
- Exemplo 2:
  - opcode: `dup`
  - Stack: `[refObj] → [refObj, refObj]`
  - Descrição: desempilha `refObj` e empilha `refObj` duas vezes.
- Exemplo de códigos com base no ficheiro [week08/sample21-bytecode/App.kt](../sample21-bytecode/App.kt):
  - `javap -v -p A.class`: método `sum`

| Operação | Pilha                    | Descrição                                                                 |
|----------|--------------------------|---------------------------------------------------------------------------|
| fload_1  | `[] → [val_x]`           | Empilha valor float do slot 1                                             |
| fload_2  | `[] → [val_y]`           | Empilha valor float do slot 2                                             |
| fadd     | `[val_x, val_y] → [res]` | Desempilha dois valores, soma-os (float) e empilha o resultado            |
| freturn  | `[res] → []`             | Desempilha resultado e retorna o controlo para o invocador com um `float` |

  - `javap -v -p A.class`: método construtor

| Operação         | Pilha              | Descrição                                                                         |
|------------------|--------------------|-----------------------------------------------------------------------------------|
| aload_0          | `[] → [objRef]`    | Empilha a referência do objeto do slot 0                                          |
| invokespecial #8 | `[objRef] → [res]` | Chama o método #8 (`<init>`) com o objRef e retorna o resultado (`void`, no caso) |
| return           | `[] → []`          | Retorna o controlo para o invocador com `void`                                    |

- `javap -v -p AppKt.class`: método `modulus`

| Operação         | _Evaluation Stack_          |
|------------------|-----------------------------|
| fload_0          | `[] → [val_x]`              |
| fload_0          | `[] → [val_x]`              |
| fmul             | `[val_x, val_x] → [res_x]`  |
| fload_1          | `[] → [val_y]`              |
| fload_1          | `[] → [val_y]`              |
| fmul             | `[val_y, val_y] -> [res_y]` |
| fadd             | `[res_x, res_y] -> [res_f]` |
| f2d              | `[res_f] -> [res_d]`        |
| invokestatic #12 | `[res_d] -> [res_d2]`       |
| d2f              | `[res_d2] -> [res_f2]`      |
| fstore_2         | `[res_f2] -> []`            |
| flaod_2          | `[res_f2] -> [res]`         |
| freturn          | `[res] -> []`               |

### Opcodes e Operações

- Bytecode = opcode (mnemónico) + argumentos
- Opcode: 1 byte.
- Argumentos: podem ser de 1 byte, 2 bytes, 3 bytes.
- No verbose do javap, a numeração antes das operações bytecode refere-se ao offset de bytes.
  - Por exemplo, `aload_1` ocupa apenas 1 byte; 
  - `ldc #ref` ocupa 3 bytes (1 para o opcode e 2 para os argumentos).
- Os principais grupos de operações são:
  - Constante (_e.g._, `iconst_1`, `ldc`)
  - Load (_e.g._, `aload_0`, `iload`)
  - Store (_e.g._, `fload_2`, `sstore`)
  - gestão do _Evaluation Stack_ (_e.g._, `swap`, `dup`)
  - Lógica e aritmética (_e.g._, `ladd`, `ior`)
  - Conversão de tipo (_e.g._, `i2b`, `d2i`)
  - Comparação (_e.g._, `lcmp`, `ifgt`)
  - Controlo de fluxo (_e.g._, `dreturn`, `goto`)
  - Referência a objeto (_e.g._, `new`, `putfield`, `invokespecial`)

- [Lista de alguns opcodes, estado da pilha e breve descrição.](https://iselpt-my.sharepoint.com/:x:/r/personal/fernanda_passos_isel_pt/_layouts/15/Doc.aspx?sourcedoc=%7Bcd1d6373-e09e-438f-a02f-19d261987c8d%7D&action=embedview&wdHideGridlines=True&wdHideHeaders=True&wdDownloadButton=True&wdInConfigurator=True)
- Lista completa com descrição completa: ver [especificação da JVM](https://docs.oracle.com/javase/specs/jvms/se22/jvms22.pdf).
