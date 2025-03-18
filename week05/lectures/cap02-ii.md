# Noções Básicas de Tipo (parte II)

## Fundamentos de Tipo: Membros

- Na última aula, foram apresentados os campos (_fields_).
  - São os membros de um tipo responsáveis por armazenar dados.
- Vimos brevemente o uso da ferramenta JOL para estimar a quantidade de memória utilizada por um tipo classe.
- Esta aula cobre:
  - Modificadores de acesso. 
  - Explorar mais a ferramenta JOL.
  - Métodos e construtores.

### Membros: Modificadores de Acesso

- Um acesso é:
  - _field_: obter/alterar seu valor diretamente, através do `.`
  - método: chamar o método.
- Os membros possuem 4 formas diferentes de acesso:
  - `private`: apenas a classe tem acesso.
  - `protected`: apenas a classe e quem herda dela tem acesso.
  - acesso _default_ (sem modificador): apenas as classes que são do mesmo pacote têm acesso.
  - `public`: qualquer classe tem acesso.
- Exemplo para teste de modificadores: [week05/sample09-modifiers](../sample09-modifiers)
  - Compilar: `javac Test.java` (já compila as classes necessárias)
  - Execução: `java Test`
  - Experimentar os erros comentados.

### Exemplo: Modificadores em Geral
- Exemplo com modificadores de acesso e modificadores static e final:
  - Disponível em: [Account.java](../sample09-modifiers/apps/Account.java)
  - Teste: [TestAccounts.java](../sample09-modifiers/apps/TestAccounts.java)
- Compilação no terminal:
  - `javac apps/Account.java`
  - `javac apps/TestAccounts.java`
- Execução:
  - `java apps.TestAccounts`

### JOL: Java Object Layout

