package pt.isel.annotations

@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS, AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
annotation class Color(val label: String)
