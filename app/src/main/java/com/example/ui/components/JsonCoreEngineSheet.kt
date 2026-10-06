package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LifeLinkApiResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JsonCoreEngineSheet(
  jsonText: String,
  apiResponse: LifeLinkApiResponse?,
  onDismiss: () -> Unit,
  onCopied: () -> Unit
) {
  val context = LocalContext.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.DataObject,
            contentDescription = "Data Engine",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "LifeLink JSON Data Core Engine",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Production-grade backend structured API payload",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_json_sheet")
        ) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Status badges
      if (apiResponse != null) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0xFFE8F5E9)
          ) {
            Text(
              text = "searchStatus: ${apiResponse.searchStatus}",
              style = MaterialTheme.typography.labelSmall,
              color = Color(0xFF2E7D32),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "group: ${apiResponse.queryParameters.bloodGroup}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = "location: ${apiResponse.queryParameters.location}",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Raw JSON Viewer Box
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF1E1E1E),
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f, fill = false)
          .height(340.dp)
      ) {
        Box(
          modifier = Modifier
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
            .horizontalScroll(rememberScrollState())
        ) {
          Text(
            text = jsonText,
            color = Color(0xFF81C784),
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            modifier = Modifier.testTag("raw_json_text_display")
          )
        }
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Copy Action Button
      Button(
        onClick = {
          val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
          val clip = ClipData.newPlainText("LifeLink API JSON", jsonText)
          clipboard.setPrimaryClip(clip)
          onCopied()
        },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("copy_json_button")
      ) {
        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("Copy Raw JSON API Payload")
      }
    }
  }
}
