package com.example.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.sp

object MathSymbolFormatter {

    private val GREEK_AND_MATH_REPLACEMENTS = listOf(
        "\\times" to "×",
        "\\cdot" to "·",
        "\\div" to "÷",
        "\\pm" to "±",
        "\\mp" to "∓",
        "\\leq" to "≤",
        "\\le" to "≤",
        "\\geq" to "≥",
        "\\ge" to "≥",
        "\\neq" to "≠",
        "\\approx" to "≈",
        "\\sim" to "∼",
        "\\equiv" to "≡",
        "\\propto" to "∝",
        "\\infty" to "∞",
        "\\int" to "∫",
        "\\iint" to "∬",
        "\\iiint" to "∭",
        "\\oint" to "∮",
        "\\partial" to "∂",
        "\\nabla" to "∇",
        "\\sum" to "∑",
        "\\prod" to "∏",
        "\\rightarrow" to "→",
        "\\to" to "→",
        "\\leftarrow" to "←",
        "\\leftrightarrow" to "↔",
        "\\Rightarrow" to "⇒",
        "\\Leftarrow" to "⇐",
        "\\Leftrightarrow" to "⇔",
        "\\degree" to "°",
        "^\\circ" to "°",
        "\\circ" to "°",
        "\\angle" to "∠",
        "\\perp" to "⊥",
        "\\parallel" to "∥",
        "\\in" to "∈",
        "\\notin" to "∉",
        "\\subset" to "⊂",
        "\\subseteq" to "⊆",
        "\\cup" to "∪",
        "\\cap" to "∩",
        "\\Delta" to "Δ",
        "\\delta" to "δ",
        "\\Omega" to "Ω",
        "\\omega" to "ω",
        "\\lambda" to "λ",
        "\\Lambda" to "Λ",
        "\\mu" to "μ",
        "\\pi" to "π",
        "\\Pi" to "Π",
        "\\theta" to "θ",
        "\\Theta" to "Θ",
        "\\alpha" to "α",
        "\\beta" to "β",
        "\\gamma" to "γ",
        "\\Gamma" to "Γ",
        "\\epsilon" to "ε",
        "\\varepsilon" to "ε",
        "\\sigma" to "σ",
        "\\Sigma" to "Σ",
        "\\tau" to "τ",
        "\\phi" to "ϕ",
        "\\Phi" to "Φ",
        "\\rho" to "ρ",
        "\\eta" to "η",
        "\\zeta" to "ζ",
        "\\text" to "",
        "\\mathrm" to "",
        "\\mathbf" to "",
        "\\ce" to ""
    )

    private val SUPERSCRIPT_MAP = mapOf(
        '0' to '⁰', '1' to '¹', '2' to '²', '3' to '³', '4' to '⁴',
        '5' to '⁵', '6' to '⁶', '7' to '⁷', '8' to '⁸', '9' to '⁹',
        '+' to '⁺', '-' to '⁻', '=' to '⁼', '(' to '⁽', ')' to '⁾',
        'n' to 'ⁿ', 'i' to 'ⁱ', 'x' to 'ˣ', 'y' to 'ʸ'
    )

    private val SUBSCRIPT_MAP = mapOf(
        '0' to '₀', '1' to '₁', '2' to '₂', '3' to '₃', '4' to '₄',
        '5' to '₅', '6' to '₆', '7' to '₇', '8' to '₈', '9' to '₉',
        '+' to '₊', '-' to '₋', '=' to '₌', '(' to '₍', ')' to '₎',
        'a' to 'ₐ', 'e' to 'ₑ', 'h' to 'ₕ', 'i' to 'ᵢ', 'j' to 'ⱼ',
        'k' to 'ₖ', 'l' to 'ₗ', 'm' to 'ₘ', 'n' to 'ₙ', 'o' to 'ₒ',
        'p' to 'ₚ', 'r' to 'ᵣ', 's' to 'ₛ', 't' to 'ₜ', 'u' to 'ᵤ',
        'v' to 'ᵥ', 'x' to 'ₓ'
    )

