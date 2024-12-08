package com.example.alarmappvdef

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.alarmappvdef.databinding.ItemAlarmBinding

class AlarmAdapter(private var alarms: List<Alarm>, private val context: Context) :
    RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.bind(alarm)
    }

    override fun getItemCount(): Int = alarms.size

    inner class AlarmViewHolder(private val binding: ItemAlarmBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(alarm: Alarm) {
            // Mettre à jour les TextView avec l'heure et le label
            binding.textViewTime.text = "${alarm.hour}:${alarm.minute}"
            binding.textViewLabel.text = alarm.label

            // Mettre à jour l'état du Switch (activé ou désactivé)
            binding.switchActive.isChecked = alarm.isActive

            // Écouteur pour changer l'état de l'alarme
            binding.switchActive.setOnCheckedChangeListener { _, isChecked ->
                // Met à jour l'état de l'alarme dans la base de données
                val dbHelper = AlarmDbHelper(context)
                dbHelper.updateAlarmStatus(alarm.id, isChecked)

                // Créer une nouvelle liste mutable mise à jour de manière sûre
                val updatedAlarms = alarms.toMutableList() // Créer une MutableList
                updatedAlarms[adapterPosition] = alarm.copy(isActive = isChecked) // Modification de l'élément à l'index adapté
                alarms = updatedAlarms // Mettre à jour la liste des alarmes

                // Afficher un message pour informer l'utilisateur du changement d'état
                Toast.makeText(context, "Alarme ${if (isChecked) "activée" else "désactivée"}", Toast.LENGTH_SHORT).show()

                // Après la modification de la liste, mettre à jour l'interface en toute sécurité
                binding.root.post {
                    // Utiliser post() pour s'assurer que notifyItemChanged() ne s'exécute pas pendant le redessin
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }
}