- JOL: ferramenta que permite analisar os esquemas de layout de objetos e estimar os seus tamanhos na JVM.
- Pode ser integrado a IDE (_e.g._, no Intellij, instalar o plugin em _Settings->Plugins_).
- Pode ser descarregado do Maven Central Repository: [JOL jar no repositório do Maven.](https://repo1.maven.org/maven2/org/openjdk/jol/jol-cli/0.17/jol-cli-0.17-full.jar)
  - Execução no terminal: `java -cp .:jol-cli-0.17-full.jar org.openjdk.jol.Main estimates <class qualified name>`
    - Neste exemplo, a versão do JOL é 0.17.
    - No Windows, substituir o separador `:` por `;` no argumento de `-cp`.
- Existem implementações diferentes da JVM e a quantidade de bytes pode variar.
  - [Uma lista de implementações da JVM](https://en.wikipedia.org/wiki/List_of_Java_virtual_machines).
  - Hotspot é a implementação _open-source_ da OpenJDK.
    - JDK >= 8 
    - JDK >= 15
  - Lilliput: em desenvolvimento.
- Para acesso à memória geralmente mais eficiciente é usado:
  - Bytes de alinhamento: tipicamente 8 bytes.
    - Para memórias RAM muito grandes, podem ser necessários 16 bytes.
  - _Padding_: bytes são completados artificialmente com algum valor para permitir o alinhamento.
- Cada objeto na JVM começa com um **cabeçalho de objeto** de tamanho fixo.
  - O cabeçalho contém informações sobre o objeto:
    - Metadados específicos para a instância particular de uma classe (_mark_) e 
    - Metadados específicos da classe (_class_).
  - O cabeçalho do objeto não é diretamente acessível programaticamente.
  - A estrutura precisa deste cabeçalho pode variar ligeiramente dependendo da implementação e versão da JVM.
- Tamanho de palavras (_word_), do processador, diferentes:
  - 32-bits
  - 64-bits
  - Indicam o tamanho dos ponteiros de referência.
- Compressão de referência (COOPS):
  - Uma técnica de armazenar ponteiros de 64 bits em apenas 32 bits. 
    - Requer que todos os campos estejam alinhados.
- Compressão de _class_ num cabeçalho de objeto CCPS:
  - 4 bytes ao invés de 8 bytes.
- Exemplo de saída para a estimativa de tamanho da classe [SavingsAccount.java](../sample10-jol/apps/SavingsAccount.java):
  - Comando para compilar a classe no terminal (a partir de `week05/sample10-jol`): `javac apps/SavingsAccount.java`
  - Comando no terminal (a partir de `week05/sample10-jol`): `java -cp .:jol-cli-0.17-full.jar org.openjdk.jol.Main estimates apps.SavingsAccount`
  ```text
  ***** Hotspot Layout Simulation (JDK 15, 64-bit model, NO compressed references, NO compressed classes, 8-byte aligned)
  apps.SavingsAccount object internals:
  OFF  SZ               TYPE DESCRIPTION                   VALUE
    0   8                    (object header: mark)         N/A
    8   8                    (object header: class)        N/A
   16   8               long SavingsAccount.balance        N/A
   24   8             double SavingsAccount.interestRate   N/A
   32   2              short SavingsAccount.accountCode    N/A
   34   1            boolean SavingsAccount.isActive       N/A
   35   5                    (alignment/padding gap)       
   40   8   java.lang.String SavingsAccount.holderName     N/A
  Instance size: 48 bytes
  Space losses: 5 bytes internal + 0 bytes external = 5 bytes total
  ```

### Métodos

- Um método é um comportamento identificado por nome que pode ser invocado.
- Componentes gerais de um método em Java:
  - **Modificadores**: acesso (public, private, protected), static, final.
  - O **tipo de retorno**: o tipo de dados do valor devolvido pelo método, ou _void_ se o método não devolver um valor.
  - O **nome** do método.
  - A **lista de parâmetros** entre parênteses: uma lista delimitada por vírgulas de parâmetros de entrada.
    - Cada nome do parâmetro é precedido pelo seu tipo. 
    - Se não existirem parâmetros, deverá utilizar parênteses vazios.
  - Uma **lista de exceções** depois do uso da palavra reservada _throws_.
  - O **corpo** do método entre chavetas: o código do método incluindo a declaração de variáveis locais.
  ```java
  public void myMethod(int a, String b) throws Exception {
    System.out.println(a + " - " + b);
  }
  ```
- Métodos finais:
  - O `final` como modificador de um método impede que ele seja sobrescrito por uma subclasse.
- **Nota**: classes finais.
  - Classes também podem ser `final`.
  - Neste caso, elas não podem ser extendidas (herdadas).
  - Em Kotlin, toda classe é `final` por omissão.
    - O `open` remove o `final` e faz com que ela possa ser herdada.

#### Métodos Construtores

- Um método construtor deve ser definido de forma similar a um método, exceto por:
  1) O nome do método deve ser o nome da classe;
  2) Não tem tipo de retorno (não há retorno);
  3) Aceita apenas os modificadores (de acesso) `private`, `protected` e `public`.
- Os construtores podem ser parametrizados.
  - Aceita sobrecarga: mais de um construtor, desde que com assinaturas diferentes.

#### Método Construtor por Omissão

Toda classe tem ao menos um membro que é o método construtor.
- Considere novamente o seguinte exemplo (ficheiro [`Person.java`](../sample11-init/pt/isel/Person.java)):
  ```java
  package pt.isel;
  class Person {}
  ```
- Quando seu `.class` é inspecionado por `javap -p Person.class`, tem-se:
  ```java
  class pt.isel.Person {
    pt.isel.Person();
  }
  ```
- O método sem parâmetros `pt.isel.Person()` na classe `Person` é conhecido como o construtor por omissão do tipo.
- O construtor por omissão é gerado automaticamente pelo compilador quando uma classe não define explicitamente nenhum construtor.
- Um tipo pode fornecer vários construtores sobrecarregados.
- O construtor é chamado automaticamente pela JVM sempre que um objeto do tipo é instanciado.
- No exemplo da classe `Person`, o construtor recebe o nome da classe proprietária `pt.isel.Person()`.
- Na verdade, o método construtor tem o nome distinto `<init>`.
  - Construtor é um método com o nome `<init>` que retorna `void`.
  - Comando `javap -c Test.class` permite analisar os metadados de uma classe que instancia _Person_.
    - Permite analisar os metadados da chamada do construtor desta classe.
  - Exemplo em: [week05/sample11-init/](../sample11-init/)