    /**
     * Formats raw mathematical/chemical text containing LaTeX or formula notations
     * into clean, readable text with native unicode superscripts, subscripts, and symbols.
     */
    fun formatMathText(raw: String): String {
        if (raw.isBlank()) return raw

        var text = raw

        // Clean common fraction commands \frac{a}{b} -> (a/b)
        val fracRegex = Regex("""\\frac\{([^{}]+)\}\{([^{}]+)\}""")
        text = fracRegex.replace(text) { match ->
            val numerator = match.groupValues[1]
            val denominator = match.groupValues[2]
            "($numerator / $denominator)"
        }

        // Clean square roots \sqrt{x} or \sqrt[n]{x}
        val sqrtRegex = Regex("""\\sqrt(?:\[([^\[\]]+)\])?\{([^{}]+)\}""")
        text = sqrtRegex.replace(text) { match ->
            val rootDegree = match.groupValues[1]
            val radicand = match.groupValues[2]
            if (rootDegree.isNotEmpty()) {
                val degreeSup = rootDegree.map { SUPERSCRIPT_MAP[it] ?: it }.joinToString("")
                "${degreeSup}√($radicand)"
            } else {
                "√($radicand)"
            }
        }

        // Replace Greek and Math symbols
        for ((tex, symbol) in GREEK_AND_MATH_REPLACEMENTS) {
            text = text.replace(tex, symbol)
        }

        // Convert exponent patterns like ^{2}, ^2, ^3, etc.
        val supGroupRegex = Regex("""\^\{([0-9+\-nixy=()]+)\}""")
        text = supGroupRegex.replace(text) { match ->
            match.groupValues[1].map { SUPERSCRIPT_MAP[it] ?: it }.joinToString("")
        }

        val supSingleRegex = Regex("""\^([0-9+\-nixy])""")
        text = supSingleRegex.replace(text) { match ->
            val ch = match.groupValues[1][0]
            SUPERSCRIPT_MAP[ch]?.toString() ?: "^$ch"
        }

        // Convert subscript patterns like _{2}, _2, _1, etc.
        val subGroupRegex = Regex("""_\{([0-9+\-aehijklmnoprstuvx=()]+)\}""")
        text = subGroupRegex.replace(text) { match ->
            match.groupValues[1].map { SUBSCRIPT_MAP[it] ?: it }.joinToString("")
        }

        val subSingleRegex = Regex("""_([0-9+\-aehijklmnoprstuvx])""")
        text = subSingleRegex.replace(text) { match ->
            val ch = match.groupValues[1][0]
            SUBSCRIPT_MAP[ch]?.toString() ?: "_$ch"
        }

        // Format common chemical formulas: H2O -> H₂O, CO2 -> CO₂, CaCO3 -> CaCO₃, H2SO4 -> H₂SO₄, C6H12O6 -> C₆H₁₂O₆
        val chemicalFormulaRegex = Regex("""\b([A-Z][a-z]?)([0-9]+)\b""")
        text = chemicalFormulaRegex.replace(text) { match ->
            val element = match.groupValues[1]
            val count = match.groupValues[2]
            val subCount = count.map { SUBSCRIPT_MAP[it] ?: it }.joinToString("")
            "$element$subCount"
        }

        // Remove leftover TeX braces and $ symbols for clean inline display
        text = text.replace("{", "")
            .replace("}", "")
            .replace("$$", "")
            .replace("$", "")
            .replace("\\", "")

        return text
    }

    /**
     * Converts raw text containing math formulas into an AnnotatedString
     * with visual styling for formulas.
     */
    fun toAnnotatedMathString(raw: String): AnnotatedString {
        val formatted = formatMathText(raw)
        return buildAnnotatedString {
            append(formatted)
        }
    }
}
