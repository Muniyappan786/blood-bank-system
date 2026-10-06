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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.engine.BloodCompatibility

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterDonorDialog(
  initialCity: String,
  onDismiss: () -> Unit,
  onRegister: (fullName: String, bloodGroup: String, city: String, daysSinceLastDonation: Int, phone: String) -> Unit
) {
  var fullName by remember { mutableStateOf("") }
  var bloodGroup by remember { mutableStateOf("O+") }
  var city by remember { mutableStateOf(initialCity) }
  var daysText by remember { mutableStateOf("100") }
  var phone by remember { mutableStateOf("") }
  var consentAgreed by remember { mutableStateOf(true) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  val days = daysText.toIntOrNull() ?: 0
  val isEligible = days >= 90

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Register as Volunteer Donor",
        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.primary
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
          text = "Join LifeLink's emergency roster. Your contact is tokenized under privacy architecture.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
          value = fullName,
          onValueChange = { fullName = it },
          label = { Text("Full Name") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("donor_name_input")
        )

        Text(
          text = "Blood Group:",
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
                selectedContainerColor = MaterialTheme.colorScheme.primary,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
              )
            )
          }
        }

        OutlinedTextField(
          value = city,
          onValueChange = { city = it },
          label = { Text("City / Region") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("donor_city_input")
        )

        OutlinedTextField(
          value = daysText,
          onValueChange = { daysText = it.filter { c -> c.isDigit() } },
          label = { Text("Days Since Last Donation") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("donor_days_input")
        )

        // Realtime 90-day eligibility indicator
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isEligible) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ) {
          Text(
            text = if (isEligible) "Status: Eligible to Donate (>= 90 days interval met)"
            else "Status: Ineligible (${90 - days} days remaining before 90-day interval)",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
            color = if (isEligible) Color(0xFF2E7D32) else Color(0xFFC62828),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Phone Number") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("donor_phone_input")
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          Checkbox(
            checked = consentAgreed,
            onCheckedChange = { consentAgreed = it }
          )
          Text(
            text = "I consent to emergency donor discovery with tokenized contact protection.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
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
          if (fullName.isBlank() || city.isBlank() || phone.isBlank()) {
            errorMessage = "Please enter your full name, city, and phone number."
            return@Button
          }
          if (!consentAgreed) {
            errorMessage = "Please accept the privacy consent agreement."
            return@Button
          }
          onRegister(fullName, bloodGroup, city, days, phone)
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.testTag("submit_donor_registration_button")
      ) {
        Text("Register as Volunteer")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
