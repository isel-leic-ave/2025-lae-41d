# Metaprogramação (parte II)

## NaiveMapper Dinâmico

- Código em [DynamicMapper.kt](../sample23-naive-mapper-metaprogramming/src/main/kotlin/DynamicMapper.kt)
- A seguir, encontram-se detalhes da construção da função `loadDynamicMapper`.
- Código do baseline **sem** associação:
```java
public class ArtistSpotify2ArtistBaseline implements Mapper<ArtistSpotify, Artist> {
    @Override
    public Artist mapFrom(ArtistSpotify src) {
        return new Artist(
                src.getName(),
                src.getKind(),
                src.getId()
        );
    }
}
```
- A interface `Mapper` corresponde a:
```java
package pt.isel;

import java.util.List;

public interface Mapper<T, R> {

    R mapFrom(T src);
}
```
- Note que é uma interface que trabalha com tipos genéricos.
  - Os tipos genéricos são definidos inicialmente como `Object`.
- A seguir, ver o **bytecode** da classe `ArtistSpotify2ArtistBaseline` (precisa ser compilado antes).
  - No projeto do Intellij, os _.class_ encontram-se em `build/classes`.
- Imitar a criação da classe e a criação dos seguintes métodos utilizando a API ClassFile na função `buildMapperByteArray` do ficheiro [DynamicMapper.kt](../sample23-naive-mapper-metaprogramming/src/main/kotlin/DynamicMapper.kt).
  - `public ArtistSpotify2ArtistBaseline();`
  - `public java.lang.Object mapFrom(java.lang.Object);`
  - `public pt.isel.Artist mapFrom(pt.isel.ArtistSpotify);`
- Código do baseline **com** associação (mapeamento `Country` → `State`):
```java
public class ArtistSpotify2ArtistBaseline implements Mapper<ArtistSpotify, Artist> {
    private static Map<Pair<Class<?>, Class<?>>, Mapper<?, ?>> mappers = Map.ofEntries(
            entry(new Pair<>(Country.class, State.class), new Country2State())
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
                loadMapper(Country.class, State.class).mapFrom(src.getCountry())
        );
    }
}

class Country2State implements Mapper<Country, State> {

  @Override
  public State mapFrom(Country src) {
    return new State(src.getName(), src.getIdiom());
  }
}
```
- A seguir, alterar o código referente ao método `public pt.isel.Artist mapFrom(pt.isel.ArtistSpotify);`.
  - Incluir o código correspondente à chamada do `loadMapper`.
  - Alterar apenas o `ìnvokeestatic`:
    - Ao invés de chamar o `loadMapper`, irá chamar o `loadDynamicMapper` da classe `pt.isel.DynamicMapperClassfileKt`.

### Situação da Implementação

- Agora, há uma implementação do _mapper_ dinâmica de tipos primitivos, String e de tipos associados.
  - Falta implementar o mapeamento das listas (próxima aula).