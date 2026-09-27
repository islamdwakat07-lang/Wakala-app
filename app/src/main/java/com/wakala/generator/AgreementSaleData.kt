package com.wakala.generator

data class AgreementSaleData(
    val party1Name: String = "أحمد محمد يوسف الأحمد",
    val party1Id: String = "900112233",
    val party1Residence: String = "نابلس",
    val party2Name: String = "خالد سالم عبدالله الخطيب",
    val party2Id: String = "900445566",
    val party2Residence: String = "نابلس",
    val ownershipDescription: String = "حصص ارثية مشاعية في قطعتي",
    val qitaaNumbers: String = "52 و58",
    val hawzNumber: String = "12",
    val locationName: String = "جليجل بلاطه",
    val qada: String = "نابلس",
    val party2WishDescription: String = "كامل حصص الفريق الأول الارثية المشاعية و/او الاصلية المشاعية بالغا مابلغت في قطعتي",
    val party1SellDescription: String = "كامل حصصه الارثية المشاعية و/او الاصلية المشاعية بالغا مابلغت في قطعتي",
    val priceText: String = "ثمانية الاف واربعمائة وثمانون (8480) دينار اردني",
    val transferOffice: String = "دائرة تسجيل أراضي حورون و/او كاتب عدل نابلس و/او كاتب عدل القدس",
    val penaltyAmount: String = "10000",
    val courtName: String = "نابلس",
    val pagesCount: String = "صفحتين",
    val copiesCount: String = "ثلاثة (3)",
    val dateText: String = "",
    val extraClauses: List<String> = emptyList()
)
