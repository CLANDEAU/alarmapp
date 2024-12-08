package com.example.alarmappvdef

import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Toast
import android.widget.TimePicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.alarmappvdef.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: AlarmDbHelper
    private lateinit var alarmAdapter: AlarmAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialisation de la base de données
        dbHelper = AlarmDbHelper(this)

        // Récupérer les alarmes depuis la base de données
        val alarms = dbHelper.getAllAlarms()

        // Configurer RecyclerView
        alarmAdapter = AlarmAdapter(alarms, this)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = alarmAdapter

        // Ajouter une alarme
        binding.buttonAddAlarm.setOnClickListener {
            val label = binding.editTextLabel.text.toString() // Récupère le label de l'alarme

            if (label.isNotBlank()) { // Vérifie que le label n'est pas vide
                // Afficher un TimePickerDialog pour choisir l'heure et la minute
                val timePickerDialog = TimePickerDialog(
                    this, // Utilisation du contexte de l'activité
                    { _, hourOfDay, minute ->
                        // Ajouter l'alarme à la base de données avec l'heure et le label
                        dbHelper.addAlarm(hourOfDay, minute, label)
                        Toast.makeText(this, "Alarme ajoutée: $hourOfDay:$minute avec le label '$label'", Toast.LENGTH_SHORT).show()

                        // Mettre à jour la liste après ajout
                        val updatedAlarms = dbHelper.getAllAlarms()
                        alarmAdapter = AlarmAdapter(updatedAlarms, this)
                        binding.recyclerView.adapter = alarmAdapter
                    },
                    8, 30, true // Valeurs par défaut de l'heure (8:30)
                )
                timePickerDialog.show()
            } else {
                Toast.makeText(this, "Le label de l'alarme ne peut pas être vide", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
