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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SosBroadcast

@Composable
fun SosBroadcastCard(
  sos: SosBroadcast,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isCritical = sos.urgency.equals("Critical", ignoreCase = true)

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("sos_card_${sos.patientName.replace(" ", "_")}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCritical) Color(0xFFFFF0F0) else MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      // Header: SOS Urgency tag + Blood Group
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (isCritical) Color(0xFFD32F2F) else Color(0xFFF57C00)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                Icons.Default.Warning,
                contentDescription = "Alert",
                tint = Color.White,
                modifier = Modifier.size(14.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "SOS • ${sos.urgency.uppercase()}",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = sos.status,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.primary
        ) {
          Text(
            text = sos.bloodGroup,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Text(
        text = "Patient: ${sos.patientName}",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(4.dp))

      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          Icons.Default.LocalHospital,
          contentDescription = "Hospital",
          tint = MaterialTheme.colorScheme.primary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "${sos.hospital}, ${sos.city}",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(
        text = "Units Required: ${sos.unitsNeeded} Units",
        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.error
      )

      Spacer(modifier = Modifier.height(14.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ElevatedButton(
          onClick = {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${sos.contact}"))
            context.startActivity(intent)
          },
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Color(0xFFD32F2F),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("sos_call_button")
        ) {
          Icon(Icons.Default.Call, contentDescription = "Call SOS", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Call Hospital/Kin", fontSize = 13.sp)
        }

        OutlinedButton(
          onClick = {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(
                Intent.EXTRA_TEXT,
                "🚨 CRITICAL BLOOD SOS 🚨\nPatient: ${sos.patientName}\nNeeded: ${sos.unitsNeeded} units of ${sos.bloodGroup}\nHospital: ${sos.hospital}, ${sos.city}\nUrgent Contact: ${sos.contact}\nPlease help or forward immediately via LifeLink!"
              )
            }
            context.startActivity(Intent.createChooser(shareIntent, "Forward Blood SOS"))
          },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("sos_share_button")
        ) {
          Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}
