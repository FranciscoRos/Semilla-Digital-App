package com.semilladigital.auth.ui.register

enum class QuestionType { Text, Number, Select, Checkbox, Radio, Range, TextArea }

data class Option(val value: String, val label: String)

data class Question(
    val id: String,
    val fieldName: String,
    val questionText: String,
    val type: QuestionType,
    val required: Boolean = false,
    val options: List<Option> = emptyList(),
    val section: String,
    val min: Int? = null,
    val max: Int? = null,
    val conditionalField: String? = null,
    val conditionalValue: Any? = null
)

val QUESTION_SCHEMA = listOf(
    // Producción
    Question(
        id = "q1", fieldName = "tipoProduccion", questionText = "¿Qué tipo de producción realiza?",
        type = QuestionType.Select, required = true, section = "Información Adicional",
        options = listOf(
            Option("agricola", "Agrícola"), Option("ganadera", "Ganadera"),
            Option("mixta", "Mixta"), Option("forestal", "Forestal")
        )
    ),
    Question(
        id = "q2", fieldName = "anosExperiencia", questionText = "¿Cuántos años de experiencia tiene?",
        type = QuestionType.Number, required = true, section = "Información Adicional", min = 0, max = 80
    ),
    Question(
        id = "q3", fieldName = "cultivos", questionText = "¿Qué cultivos produce?",
        type = QuestionType.Checkbox, required = true, section = "Información Adicional",
        options = listOf(
            Option("maiz", "Maíz"), Option("frijol", "Frijol"), Option("trigo", "Trigo"),
            Option("sorgo", "Sorgo"), Option("hortalizas", "Hortalizas"), Option("frutales", "Frutales")
        )
    ),
    // Infraestructura
    Question(
        id = "q4", fieldName = "tieneRiego", questionText = "¿Cuenta con sistema de riego?",
        type = QuestionType.Radio, required = true, section = "Información Adicional",
        options = listOf(Option("si", "Sí"), Option("no", "No"))
    ),
    Question(
        id = "q5", fieldName = "fuenteAgua", questionText = "¿Cuál es la fuente de agua?",
        type = QuestionType.Text, required = false, section = "Información Adicional",
        conditionalField = "tieneRiego", conditionalValue = "si"
    ),
    Question(
        id = "q8", fieldName = "tipoMaquinaria", questionText = "¿Qué tipo de maquinaria utiliza?",
        type = QuestionType.Checkbox, required = false, section = "Información Adicional",
        options = listOf(
            Option("tractor", "Tractor"), Option("cosechadora", "Cosechadora"),
            Option("sembradora", "Sembradora"), Option("aspersora", "Aspersora"), Option("ninguna", "Ninguna")
        )
    ),
    // Prácticas Agrícolas
    Question(
        id = "q6", fieldName = "usaPesticidas", questionText = "¿Utiliza pesticidas o agroquímicos?",
        type = QuestionType.Radio, required = false, section = "Información Adicional",
        options = listOf(Option("si", "Sí"), Option("no", "No"))
    ),
    Question(
        id = "q7", fieldName = "certificacionOrganica", questionText = "¿Tiene certificación orgánica?",
        type = QuestionType.Radio, required = false, section = "Información Adicional",
        options = listOf(Option("si", "Sí"), Option("no", "No"), Option("en_proceso", "En proceso"))
    ),
    // Recursos Humanos
    Question(
        id = "q9", fieldName = "trabajadores", questionText = "¿Cuántos trabajadores emplea?",
        type = QuestionType.Number, required = false, section = "Información Adicional", min = 0, max = 500
    ),
    // Comercialización
    Question(
        id = "q10", fieldName = "ventaProductos", questionText = "¿Dónde vende sus productos?",
        type = QuestionType.Select, required = false, section = "Información Adicional",
        options = listOf(
            Option("local", "Mercado local"), Option("intermediario", "Intermediarios"),
            Option("exportacion", "Exportación"), Option("consumo_propio", "Consumo propio")
        )
    ),
    // Apoyos
    Question(
        id = "q11", fieldName = "apoyosGubernamentales", questionText = "¿Ha recibido apoyos gubernamentales?",
        type = QuestionType.Radio, required = false, section = "Información Adicional",
        options = listOf(Option("si", "Sí"), Option("no", "No"))
    )
)