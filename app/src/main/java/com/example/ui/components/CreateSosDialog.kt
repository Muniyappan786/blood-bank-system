package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.engine.BloodCompatibility

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateSosDialog(
  initialGroup: String,
  initialCity: String,
  onDismiss: () -> Unit,
  onBroadcast: (patientName: String, bloodGroup: String, units: Int, hospital: String, city: String, urgency: String, contact: String) -> Unit
) {
  var patientName by remember { mutableStateOf("") }
  var bloodGroup by remember { mutableStateOf(initialGroup) }
  var unitsText by remember { mutableStateOf("2") }
  var hospital by remember { mutableStateOf("") }
  var city by remember { mutableStateOf(initialCity) }
  var urgency by remember { mutableStateOf("Critical") }
  var contact by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Broadcast Emergency SOS",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.error
      )
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Instantly alert blood banks and compatible volunteer donors in the area.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
          value = patientName,
          onValueChange = { patientName = it },
          label = { Text("Patient Name") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("sos_patient_input")
        )

        Text(
          text = "Blood Group Needed:",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          BloodCompatibility.ALL_BLOOD_GROUPS.forEach { grp ->
            FilterChip(
              selected = bloodGroup == grp,
              onClick = { bloodGroup = grp },
              label = { Text(grp, fontWeight = FontWeight.Bold) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.error,
                selectedLabelColor = Color.White
              )
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = unitsText,
            onValueChange = { unitsText = it.filter { c -> c.isDigit() } },
            label = { Text("Units Needed") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("sos_units_input")
          )

          OutlinedTextField(
            value = city,
            onValueChange = { city = it },
            label = { Text("City") },
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("sos_city_input")
          )
        }

        OutlinedTextField(
          value = hospital,
          onValueChange = { hospital = it },
          label = { Text("Hospital Name & Branch") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("sos_hospital_input")
        )

        OutlinedTextField(
          value = contact,
          onValueChange = { contact = it },
          label = { Text("Emergency Contact Number") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("sos_contact_input")
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          listOf("Critical", "Urgent").forEach { lvl ->
            FilterChip(
              selected = urgency == lvl,
              onClick = { urgency = lvl },
              label = { Text(lvl) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = if (lvl == "Critical") Color(0xFFD32F2F) else Color(0xFFF57C00),
                selectedLabelColor = Color.White
              )
            )
          }
        }

        if (errorMessage != null) {
          Text(
            text = errorMessage ?: "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (patientName.isBlank() || hospital.isBlank() || contact.isBlank()) {
            errorMessage = "Please fill all required patient, hospital, and contact fields."
            return@Button
          }
          val units = unitsText.toIntOrNull() ?: 1
          onBroadcast(patientName, bloodGroup, units, hospital, city, urgency, contact)
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
        modifier = Modifier.testTag("submit_sos_button")
      ) {
        Text("Broadcast SOS Now")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
