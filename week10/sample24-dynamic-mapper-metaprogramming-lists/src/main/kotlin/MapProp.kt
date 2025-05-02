package pt.isel.sample24

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class MapProp(
    val paramName: String
)