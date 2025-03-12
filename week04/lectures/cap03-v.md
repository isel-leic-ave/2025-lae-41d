# Reflection (parte V)

## NaiveMapper: Implementação III

- Desde o exemplo em [week02/sample05-naivemapper](../../week02/sample05-naivemapper), há uma versão do `NaiveMapper` que define uma classe.
  - Trata-se de uma implementação de uma classe chamada `NaiveMapper` com a função `mapFrom` para realizar o mapeamento.
  - A funcionalidade de mapeamento desta classe é igual a da função `mapTo()` referente à segunda versão do `NaiveMapper` 
    - mas através do método `mapFrom` de uma instância da classe `NaiveMapper`.
- O código a seguir, presente em [week03/sample06-naivemapper-annotations](../../week03/sample06-naivemapper-annotations), mostra tal implementação já com a funcionalidade das _Annotations_:
    ```kotlin
    class NaiveMapper<T : Any>(val srcType: KClass<*>, val destType:KClass<T>) {
        private val destCtor = destType
            .constructors
            .first { ctor ->
                ctor
                    .parameters
                    .filter { !it.isOptional }
                    .all { param -> srcType
                        .memberProperties
                        .any { matchPropWithParam(it, param) }
                    }
            }
    
        private val args: Map<KParameter, KProperty<*>?> = destCtor
            .parameters
            .associateWith { param -> srcType
                .memberProperties
                .firstOrNull() { matchPropWithParam(it, param) }
            }
            .filter { it.value != null }
    
        private fun matchPropWithParam(srcProp: KProperty<*>, param: KParameter) : Boolean {
            if(srcProp.name == param.name) {
                return srcProp.returnType == param.type
            }
            val annot = srcProp.findAnnotation<MapProp>() ?: return false
            return annot.paramName == param.name && srcProp.returnType == param.type
        }
    
        fun mapFrom(src: Any) : T {
            val args: Map<KParameter, Any?> = args
                .map { pair -> pair.key to pair.value?.call(src) }
                .associate { it }
            return destCtor.callBy(args)
        }
    }
    ```
- Etapas da implementação da classe `NaiveMapper`:
  1) Seleciona-se o construtor `ctor` com todos os parâmetros obrigatórios nas propriedades da classe de origem.
     1) É usada agora a função auxiliar `matchPropWithParam(srcProp, param)`.
        - Ela representa o predicado que realiza a correspondência das propriedades da classe de origem e dos parâmetros do construtor primário da classe de destino.
        - Devem corresponder o nome e o tipo da propriedade/parâmetro.
        - Considera também a anotação `MapProp` também para realizar a correspondência dos nomes.
     2) Este construtor é usado para construir o objeto final de retorno da função `mapFrom()`.
  2) Depois, procura-se a propriedade da origem correspondente a um parâmetro de `ctor` (destino), gerando-se o mapeamento `args`. 
     1) `args` é um mapeamento (parâmetro, propriedade), onde:
        - o **parâmetro** é um _KParameter_ do construtor da classe de destino e
        - a **propriedade** é um _KProperty_ da classe de origem.
        - Usa também a função auxiliar `matchPropWithParam(srcProp, param)` para realizar as correspondências.
        - Esse mapeamento é criado para ser usado com o método `callBy()`, conforme já visto na Implementação II.
  3) A função `mapFrom()` gera então o mapeamento final: um objeto do tipo da classe de destino com os valores das propriedades do objeto de origem.
     1) A partir de `args`, tem-se um novo `args` que:
        - associa o **parâmetro** à propriedade do objeto de destino e
        - associa o valor obtido da **propriedade** (pelo método `call()`) do objeto de origem.
     2) Por fim, chama-se o método `callby(args)` de `destCtor` com esse novo argumento `args` para gerar o objeto do tipo da classe de destino mapeado para os valores do objeto de origem.

## NaiveMapper: Reflexão de Tipos Não Primitivos

- Objetivo agora é incluir o mapeamento de tipos **não primitivos**.
  - Exemplo no domínio _Artist_: mapear um `ArtistSpotify` para `Artist`.
      ```kotlin
      class ArtistSpotify(
          val name: String,
          val kind: String,
          @MapProp("from") val country: Country 
      )
      ```
      ```kotlin
      class Artist(
         val name: String,
         val kind: String,
         val from: State
      )
      ```
    - Onde `Country` é definido por:
      - `class Country(val name: String, val idiom: String)`
    - `State` é definido por:
      - `class State(val name: String, val idiom: String)`

### 1) Alterações da Implementação III: Função `matchPropWithParam`

