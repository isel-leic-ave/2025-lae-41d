# Aula Prática

- Apoio de laboratório dedicado à segunda parte do trabalho.
  - [jdbcRepo2](https://github.com/isel-leic-ave/jdbcRepo).

## Baselines

É importante gerar baselines para cada exemplo de classe:
   - _User_ (classe simples);
   - _Channel_ (classe com Enum);
   - _Message_ (classe com associação).

Sugestão de implementação dos baselines, implementação da geração de código e testes:

1) Criar baseline simples que herda da classe _RepositoryReflect_ e implementa seu construtor.
   1) Testar o baseline (incluir a criação do baseline em todos os testes);
   2) Implementar em _loadDynamicRepo_ (em _RepositoryDynamic.kt_) a geração desta classe através do baseline (precisa ver o seu bytecode);
   3) Testar com o _loadDynamicRepo_ e verificar se passa nos testes (este caso ainda usa a reflexão antiga).
2) Criar um baseline para o _User_ com a sobrescrita do método _mapRowToEntity_ para o _User_.
   1) Testar o baseline (incluir a criação do baseline nos testes apenas do User);
   2) Adicionar a implementação em _loadDynamicRepo_ da geração deste método através do baseline (deve ser genérico);
   3) Testar apenas o _User_ com o _loadDynamicRepo_.
3) Fazer o mesmo para o _Channel_.
4) Fazer o mesmo para o _Message_.
   1) Neste caso, o baseline precisa chamar uma função para carregar um repositório para cada associação, similar ao que é feito no exemplo de aula do Mapper Dinâmico [ArtistSpotify2ArtistBaseline.java](../../week09/sample23-dynamic-mapper-metaprogramming/src/test/java/ArtistSpotify2ArtistBaseline.java).
   2) Na implementação da geração do código, deve-se trocar essa função de carregar o repositório para o _loadDynamicRepo_. 