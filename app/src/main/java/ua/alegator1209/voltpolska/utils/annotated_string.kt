package ua.alegator1209.voltpolska.utils

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

fun AnnotatedString.Builder.bold(text: String): Unit = withStyle(
  SpanStyle(fontWeight = FontWeight.Bold)
) {
  append(text)
}

@ReadOnlyComposable
@Composable
fun AnnotatedString.Builder.value(text: String): Unit = withStyle(
  SpanStyle(color = MaterialTheme.colors.secondaryVariant)
) {
  append(text)
}