- De:
    ```kotlin
    private fun matchPropWithParamOld(srcProp: KProperty<*>, param: KParameter) : Boolean {
        if(srcProp.name == param.name) {
            return srcProp.returnType == param.type
        }
        val annot = srcProp.findAnnotation<MapProp>() ?: return false
        return annot.paramName == param.name && srcProp.returnType == param.type
    }    
    ```
- Para:
    ```kotlin
    private fun matchPropWithParam(srcProp: KProperty<*>, param: KParameter) : Boolean {
        val fromName = srcProp.findAnnotation<MapProp>()?.paramName ?: srcProp.name
        if(fromName != param.name) {
            return false
        }
        val srcKlass = srcProp.returnType.classifier as KClass<*>
        return if(srcKlass.java.isPrimitive)
            srcProp.returnType == param.type
        else
            true
    }
    ```
- Primeiramente, verifica-se o nome das propriedades de origem e de destino, fazendo-se uso das anotações.
- Depois, verifica-se se o tipo da classe de origem é primitivo ou não.
  - Se for, é retornado se o tipo de retorno da propriedade de origem igual ao tipo do parâmetro do construtor de destino.
  - Caso contrário, assume-se que o retorno é sempre verdadeiro para fazer-se o mapeamento mais tarde (recursivamente).
- **Notas**:
  1) Para verificar se o tipo é primitivo, obtem-se o tipo de `srcProp` através da propriedade `returnType`.
     - `srcProp.returnType` retorna um Ktype, que representa um tipo.
     - Um `Ktype` possui a propriedade `classifier` que retorna um `KClassifier`.
     - O tipo `KClassifier` pode ser:
          - Uma classe (`Kclass`);
          - Um parâmetro de tipo (`KTypeParameter`).
       - Por isso, é usado o _casting_ `as KClass<*>`, pois neste caso só é esperado um `Kclass`.
  2) De seguida, usa-se `srcKlass.java.isPrimitive` para aceder a propriedade da Reflexão de Java que indica se um tipo é primitivo.
     - A reflexão de Kotlin não possui propriedade semelhante.

### 2) Alterações da Implementação III: Função `mapFrom`

- De:
  ```kotlin
  fun mapFrom(src: Any) : T {
     val args: Map<KParameter, Any?> = args
        .map { pair -> pair.key to pair.value?.call(src) }
        .associate { it }
     return destCtor.callBy(args)
  }
  ```
- Para:
  ```kotlin
  fun mapFrom(src: Any) : T { 
     val args: Map<KParameter, Any?> = args
        .map { pair ->
            val propVal = pair.value?.call(src)
            pair.key to parse(pair.value!!.returnType, pair.key.type, propVal)
        }
        .associate { it }
     return destCtor.callBy(args)
  }
  ```  
- Basicamente, foi alterado o uso da função auxiliar `parse()`.
  - Ela permite fazer o `mapFrom()` recursivamente em objetos não primitivos.
  - Nos casos primitivos ou String, retorna o valor da propriedade.
  - A versão desta função a seguir mostra esta funcionalidade para objetos não primitivos excluindo coleções e Arrays.
    ```kotlin
    private fun parseWithoutList(srcType: KType, destType: KType, propVal: Any?): Any? {
        if(propVal == null) return null

        val srcKlass = srcType.classifier as KClass<Any>
        val destKlass = destType.classifier as KClass<Any>
        // Primitive or String case
        if(srcKlass.java.isPrimitive || srcKlass == String::class) {
            return propVal
        }

        return NaiveMapper(srcKlass, destKlass).mapFrom(propVal)
    }
    ```

## NaiveMapper: Reflexão de tipos _Generics_

### Conceitos Básicos de _Generics_ em Kotlin: Revisão

- **Parâmetros de tipos** (_Type parameters_): define um parâmetro de tipo utilizando `<T>` ao declarar uma classe, interface ou função.
    ```kotlin
    class Box<T>(val item: T)
    ```

- **Classes genéricas**: pode conter qualquer tipo, e pode utilizar esse tipo dentro dos métodos e propriedades da classe.
    ```kotlin
    val box: Box<Int> = Box(123)  // Box holding an Integer
    val boxString: Box<String> = Box("Hello")  // Box holding a String
    ```

- **Funções genéricas**: podem ter parâmetros de tipo, o que permite que a função funcione com qualquer tipo.
    ```kotlin
    fun <T> printItem(item: T) {
        println(item)
    }
    ```
