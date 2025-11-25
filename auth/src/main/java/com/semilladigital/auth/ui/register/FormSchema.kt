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
    Question(
        id = "q1", fieldName = "tipoProduccion", questionText = "Tipo de Uso",
        type = QuestionType.Select, required = true, section = "Usos de la Parcela",
        options = listOf(
            Option("agricola", "Agricultura"),
            Option("ganadera", "Ganadería"),
            Option("pesca", "Pesca/Acuacultura"),
            Option("apicultura", "Apicultura")
        )
    ),
    Question(
        id = "q_act_agricola", fieldName = "actividadesAgricola", questionText = "Actividades Específicas",
        type = QuestionType.Checkbox, required = false, section = "Usos de la Parcela",
        conditionalField = "tipoProduccion", conditionalValue = "agricola",
        options = listOf(
            Option("maiz", "Siembra de Maíz"),
            Option("frijol", "Siembra de Frijol"),
            Option("chile", "Siembra de Chile"),
            Option("tomate", "Siembra de Tomate"),
            Option("calabaza", "Siembra de Calabaza"),
            Option("hortalizas", "Cultivo de Hortalizas"),
            Option("frutas", "Cultivo de Frutas")
        )
    ),
    Question(
        id = "q_act_ganadera", fieldName = "actividadesGanadera", questionText = "Actividades Específicas",
        type = QuestionType.Checkbox, required = false, section = "Usos de la Parcela",
        conditionalField = "tipoProduccion", conditionalValue = "ganadera",
        options = listOf(
            Option("vacas", "Cría de Vacas"),
            Option("cerdos", "Cría de Cerdos"),
            Option("ovejas", "Cría de Ovejas"),
            Option("cabras", "Cría de Cabras"),
            Option("pollos", "Cría de Pollos"),
            Option("caballos", "Cría de Caballos")
        )
    ),
    Question(
        id = "q_act_pesca", fieldName = "actividadesPesca", questionText = "Actividades Específicas",
        type = QuestionType.Checkbox, required = false, section = "Usos de la Parcela",
        conditionalField = "tipoProduccion", conditionalValue = "pesca",
        options = listOf(
            Option("mojarra", "Cría de Mojarra"),
            Option("tilapia", "Cría de Tilapia"),
            Option("camaron", "Cría de Camarón"),
            Option("trucha", "Cría de Trucha"),
            Option("carpa", "Cría de Carpa"),
            Option("bagre", "Cría de Bagre")
        )
    ),
    Question(
        id = "q_act_apicultura", fieldName = "actividadesApicultura", questionText = "Actividades Específicas",
        type = QuestionType.Checkbox, required = false, section = "Usos de la Parcela",
        conditionalField = "tipoProduccion", conditionalValue = "apicultura",
        options = listOf(
            Option("miel", "Producción de Miel"),
            Option("reina", "Cría de Abejas Reina"),
            Option("meliponicultura", "Meliponicultura (Abeja nativa)"),
            Option("derivados", "Cera, Propóleo y Jalea Real")
        )
    ),
    Question(
        id = "q2", fieldName = "anosExperiencia", questionText = "¿Cuántos años de experiencia tiene?",
        type = QuestionType.Number, required = true, section = "Información Adicional", min = 0, max = 80
    ),
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
    Question(
        id = "q9", fieldName = "trabajadores", questionText = "¿Cuántos trabajadores emplea?",
        type = QuestionType.Number, required = false, section = "Información Adicional", min = 0, max = 500
    ),
    Question(
        id = "q10", fieldName = "ventaProductos", questionText = "¿Dónde vende sus productos?",
        type = QuestionType.Select, required = false, section = "Información Adicional",
        options = listOf(
            Option("local", "Mercado local"), Option("intermediario", "Intermediarios"),
            Option("exportacion", "Exportación"), Option("consumo_propio", "Consumo propio")
        )
    ),
    Question(
        id = "q11", fieldName = "apoyosGubernamentales", questionText = "¿Ha recibido apoyos gubernamentales?",
        type = QuestionType.Radio, required = false, section = "Información Adicional",
        options = listOf(Option("si", "Sí"), Option("no", "No"))
    )
)