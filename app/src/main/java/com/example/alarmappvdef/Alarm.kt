package com.example.alarmappvdef

data class Alarm(
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String? = null,
    val isActive: Boolean = false // Ajout du champ 'isActive' pour savoir si l'alarme est activée
)
