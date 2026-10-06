package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EligibilityCheckerSheet(
  onDismiss: () -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  var daysText by remember { mutableStateOf("100") }
  var ageText by remember { mutableStateOf("26") }
  var weightText by remember { mutableStateOf("65") }

  val days = daysText.toIntOrNull() ?: 0
  val age = ageText.toIntOrNull() ?: 0
  val weight = weightText.toIntOrNull() ?: 0

  val gapSatisfied = days >= 90
  val ageSatisfied = age in 18..65
  val weightSatisfied = weight >= 45
  val overallEligible = gapSatisfied && ageSatisfied && weightSatisfied

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surface
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .padding(bottom = 32.dp)
        .verticalScroll(rememberScrollState())
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Donor Eligibility Self-Check",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Medical standards based on Indian Red Cross & WHO",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_eligibility_sheet")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Input 1: Days since last donation
      Text(
        text = "Days Since Last Blood Donation:",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
      )
      Spacer(modifier = Modifier.height(4.dp))
      OutlinedTextField(
        value = daysText,
        onValueChange = { daysText = it.filter { char -> char.isDigit() } },
        label = { Text("Days (e.g. 95)") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("eligibility_days_input"),
        singleLine = true
      )

      Spacer(modifier = Modifier.height(6.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(0, 45, 90, 120).forEach { preset ->
          FilterChip(
            selected = days == preset,
            onClick = { daysText = preset.toString() },
            label = { Text("$preset d") }
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Input 2 & 3: Age & Weight
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = ageText,
          onValueChange = { ageText = it.filter { char -> char.isDigit() } },
          label = { Text("Age (Years)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier
            .weight(1f)
            .testTag("eligibility_age_input"),
          singleLine = true
        )

        OutlinedTextField(
          value = weightText,
          onValueChange = { weightText = it.filter { char -> char.isDigit() } },
          label = { Text("Weight (kg)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier
            .weight(1f)
            .testTag("eligibility_weight_input"),
          singleLine = true
        )
      }

      Spacer(modifier = Modifier.height(20.dp))

      // Live Assessment Result Card
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (overallEligible) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = if (overallEligible) Icons.Default.CheckCircle else Icons.Default.Warning,
              contentDescription = "Verdict",
              tint = if (overallEligible) Color(0xFF2E7D32) else Color(0xFFC62828),
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (overallEligible) "Eligible to Donate Blood Today!"
              else "Currently Ineligible to Donate",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = if (overallEligible) Color(0xFF1B5E20) else Color(0xFFB71C1C)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Breakdown items
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            EligibilityItem(
              title = "Donation Interval (Min 90 Days Gap)",
              passed = gapSatisfied,
              detail = if (gapSatisfied) "$days days completed (>= 90 days required)"
              else "${90 - days} days remaining before next safe donation"
            )
            EligibilityItem(
              title = "Age Requirement (18 - 65 Years)",
              passed = ageSatisfied,
              detail = if (ageSatisfied) "Age $age is within eligible bracket"
              else "Must be between 18 and 65 years"
            )
            EligibilityItem(
              title = "Weight Requirement (>= 45-50 kg)",
              passed = weightSatisfied,
              detail = if (weightSatisfied) "$weight kg meets health threshold"
              else "Weight must be at least 45 kg"
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Medical guidelines info
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Text(
            text = "Why the 90-day gap is critical:",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "While blood volume replenishes within 24 to 48 hours, bone marrow requires approximately 8 to 12 weeks to completely replenish iron and red blood cell stores. The 90-day rule protects donor wellness.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}

@Composable
private fun EligibilityItem(
  title: String,
  passed: Boolean,
  detail: String
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.Top
  ) {
    Text(
      text = if (passed) "✓" else "✕",
      color = if (passed) Color(0xFF2E7D32) else Color(0xFFC62828),
      fontWeight = FontWeight.Black,
      fontSize = 14.sp
    )
    Spacer(modifier = Modifier.width(8.dp))
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface
      )
      Text(
        text = detail,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}
