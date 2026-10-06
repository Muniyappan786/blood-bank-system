package com.example.ui.components

import android.content.Context
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BloodBank

@Composable
fun BloodBankCard(
  bank: BloodBank,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  val stockColor = when (bank.stockStatus.lowercase()) {
    "critical" -> Color(0xFFD32F2F)
    "low" -> Color(0xFFF57C00)
    "surplus" -> Color(0xFF1976D2)
    else -> Color(0xFF388E3C)
  }

  val stockBg = when (bank.stockStatus.lowercase()) {
    "critical" -> Color(0xFFFFEBEE)
    "low" -> Color(0xFFFFF3E0)
    "surplus" -> Color(0xFFE3F2FD)
    else -> Color(0xFFE8F5E9)
  }

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("blood_bank_card_${bank.name.replace(" ", "_")}"),
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
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = bank.name,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LocationOn,
              contentDescription = "Address",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${bank.address}, ${bank.city}",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }

        // Units badge
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = stockBg,
          modifier = Modifier.padding(start = 8.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = "${bank.unitsAvailable}",
              style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
              color = stockColor
            )
            Text(
              text = "Units",
              style = MaterialTheme.typography.labelSmall,
              color = stockColor
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Status chips row
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Surface(
          shape = RoundedCornerShape(8.dp),
          color = stockBg
        ) {
          Text(
            text = "Status: ${bank.stockStatus}",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
            color = stockColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = "Component: ${bank.componentType}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = MaterialTheme.colorScheme.surfaceVariant
        ) {
          Text(
            text = bank.lastUpdated,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }

      if (bank.expiryAlert.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Alert",
            tint = if (bank.expiryAlert.contains("No upcoming", ignoreCase = true)) Color(0xFF66BB6A) else Color(0xFFFFA000),
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = bank.expiryAlert,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Action buttons
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        ElevatedButton(
          onClick = {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${bank.phone}"))
            context.startActivity(intent)
          },
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
          ),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .weight(1f)
            .testTag("call_blood_bank_button")
        ) {
          Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Call Bank", fontSize = 13.sp)
        }

        OutlinedButton(
          onClick = {
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
              type = "text/plain"
              putExtra(
                Intent.EXTRA_TEXT,
                "LifeLink Blood Bank Alert:\n${bank.name}\n${bank.unitsAvailable} Units of ${bank.componentType} available in ${bank.city}.\nPhone: ${bank.phone}"
              )
            }
            context.startActivity(Intent.createChooser(shareIntent, "Share Blood Bank Stock"))
          },
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("share_blood_bank_button")
        ) {
          Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
        }
      }
    }
  }
}
