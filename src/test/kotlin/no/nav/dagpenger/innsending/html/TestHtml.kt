package no.nav.dagpenger.innsending.html

import org.intellij.lang.annotations.Language

internal object TestHtml {
    val lang = "nb"
    val tittel = "En tittel"

    @Language("HTML")
    val simpleHtml =
        """<html lang='$lang'>
<head>
    <title>$tittel</title>
    <meta name="subject" content="subject"/>
    <meta name="author" content="author"/>
    <meta name="description" content="description"/>
    <link rel="icon" type="image/png" sizes="16x16" href="/dagpenger/dialog/favicon/favicon-16x16.png"/>
    <link rel="icon" type="image/png" sizes="32x32" href="/dagpenger/dialog/favicon/favicon-32x32.png"/>
    <link rel="preload" href="/dagpenger/dialog/_next/static/css/67aec5957859d7b0.css" as="style"/>
    <link rel="preload" href="/dagpenger/dialog/_next/static/css/7017b59e0c86bc67.css" as="style"/>
    <link rel="shortcut icon" href="/dagpenger/dialog/favicon/favicon.ico"/>
    <link rel="stylesheet" href="/dagpenger/dialog/_next/static/css/67aec5957859d7b0.css" data-n-g=""/>
    <link rel="stylesheet" href="/dagpenger/dialog/_next/static/css/7017b59e0c86bc67.css" data-n-p=""/>
    <link rel="stylesheet" href="http://absolut.url/1.css" data-n-p=""/>
    <link rel="stylesheet" href="https://absolut.url/2.css" data-n-p=""/>
</head>
<body>
<main></main>
<p>
    <hi>Hei på deg</hi>
</p>
</body>
</html
        """.trimIndent()
}
