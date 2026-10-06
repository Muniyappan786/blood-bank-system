package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.BloodCompatibility
import com.example.data.model.EmergencyDonor

@Composable
fun EmergencyDonorCard(
  donor: EmergencyDonor,
  targetBloodGroup: String,
  isContactRevealed: Boolean,
  onRevealConsent: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var showConsentDialog by remember { mutableStateOf(false) }

  val isEligible = donor.eligibilityStatus.equals("Eligible", ignoreCase = true)
  val compatibilityText = BloodCompatibility.getCompatibilityBadge(donor.bloodGroup, targetBloodGroup)

  if (showConsentDialog) {
    AlertDialog(
      onDismissRequest = { showConsentDialog = false },
      icon = {
        Icon(
          Icons.Default.Lock,
          contentDescription = "Consent",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(28.dp)
        )
      },
      title = {
        Text("Emergency Contact Consent Protocol")
      },
      text = {
        Column {
          Text(
            text = "LifeLink Privacy Architecture protects volunteer donors from unsolicited calls.",
            style = MaterialTheme.typography.bodyMedium
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "By revealing ${donor.fullName}'s contact token, you confirm this request is for an active medical emergency requiring ${donor.bloodGroup} blood.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showConsentDialog = false
            onRevealConsent()
          },
          colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
          Text("I Confirm & Reveal")
        }
      },
      dismissButton = {
        TextButton(onClick = { showConsentDialog = false }) {
          Text("Cancel")
        }
      }
    )
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("emergency_donor_card_${donor.donorId}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header: Donor Name, ID, Blood Group Badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer,
            modifier = Modifier.size(42.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.Person,
                contentDescription = "Donor",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
              )
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = donor.fullName,
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "ID: ${donor.donorId} • ${donor.city} (${donor.simulatedDistance})",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Blood Group Badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primary,
          modifier = Modifier.padding(start = 6.dp)
        ) {
          Text(
            text = donor.bloodGroup,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Compatibility & Eligibility Chips
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Eligibility Tag (Strict 90 days rule)
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (isEligible) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(
              imageVector = if (isEligible) Icons.Default.CheckCircle else Icons.Default.Warning,
              contentDescription = "Eligibility",
              tint = if (isEligible) Color(0xFF2E7D32) else Color(0xFFC62828),
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isEligible) "Eligible (${donor.daysSinceLastDonation}d ago)"
              else "Ineligible (${donor.daysSinceLastDonation}d ago, min 90d)",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
              color = if (isEligible) Color(0xFF2E7D32) else Color(0xFFC62828)
            )
          }
        }

        // Medical Compatibility Tag
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = compatibilityText,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Contact Privacy Section
      if (!isContactRevealed) {
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                Icons.Default.Lock,
                contentDescription = "Privacy Protected",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(
                  text = "Protected: +91 ••••• •••••",
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "Token: ${donor.hiddenContactToken}",
                  style = MaterialTheme.typography.labelSmall,
                  fontSize = 10.sp,
                  color = MaterialTheme.colorScheme.outline
                )
              }
            }

            ElevatedButton(
              onClick = { showConsentDialog = true },
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.testTag("request_contact_consent_button_${donor.donorId}")
            ) {
              Text("Request Contact", fontSize = 12.sp)
            }
          }
        }
      } else {
        // Contact Revealed
        Surface(
          shape = RoundedCornerShape(10.dp),
          color = Color(0xFFE8F5E9),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Emergency Direct Line:",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF2E7D32)
                )
                Text(
                  text = donor.rawContactNumber,
                  style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                  color = Color(0xFF1B5E20)
                )
              }
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFFC8E6C9)
              ) {
                Text(
                  text = "Consent Verified",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF1B5E20),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = {
                  val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${donor.rawContactNumber}"))
                  context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("call_donor_button_${donor.donorId}")
              ) {
                Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Call Donor", fontSize = 12.sp)
              }

              OutlinedButton(
                onClick = {
                  val smsIntent = Intent(Intent.ACTION_VIEW, Uri.parse("sms:${donor.rawContactNumber}")).apply {
                    putExtra("sms_body", "Hello ${donor.fullName}, regarding urgent blood requirement for ${targetBloodGroup} blood on LifeLink.")
                  }
                  context.startActivity(smsIntent)
                },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("sms_donor_button_${donor.donorId}")
              ) {
                Icon(Icons.Default.Message, contentDescription = "SMS", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("SMS", fontSize = 12.sp)
              }
            }
          }
        }
      }
    }
  }
}
