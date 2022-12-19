package no.nav.dagpenger.innsending.html

import org.intellij.lang.annotations.Language

internal object TestHtml {
    @Language("HTML")
    val simpleHtml =
        """<html lang='nb'>
<head>
    <title>title</title>
    <meta name="subject" content="subject"/>
    <meta name="author" content="author"/>
    <meta name="description" content="description"/>
</head>
        """.trimIndent()
}
