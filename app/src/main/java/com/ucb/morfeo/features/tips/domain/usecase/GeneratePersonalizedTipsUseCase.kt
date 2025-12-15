package com.ucb.morfeo.features.tips.domain.usecase

import com.ucb.morfeo.features.core.database.entity.SleepCore
import com.ucb.morfeo.features.tips.domain.model.Tip
import com.ucb.morfeo.features.tips.domain.model.TipPriority
import com.ucb.morfeo.features.week.domain.model.WeeklySummary
import kotlin.math.abs

class GeneratePersonalizedTipsUseCase {

    fun execute(today: SleepCore?, weekly: WeeklySummary?): List<Tip> {
        val tips = mutableListOf<Tip>()

        // Si no hay data todavía
        if (today == null || weekly == null) {
            tips += Tip(
                id = "no_data",
                title = "Registra tu sueño",
                message = "Aún no hay datos suficientes. Registra al menos 1 día para generar consejos personalizados.",
                priority = TipPriority.HIGH
            )
            return tips
        }

        val sleepMin = today.sleepDuration
        val score = today.sleepScore
        val awakeMin = today.awakeDuration
        val deep = today.deepSleepPercentage
        val rem = today.remSleepPercentage

        // Consistencia semanal (si existe)
        val consistencyPct = (weekly.consistencyScore * 100f).coerceIn(0f, 100f)


        // 1) Duración
        if (sleepMin < 420) {
            tips += Tip(
                id = "duration_low",
                title = "Sube tu duración total",
                message = "Dormiste ${fmtMin(sleepMin)}. Objetivo recomendado: 7h–9h. Prueba acostarte 30–60 min antes hoy.",
                priority = TipPriority.HIGH
            )
        } else {
            tips += Tip(
                id = "duration_ok",
                title = "Buena duración",
                message = "Dormiste ${fmtMin(sleepMin)}. Mantén este rango para sostener energía y recuperación.",
                priority = TipPriority.LOW
            )
        }

        // 2) Score
        if (score < 70) {
            tips += Tip(
                id = "score_low",
                title = "Mejora tu higiene del sueño",
                message = "Tu score fue $score. Ideas rápidas: evita pantallas 30 min antes, cena ligera, cuarto fresco y oscuro.",
                priority = TipPriority.HIGH
            )
        } else if (score < 80) {
            tips += Tip(
                id = "score_mid",
                title = "Vas bien, falta pulir",
                message = "Tu score fue $score. Si fijas horarios y reduces interrupciones, subes fácil a 80+.",
                priority = TipPriority.MEDIUM
            )
        } else {
            tips += Tip(
                id = "score_good",
                title = "Excelente score",
                message = "Tu score fue $score. Repite la misma rutina 3–5 días para consolidar el hábito.",
                priority = TipPriority.LOW
            )
        }

        // 3) Despertares
        if (awakeMin >= 30) {
            tips += Tip(
                id = "awake_high",
                title = "Reduce despertares",
                message = "Estuviste despierto ${fmtMin(awakeMin)}. Revisa: cafeína tarde, líquidos antes de dormir y ruido/luz.",
                priority = TipPriority.MEDIUM
            )
        }

        // 4) Fases (no es médico, son tips de hábitos)
        if (deep < 15f) {
            tips += Tip(
                id = "deep_low",
                title = "Aumenta sueño profundo",
                message = "Deep ${deep.toInt()}%. Ayuda: fuerza/actividad física temprano, evita alcohol, duerme a hora constante.",
                priority = TipPriority.MEDIUM
            )
        }
        if (rem < 18f) {
            tips += Tip(
                id = "rem_low",
                title = "Cuida tu REM",
                message = "REM ${rem.toInt()}%. Ayuda: duerme suficiente (REM sube al final), evita desvelos y siestas largas.",
                priority = TipPriority.MEDIUM
            )
        }

        // 5) Consistencia semanal
        // 5) Consistencia semanal (0..100)
        if (consistencyPct < 80f) {
            tips += Tip(
                id = "consistency_low",
                title = "Más consistencia",
                message = "Tu consistencia es ${consistencyPct.toInt()}%. Intenta mantener la hora de dormir/despertar con variación ≤ 30 min.",
                priority = TipPriority.HIGH
            )
        } else if (consistencyPct < 90f) {
            tips += Tip(
                id = "consistency_mid",
                title = "Consistencia buena",
                message = "Consistencia ${consistencyPct.toInt()}%. Si ajustas 15–20 min tu rutina, puedes llegar a nivel excelente.",
                priority = TipPriority.MEDIUM
            )
        } else {
            tips += Tip(
                id = "consistency_good",
                title = "Consistencia excelente",
                message = "Consistencia ${consistencyPct.toInt()}%. Esto es de lo más importante para mejorar sueño y energía.",
                priority = TipPriority.LOW
            )
        }


        // Bonus: “micro-tip” si score bajo pero duración alta (caso típico)
        if (sleepMin >= 450 && score < 70) {
            tips += Tip(
                id = "duration_high_score_low",
                title = "Dormiste bastante, pero calidad baja",
                message = "Si duermes suficiente y el score sigue bajo, revisa despertares, estrés, ruido y temperatura del cuarto.",
                priority = TipPriority.MEDIUM
            )
        }

        return tips.sortedBy { it.priority.ordinal } // HIGH primero (porque enum: HIGH, MEDIUM, LOW)
    }

    private fun fmtMin(min: Long): String {
        val h = min / 60
        val m = min % 60
        return "${h}h ${m}m"
    }
}
