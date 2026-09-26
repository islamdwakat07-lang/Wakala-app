package com.wakala.generator

private fun phIqrar(value: String, hint: String): String = value.ifBlank { "[$hint]" }

fun buildIqrarTanzimSegments(d: IqrarTanzimData): List<TextSegment> {
    val name = phIqrar(d.applicantName, "اسم مقدّم الإقرار")
    val id = phIqrar(d.idNumber, "رقم الهوية")
    val qitaa = phIqrar(d.qitaa, "رقم القطعة")
    val hawz = phIqrar(d.hawz, "رقم الحوض")
    val village = phIqrar(d.village, "اسم القرية")
    val requestNumber = phIqrar(d.requestNumber, "رقم الطلب")

    val segments = mutableListOf<TextSegment>()
    fun t(text: String, underlined: Boolean = false) {
        segments += TextSegment(text, underlined)
    }

    t("أنا الموقع أدناه ")
    t(name, true)
    t(" حامل هوية رقم ")
    t(id, true)
    t("، اقر بذلك أن قطعة رقم ")
    t(qitaa, true)
    t(" بحوض رقم ")
    t(hawz, true)
    t(" من أراضي قرية ")
    t(village, true)
    t("، والتي قمت بتقديم طلب معلومات رقم ")
    t(requestNumber, true)
    t(
        " على القطعة المذكورة أعلاه إلى دائرة التنظيم، هي قطعة لا يوجد عليها مصادرة حسب أمر قائد " +
            "عسكري صدر بموجب صلاحياته بناء على نظام رقم 119 من أنظمة الدفاع (حالة الطوارئ) 1945، " +
            "كما اقر أنه لم تتم مصادرة أي بناء قائم أو كان قائماً على الأرض المذكورة أعلاه."
    )

    return segments
}