- **Variância**: tem a ver com a produção ou consumo de subtipos de um tipo genérico.
  - Por exemplo, `Int` é um subtipo de `Number` e, portanto, valores de `Int` podem ser usados com segurança quando o tipo `Number` é esperado.
  - Covariância (palavra-chave: `out`): permite um tipo genérico produzir valores de um certo tipo ou subtipo.
      ```kotlin
      interface Producer<out T> {
          fun produce(): T
      }
      ```
  - Contravariância (palavra-chave: `in`): permite um tipo genérico consumir valores de um certo tipo ou subtipo.
      ```kotlin
      interface Consumer<in T> {
        fun consume(item: T)
      }
      ```
  - Considere o seguinte exemplo, onde a função `printOutput` espera um `Producer<Number>` como argumento.
    ```kotlin
    interface Producer<out T> {
        fun produce(): T
    }
    
    fun printOutput(producer: Producer<Number>) {
        val number: Number = producer.produce()
        println(number)
    }
    
    class IntegerProducer : Producer<Int> {
        override fun produce(): Int {
            return 42
        }
    }
    
    fun main(){
        printOutput(IntegerProducer())
    }
    ```
    - Sem o modificador `out` na interface `Producer`, a herança `IntegerProducer : Producer<Int>` não resulta.
      - Há um erro de tipo, pois o esperado é apenas um `Number`.
      - Para permitir o uso de subtipos, como o `Int`, é obrigatório o uso do `out` para a função `produce()` produzir um valor de um subtipo.

### NaiveMapper: Implementação IV

- Por questão de simplicidade, será visto o caso de `List<T>` como valor de uma propriedade (em qualquer profundidade).
- É importante, agora, entender a relação entre _Type_ e _Class_.
  - _Type_ pode ser _nullable_ e pode incluir **argumentos de tipos genéricos**. 
- O tipo `T` de `List` é um _Generics_.
  - Quando instanciado, terá um tipo (_e.g._, `Int`) que fará parte dos argumentos de tipo de `List`.
- _KType_ possui informações sobre a nulabilidade e os argumentos de tipos genéricos.
  - _KType_ pode ser obtido a partir da propriedade _returnType_ de um _KCallable_ ou da propriedade _type_ de um _KParameter_.
  - Relativamente aos argumentos, Ktype possui a propriedade `arguments` que retorna uma lista de `KTypeProjection`.
    - De forma simplificada, um `KTypeProjection` é uma classe que representa uma projeção de tipo que permite a variância (_generics_).
    - Por exemplo, no tipo `Array<out Number>`, `out Number` é uma covariação da classe `Number`.
    - Um `KTypeProjection` tem uma propriedade `type`, que retorna o Ktype do tipo da lista.
      - Por exemplo, o Ktype de `out Number` é `Number`.
    - Para o exemplo no domínio _Artist_, `List<Song>`, `Song` é o único tipo da lista de tipos genéricos em `arguments`.
      - Por isso, pode-se aceder ao `srcType.arguments[0].type` para se obter o tipo de referência da lista.
- A função `parse()` então torna-se:
    ```kotlin
    private fun parse(srcType: KType, destType: KType, propVal: Any?): Any? {
        if(propVal == null) return null

        val srcKlass = srcType.classifier as KClass<*>
        val destKlass = destType.classifier as KClass<*>
        // Primitive or String case
        if(srcKlass.java.isPrimitive || srcKlass == String::class) {
            return propVal
        }
        // List case: use of generics
        if(srcKlass == List::class && destKlass == List::class) {
            // srcType and destType are Ktype
            val elemSrcKlass = srcType.arguments[0].type?.classifier as KClass<*>
            val elemDestKlass = destType.arguments[0].type?.classifier as KClass<*>
            val mapper: NaiveMapper<*> = NaiveMapper(elemSrcKlass, elemDestKlass)
            return (propVal as List<Any>).map {
                mapper.mapFrom(it)
            }
        }
        // Object case:
        return NaiveMapper(srcKlass, destKlass).mapFrom(propVal)
        // Other cases (e.g., Array, Maps, Enum) need to be implemented
    }  
    ```
- Caso a classe de origem e a de destino sejam referentes a `List`, cada elemento da lista é avaliado recursivamente.
- A partir dos tipos (`Ktype`) `srcType` e `destType`, obtêm-se os argumentos dos tipos genéricos da lista.
  - Com a referência da classe de cada tipo, pode-se então instanciar um novo _mapper_ e chamar recursivamente o `mapFrom()`.
- Para outras estruturas, como _Arrays_ e outras coleções (_e.g._, Maps), é necessário incluir a sua implementação nesta função. 