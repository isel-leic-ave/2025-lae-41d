# Metaprogramação (parte III)

- Esta parte corresponde a:
  - Adicionar a implementação dinâmica, com a API Class-file, do mapeamento de listas;
  - Fazer e analisar o benchmark desta implementação completa.

## Mapper Dinâmico (continuação)

- **Objetivo**: adicionar a implementação dinâmica do mapeamento das listas.
  - Deve-se gerar o código para carregar o mapeamento de uma lista de objetos para outra.
- Código da interface Mapper inclui um método `default` (implementação na própria interface) chamado `mapFromList`:
```java
package pt.isel.sample24;

import java.util.List;
import java.util.ArrayList;

public interface Mapper<T, R> {

    R mapFrom(T src);

    default List<R> mapFromList(List<T> src) {
        List<R> result = new ArrayList<R>();
        for (T t : src) { result.add(mapFrom(t)); }
        return result;
    }
}
```
- O _baseline_ para gerar os _bytecodes_ necessários corresponde a:
```java
public class ArtistSpotify2ArtistBaseline implements Mapper<ArtistSpotify, Artist> {
    private static Map< Pair< Class<?>, Class<?> >, Mapper<?, ?> > mappers = Map.ofEntries(
        entry(new Pair<>(Country.class, State.class), new Country2StateBaseline()),
        entry(new Pair<>(Song.class, Track.class), new Song2TrackBaseline())
    );

    private static <T, R> Mapper<T, R> loadMapper(Class<T> srcType, Class<R> destType) {
        return (Mapper<T, R>) mappers.get(new Pair<>(srcType, destType));
    }

    @Override
    public Artist mapFrom(ArtistSpotify src) {
        return new Artist(
            src.getName(),
            src.getKind(),
            src.getId(),
            loadMapper(Country.class, State.class).mapFrom(src.getCountry()),
            loadMapper(Song.class, Track.class).mapFromList(src.getSongs())
        );
    }
}

class Country2StateBaseline implements Mapper<Country, State> {

    @Override
    public State mapFrom(Country src) {
        return new State(src.getName(), src.getIdiom());
    }
}

class Song2TrackBaseline implements Mapper<Song, Track> {

    @Override
    public Track mapFrom(Song src) {
        return new Track(src.getName(), src.getYear());
    }
}
```
- Implementação completa disponível no subprojeto [week10/sample24-dynamic-mapper-metaprogramming-lists](../sample24-dynamic-mapper-metaprogramming-lists)
- Note que:
  - O pacote usado é `pt.isel.sample24` para não ser confundido com o exemplo anterior (sem o mapeamento das listas);
  - Foi adicionado a classe `Song2TrackBaseline` que implementa o `Mapper<Song, Track>`;
  - Na criação do objeto `Artist`, carrega-se o `Mapper<Song, Track>` para chamar o método `mapFromList()`;
    - Este método faz o mapeamento de `List<Song>` para `List<Track>`;
- Quando os _baselines_ são compilados, o seu bytecode é gerado em [week10/sample24-dynamic-mapper-metaprogramming-lists/build/classes/java/test](../sample24-dynamic-mapper-metaprogramming-lists/build/classes/java/test).
  - A compilação dos _baselines_ pode ser feita executando o teste `genBytecodeJavaBaselines` no ficheiro [DynamicMapperTest.kt](../sample24-dynamic-mapper-metaprogramming-lists/src/test/kotlin/pt/isel/sample24/DynamicMapperTest.kt).
- Comparando o bytecode do método `mapFrom()` ao do projeto anterior ([week09/sample23-dynamic-mapper-metaprogramming](../../week09/sample23-dynamic-mapper-metaprogramming)), tem-se a seguinte diferença: 
    ```bytecode
    35: ldc           #56                 // class pt/isel/sample24/Song
    37: ldc           #58                 // class pt/isel/sample24/Track
    39: invokestatic  #45                 // Method loadMapper:(Ljava/lang/Class;Ljava/lang/Class;)Lpt/isel/sample24/Mapper;
    42: aload_1
    43: invokevirtual #60                 // Method pt/isel/sample24/ArtistSpotify.getSongs:()Ljava/util/List;
    46: invokeinterface #64,  2           // InterfaceMethod pt/isel/sample24/Mapper.mapFromList:(Ljava/util/List;)Ljava/util/List;
    ```
- Esta parte será implementada utilizando a API Class-file quando o **tipo de retorno de uma propriedade da classe de origem é uma lista**.
  - Ver o código completo da função `buildMapperByteArray` no ficheiro [DynamicMapper.kt](../sample24-dynamic-mapper-metaprogramming-lists/src/main/kotlin/DynamicMapper.kt).
  ```kotlin
  if (srcProp.returnType.classifier == List::class && destParam.type.classifier == List::class) {
      val elemSrcKlass = srcProp.returnType.arguments[0].type?.classifier as KClass<*>
      val elemDestKlass = destParam.type.arguments[0].type?.classifier as KClass<*>
      cob.ldc(cob.constantPool().classEntry(elemSrcKlass.descriptor())) // e.g., Song::class
      cob.ldc(cob.constantPool().classEntry(elemDestKlass.descriptor())) // e.g., Track::class
      cob.invokestatic(
          ClassDesc.of("${packageName}.${thisClassName}"),
          "loadDynamicMapper",
          MethodTypeDesc.of(
              Mapper::class.descriptor(),
              Class::class.descriptor(),
              Class::class.descriptor()
          ),
      )
      cob.aload(1)
      cob.invokevirtual(
          src.descriptor(),
          srcProp.javaGetter?.name,
          MethodTypeDesc.of(srcProp.returnType.descriptor())
      )
      cob.invokeinterface(
          Mapper::class.descriptor(),
          "mapFromList",
          MethodTypeDesc.of(CD_List, CD_List),
      )
  }
  ```
- Após executar o teste `mapArtistSpotifyToArtist` em [DynamicMapperTest.kt](../sample24-dynamic-mapper-metaprogramming-lists/src/test/kotlin/pt/isel/sample24/DynamicMapperTest.kt), as classes geradas dinamicamente encontram-se em:
  - [week10/sample24-dynamic-mapper-metaprogramming-lists/build/classes/java/test](../sample24-dynamic-mapper-metaprogramming-lists/build/classes/java/test). 

## Benchmarking

- Resultado comparativo com as execuções com apenas reflexão:
```text
Benchmark                                                      Mode  Cnt      Score     Error  Units
NaiveMapperBenchmark.mapArtistSpotifyToArtistVersion2          avgt    4  35251.230 ± 547.380  ns/op
NaiveMapperBenchmark.mapArtistSpotifyToArtistVersion4          avgt    4  17558.089 ± 357.554  ns/op
NaiveMapperBenchmark.mapArtistSpotifyToArtistVersion4Enhanced  avgt    4   8860.349 ± 726.109  ns/op
```

- Resultado com a execução do mapeamento dinâmico usando a API Class-file:

```text
Benchmark                                               Mode  Cnt    Score   Error  Units
DynamicMapperBenchmark.mapArtistSpotifyToArtistDynamic  avgt    4  123.699 ± 5.525  ns/op
```

- Houve uma melhora de aproximadamente **72 vezes** relativamente à versão melhorada do _NaiveMapper_ com apenas reflexão.