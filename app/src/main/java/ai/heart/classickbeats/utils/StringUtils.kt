package ai.heart.classickbeats.utils

import android.text.Html
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView

object StringUtils {

    private fun makeLinkClickable(
        strBuilder: SpannableStringBuilder,
        span: URLSpan?,
        onClickHandler: (String) -> Unit
    ) {
        val start = strBuilder.getSpanStart(span)
        val end = strBuilder.getSpanEnd(span)
        val flags = strBuilder.getSpanFlags(span)
        val clickable: ClickableSpan = object : ClickableSpan() {
            override fun onClick(view: View) {
                onClickHandler.invoke(span?.url ?: "")
            }
        }
        strBuilder.setSpan(clickable, start, end, flags)
        strBuilder.removeSpan(span)
    }

    fun setTextViewHTML(text: TextView, html: String?, onClickHandler: (String) -> Unit) {
        val sequence: CharSequence = Html.fromHtml(html)
        val strBuilder = SpannableStringBuilder(sequence)
        val urls = strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
        for (span in urls) {
            makeLinkClickable(strBuilder, span, onClickHandler)
        }
        text.text = strBuilder
        text.movementMethod = LinkMovementMethod.getInstance()
    }
}