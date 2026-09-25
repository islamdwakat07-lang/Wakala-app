package com.wakala.generator

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class DocumentCard(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val route: String,
    val badgeBackground: Color,
    val badgeTint: Color,
    val highlighted: Boolean = false
)

val documentCards = listOf(
    DocumentCard(
        id = "wakala_dawriya",
        title = "وكالة دورية",
        icon = Icons.Filled.Autorenew,
        route = "placeholder/wakala_dawriya",
        badgeBackground = Color(0xFFE3F2FD),
        badgeTint = Color(0xFF1565C0)
    ),
    DocumentCard(
        id = "ittifaqiyat_bay",
        title = "اتفاقية بيع",
        icon = Icons.Filled.SwapHoriz,
        route = "placeholder/ittifaqiyat_bay",
        badgeBackground = Color(0xFFF3E5F5),
        badgeTint = Color(0xFF7B1FA2)
    ),
    DocumentCard(
        id = "wakala_khususiya",
        title = "وكالة خصوصية اخراج قيد وتنظيم",
        icon = Icons.Filled.Description,
        route = "wakala_khususiya",
        badgeBackground = Color(0xFFFCE4EC),
        badgeTint = Color(0xFF8A1F2D),
        highlighted = true
    ),
    DocumentCard(
        id = "wakala_khassa_mahkama",
        title = "وكالة خاصة محكمة",
        icon = Icons.Filled.Gavel,
        route = "placeholder/wakala_khassa_mahkama",
        badgeBackground = Color(0xFFFFF3E0),
        badgeTint = Color(0xFFE65100)
    ),
    DocumentCard(
        id = "aqd_ijar",
        title = "عقد إيجار",
        icon = Icons.Filled.Apartment,
        route = "placeholder/aqd_ijar",
        badgeBackground = Color(0xFFE8F5E9),
        badgeTint = Color(0xFF2E7D32)
    ),
    DocumentCard(
        id = "iqrar_tanzim",
        title = "إقرار للتنظيم",
        icon = Icons.Filled.Assignment,
        route = "placeholder/iqrar_tanzim",
        badgeBackground = Color(0xFFEDE7F6),
        badgeTint = Color(0xFF4527A0)
    ),
    DocumentCard(
        id = "fath_safqa",
        title = "فتح صفقة",
        icon = Icons.Filled.Work,
        route = "placeholder/fath_safqa",
        badgeBackground = Color(0xFFFFFDE7),
        badgeTint = Color(0xFFF9A825)
    ),
    DocumentCard(
        id = "iqrar_istilam",
        title = "إقرار استلام مبلغ",
        icon = Icons.Filled.MonetizationOn,
        route = "placeholder/iqrar_istilam",
        badgeBackground = Color(0xFFE0F2F1),
        badgeTint = Color(0xFF00695C)
    ),
    DocumentCard(
        id = "iqrar_taahhud",
        title = "إقرار وتعهد عدلي",
        icon = Icons.Filled.VerifiedUser,
        route = "placeholder/iqrar_taahhud",
        badgeBackground = Color(0xFFEFEBE9),
        badgeTint = Color(0xFF4E342E)
    ),
    DocumentCard(
        id = "half_yamin",
        title = "حلف يمين",
        icon = Icons.Filled.MenuBook,
        route = "placeholder/half_yamin",
        badgeBackground = Color(0xFFECEFF1),
        badgeTint = Color(0xFF37474F)
    )
)

fun findDocumentCard(id: String): DocumentCard? {
    return documentCards.find { it.id == id }
}
