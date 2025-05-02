# Aula Prática

- Apoio de laboratório dedicado à segunda parte do trabalho.
  - [jdbcRepo2](https://github.com/isel-leic-ave/jdbcRepo).

## Baselines

1) Baseline simples que herda da classe _RepositoryReflect_ e implemente seu construtor.
   1) Testar o baseline;
   2) Implementar em _RepositoryDynamic.kt_ a geração desta classe através do baseline;
   3) Testar com o _loadDynamicRepo_.
2) Adicionar ao baseline a sobrescrita do método _mapRowToEntity_ para o _User_.
   1) Testar o baseline;
   2) Implementar em _RepositoryDynamic.kt_ a geração deste método através do baseline (deve ser genérico);
   3) Testar apenas o _User_ com o _loadDynamicRepo_.
3) Fazer o mesmo para o _Channel_.
4) Fazer o mesmo para o _Message_.