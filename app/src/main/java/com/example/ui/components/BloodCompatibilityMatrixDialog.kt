package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.BloodCompatibility

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BloodCompatibilityMatrixDialog(
  initialGroup: String,
  onDismiss: () -> Unit
) {
  var selectedGroup by remember { mutableStateOf(initialGroup) }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  val compatibleDonors = BloodCompatibility.getCompatibleDonorGroups(selectedGroup)
  val compatibleRecipients = BloodCompatibility.getCompatibleRecipientGroups(selectedGroup)
  val compatiblePlasma = BloodCompatibility.getPlasmaDonorCompatibility(selectedGroup)

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
            text = "Medical Compatibility Matrix",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "Red Blood Cell & Plasma matching standards",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_compatibility_dialog")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Blood Group Selector Chips
      Text(
        text = "Select Blood Group to Inspect:",
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
      )
      Spacer(modifier = Modifier.height(8.dp))
      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        BloodCompatibility.ALL_BLOOD_GROUPS.forEach { group ->
          FilterChip(
            selected = selectedGroup == group,
            onClick = { selectedGroup = group },
            label = { Text(group, fontWeight = FontWeight.Bold) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primary,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimary
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Universal Donor / Recipient Highlights
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFFFF3E0),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(12.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            Icons.Default.Info,
            contentDescription = "Medical Rule",
            tint = Color(0xFFE65100),
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (selectedGroup == "O-") "🌟 O- is the Universal Donor: Can donate red blood cells to ANY blood group!"
            else if (selectedGroup == "AB+") "🌟 AB+ is the Universal Recipient: Can receive red blood cells from ANY blood group!"
            else "Standard ABO and Rh antigen matching required for $selectedGroup.",
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = Color(0xFF5D4037)
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Can Receive Red Cells From
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Patient $selectedGroup can RECEIVE red blood cells from:",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
          )
          Spacer(modifier = Modifier.height(8.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            compatibleDonors.forEach { donorGrp ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (donorGrp == "O-") Color(0xFFC8E6C9) else MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
              ) {
                Text(
                  text = if (donorGrp == "O-") "O- (Universal)" else donorGrp,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                  color = if (donorGrp == "O-") Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Can Donate Red Cells To
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Donor $selectedGroup can GIVE red blood cells to:",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.secondary
          )
          Spacer(modifier = Modifier.height(8.dp))
          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            compatibleRecipients.forEach { recipGrp ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (recipGrp == "AB+") Color(0xFFBBDEFB) else MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
              ) {
                Text(
                  text = if (recipGrp == "AB+") "AB+ (Universal)" else recipGrp,
                  style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                  color = if (recipGrp == "AB+") Color(0xFF0D47A1) else MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Plasma Rule Note
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Text(
            text = "Fresh Frozen Plasma (FFP) Rule:",
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF00796B)
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Plasma compatibility is reverse: Type AB plasma has no antibodies and can be given to ANY recipient (Universal Plasma Donor). Type O is the universal plasma recipient.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }
  }
}
