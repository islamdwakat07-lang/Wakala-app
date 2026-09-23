 package com.wakala.generator

data class WakalaData(
    val name1: String = "",
    val id1: String = "",
    val hasSecond: Boolean = false,
    val name2: String = "",
    val id2: String = "",
    val hawz: String = "",
    val hawzLocation: String = "",
    val qitaa: String = "",
    val village: String = "",
    val location: String = "حورون و/أو قدوم و/أو سالم و/أو بيت ايل",
    val qada: String = "",
    val dateText: String = ""
)

data class TextSegment(val text: String, val underlined: Boolean = false)

private fun ph(value: String, hint: String): String = value.ifBlank { "[$hint]" }

fun buildWakalaSegments(d: WakalaData): List<TextSegment> {
    val n1 = ph(d.name1, "اسم الموكل")
    val i1 = ph(d.id1, "رقم الهوية")
    val hawz = ph(d.hawz, "رقم الحوض")
    val hawzLocation = ph(d.hawzLocation, "اسم الموقع")
    val qitaa = ph(d.qitaa, "رقم القطعة")
    val village = ph(d.village, "اسم البلد")
    val location = ph(d.location, "اسم الدائرة")
    val qada = ph(d.qada, "اسم القضاء")

    val segments = mutableListOf<TextSegment>()
    fun t(text: String, underlined: Boolean = false) {
        segments += TextSegment(text, underlined)
    }

    if (d.hasSecond) {
        val n2 = ph(d.name2, "اسم الموكل الثاني")
        val i2 = ph(d.id2, "رقم هوية الموكل الثاني")

        t("نحن الموقعان أدناه اسمانا ")
        t(n1)
        t(" / هوية رقم ")
        t(i1, true)
        t(" و")
        t(n2)
        t(" / هوية رقم ")
        t(i2, true)
        t(" قد وكلنا عنا المحامي عبدالله إسماعيل عبدالله دويكات هوية رقم ")
        t("854544970", true)
        t(" من ")
        t("نابلس", true)
        t(" ليقدم و/أو يتابع استصدار اخراج قيد لدى دائرة تسجيل الأراضي في ")
        t(location)
        t(" ويرافع فيها على مأمور دائرة تسجيل أراضي ")
        t(location)
        t(" وأمام دائرة تسجيل أراضي ")
        t(location)
        t(" والتي موضوعها: ")
        t(
            "تقديم معاملة اصدار سند تسجيل و/أو اخراج قيد بأسمائنا و/أو باسم أي مورث من مورثينا و/أو مورثي مورثينا",
            true
        )
        t(" وذلك في قطعة الأرض رقم ")
        t(qitaa)
        t(" من الحوض رقم ")
        t(hawz)
        t(" موقع ")
        t(hawzLocation)
        t(" من أراضي ")
        t(village)
        t(" قضاء ")
        t(qada)
        t(
            " وبذات الوقت مراجعة دائرة التنظيم وتقديم المعاملة أمام هذه الدوائر في أي منطقة من المناطق " +
                "المذكورة أعلاه وأوكلناه بالتوقيع على جميع الطلبات والتعهدات والإقرارات وكافة الأوراق " +
                "الخاصة بتقديم المعاملة لدى دائرة تسجيل الأراضي ودائرة التنظيم وذلك حتى حصولنا على كافة " +
                "الموافقات اللازمة واستلام الردود وناتج المعاملات من وصول و/أو اخراجات قيد بأسمائنا في " +
                "قطعة الأرض الموصوفة أعلاه وعمل كل ما يلزم لذلك والتوقيع على كافة الأوراق اللازمة لهذا " +
                "الخصوص كما وكلناه باستلام ناتج المعاملات."
        )
    } else {
        t("أنا الموقع اسمي ")
        t(n1)
        t(" / هوية رقم ")
        t(i1, true)
        t(" قد وكلت عني المحامي عبدالله إسماعيل عبدالله دويكات هوية رقم ")
        t("854544970", true)
        t(" من ")
        t("نابلس", true)
        t(" ليقدم و/أو يتابع استصدار اخراج قيد لدى دائرة تسجيل الأراضي في ")
        t(location)
        t(" ويرافع فيها على مأمور دائرة تسجيل أراضي ")
        t(location)
        t(" وأمام دائرة تسجيل أراضي ")
        t(location)
        t(" والتي موضوعها: ")
        t(
            "تقديم معاملة اصدار سند تسجيل و/أو اخراج قيد باسمي و/أو باسم أي مورث من مورثيني و/أو مورثين مورثيني",
            true
        )
        t(" وذلك في قطعة الأرض رقم ")
        t(qitaa)
        t(" من الحوض رقم ")
        t(hawz)
        t(" موقع ")
        t(hawzLocation)
        t(" من أراضي ")
        t(village)
        t(" قضاء ")
        t(qada)
        t(
            " وبذات الوقت مراجعة دائرة التنظيم وتقديم المعاملة أمام هذه الدوائر في أي منطقة من المناطق " +
                "المذكورة أعلاه وأوكله بالتوقيع على جميع الطلبات والتعهدات والإقرارات وكافة الأوراق " +
                "الخاصة بتقديم المعاملة لدى دائرة تسجيل الأراضي ودائرة التنظيم وذلك حتى حصولي على كافة " +
                "الموافقات اللازمة واستلام الردود وناتج المعاملات من وصول و/أو اخراجات قيد باسمي في " +
                "قطعة الأرض الموصوفة أعلاه وعمل كل ما يلزم لذلك والتوقيع على كافة الأوراق اللازمة لهذا " +
                "الخصوص كما وكلته باستلام ناتج المعاملات."
        )
    }

    return segments
}
