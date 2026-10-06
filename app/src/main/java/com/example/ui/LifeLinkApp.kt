package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.engine.BloodCompatibility
import com.example.ui.components.BloodBankCard
import com.example.ui.components.BloodCompatibilityMatrixDialog
import com.example.ui.components.CreateSosDialog
import com.example.ui.components.EligibilityCheckerSheet
import com.example.ui.components.EmergencyDonorCard
import com.example.ui.components.JsonCoreEngineSheet
import com.example.ui.components.RegisterDonorDialog
import com.example.ui.components.SosBroadcastCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeLinkApp(
  viewModel: LifeLinkViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(uiState.toastMessage) {
    uiState.toastMessage?.let { msg ->
      snackbarHostState.showSnackbar(msg)
      viewModel.clearToast()
    }
  }

  // Modals & Sheets
  if (uiState.showCompatibilityDialog) {
    BloodCompatibilityMatrixDialog(
      initialGroup = uiState.selectedBloodGroup,
      onDismiss = { viewModel.setShowCompatibility(false) }
    )
  }

  if (uiState.showEligibilityChecker) {
    EligibilityCheckerSheet(
      onDismiss = { viewModel.setShowEligibilityChecker(false) }
    )
  }

  if (uiState.showJsonEngineSheet) {
    JsonCoreEngineSheet(
      jsonText = uiState.jsonExportText,
      apiResponse = uiState.apiResponse,
      onDismiss = { viewModel.setShowJsonEngineSheet(false) },
      onCopied = {
        Toast.makeText(context, "LifeLink API JSON copied to clipboard!", Toast.LENGTH_SHORT).show()
      }
    )
  }

  if (uiState.showCreateSosDialog) {
    CreateSosDialog(
      initialGroup = uiState.selectedBloodGroup,
      initialCity = uiState.selectedLocation,
      onDismiss = { viewModel.setShowCreateSos(false) },
      onBroadcast = { name, group, units, hospital, city, urgency, contact ->
        viewModel.createSosBroadcast(name, group, units, hospital, city, urgency, contact)
      }
    )
  }

  if (uiState.showRegisterDonorDialog) {
    RegisterDonorDialog(
      initialCity = uiState.selectedLocation,
      onDismiss = { viewModel.setShowRegisterDonor(false) },
      onRegister = { name, group, city, days, phone ->
        viewModel.registerDonor(name, group, city, days, phone)
      }
    )
  }

  Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopAppBar(
        title = {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = CircleShape,
              color = Color.White,
              modifier = Modifier.size(32.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  Icons.Default.Favorite,
                  contentDescription = "Logo",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "LifeLink",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black),
                color = Color.White
              )
              Text(
                text = "Blood & Emergency Donor Finder",
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 10.sp
              )
            }
          }
        },
        actions = {
          // JSON Core Engine inspect button
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.White.copy(alpha = 0.2f),
            modifier = Modifier
              .clickable { viewModel.setShowJsonEngineSheet(true) }
              .testTag("open_json_engine_button")
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
              Icon(
                Icons.Default.DataObject,
                contentDescription = "JSON Core Engine",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "API JSON",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.width(6.dp))

          IconButton(
            onClick = { viewModel.refreshData() },
            modifier = Modifier.testTag("refresh_button")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.topAppBarColors(
          containerColor = MaterialTheme.colorScheme.primary
        )
      )
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
      ) {
        val tabs = listOf(
          Triple("Blood Banks", Icons.Default.LocalHospital, 0),
          Triple("Donors", Icons.Default.People, 1),
          Triple("SOS Alerts", Icons.Default.Campaign, 2),
          Triple("Education", Icons.Default.School, 3)
        )
        tabs.forEach { (label, icon, index) ->
          NavigationBarItem(
            selected = uiState.currentTab == index,
            onClick = { viewModel.setTab(index) },
            icon = { Icon(icon, contentDescription = label) },
            label = { Text(label, fontSize = 11.sp, fontWeight = if (uiState.currentTab == index) FontWeight.Bold else FontWeight.Normal) },
            modifier = Modifier.testTag("tab_${label.replace(" ", "_").lowercase()}")
          )
        }
      }
    },
    floatingActionButton = {
      when (uiState.currentTab) {
        1 -> {
          ExtendedFloatingActionButton(
            onClick = { viewModel.setShowRegisterDonor(true) },
            icon = { Icon(Icons.Default.Add, contentDescription = "Register Donor") },
            text = { Text("Volunteer As Donor") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.testTag("fab_register_donor")
          )
        }
        2 -> {
          ExtendedFloatingActionButton(
            onClick = { viewModel.setShowCreateSos(true) },
            icon = { Icon(Icons.Default.Warning, contentDescription = "Broadcast SOS") },
            text = { Text("Broadcast SOS") },
            containerColor = Color(0xFFD32F2F),
            contentColor = Color.White,
            modifier = Modifier.testTag("fab_broadcast_sos")
          )
        }
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      // Emergency SOS Ticker Banner
      val activeSos = uiState.apiResponse?.activeSOSBroadcasts.orEmpty()
      if (activeSos.isNotEmpty()) {
        val topSos = activeSos.first()
        Surface(
          color = Color(0xFFB71C1C),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.setTab(2) }
            .testTag("emergency_sos_banner")
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.Campaign,
              contentDescription = "Alert",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "🚨 URGENT: ${topSos.patientName} needs ${topSos.unitsNeeded} units ${topSos.bloodGroup} at ${topSos.hospital}, ${topSos.city}",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
              color = Color.White,
              modifier = Modifier.weight(1f),
              maxLines = 1
            )
            Text(
              text = "VIEW",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Black),
              color = Color(0xFFFFEBEE)
            )
          }
        }
      }

      // Filter & Query Parameters Section (Sticky Search Bar)
      Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
          // Blood Group Chips Carousel
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Patient Blood Group:",
              style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
              color = MaterialTheme.colorScheme.onSurface
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.clickable { viewModel.setShowCompatibility(true) }
              ) {
                Text(
                  text = "Compatibility Matrix",
                  style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                  color = MaterialTheme.colorScheme.onPrimaryContainer,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            BloodCompatibility.ALL_BLOOD_GROUPS.forEach { group ->
              FilterChip(
                selected = uiState.selectedBloodGroup == group,
                onClick = { viewModel.selectBloodGroup(group) },
                label = { Text(group, fontWeight = FontWeight.Bold) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primary,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier.testTag("chip_blood_group_$group")
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Location & Component Filters
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              Icons.Default.LocationOn,
              contentDescription = "Location",
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(16.dp)
            )

            val cities = listOf("Chennai", "Coimbatore", "Bangalore", "Hyderabad", "Mumbai", "Delhi")
            cities.forEach { city ->
              FilterChip(
                selected = uiState.selectedLocation.equals(city, ignoreCase = true),
                onClick = { viewModel.selectLocation(city) },
                label = { Text(city, fontSize = 12.sp) },
                modifier = Modifier.testTag("chip_city_$city")
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            val components = listOf("Whole Blood", "Platelets", "Plasma", "PRBC")
            components.forEach { comp ->
              FilterChip(
                selected = uiState.selectedComponent.equals(comp, ignoreCase = true),
                onClick = { viewModel.selectComponent(comp) },
                label = { Text(comp, fontSize = 12.sp) }
              )
            }
          }
        }
      }

      // Tab Content
      Box(modifier = Modifier.fillMaxSize()) {
        when (uiState.currentTab) {
          0 -> BloodBanksTab(viewModel = viewModel, uiState = uiState)
          1 -> EmergencyDonorsTab(viewModel = viewModel, uiState = uiState)
          2 -> SosBroadcastsTab(viewModel = viewModel, uiState = uiState)
          3 -> EducationTab(viewModel = viewModel, uiState = uiState)
        }
      }
    }
  }
}

@Composable
private fun BloodBanksTab(
  viewModel: LifeLinkViewModel,
  uiState: LifeLinkUiState
) {
  val banks = uiState.apiResponse?.bloodBanks.orEmpty()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("blood_banks_list"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Blood Banks in ${uiState.selectedLocation}",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "${banks.size} centers with ${uiState.selectedComponent} stock",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    items(banks) { bank ->
      BloodBankCard(bank = bank)
    }

    item {
      Spacer(modifier = Modifier.height(60.dp))
    }
  }
}

@Composable
private fun EmergencyDonorsTab(
  viewModel: LifeLinkViewModel,
  uiState: LifeLinkUiState
) {
  val donors = uiState.apiResponse?.emergencyDonors.orEmpty()
  val eligibleCount = donors.count { it.eligibilityStatus.equals("Eligible", ignoreCase = true) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("emergency_donors_list"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      // Summary & Medical Rule Card
      Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Matched Volunteer Donors",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = Color(0xFFE8F5E9)
            ) {
              Text(
                text = "$eligibleCount of ${donors.size} Eligible",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF2E7D32),
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "• Medical rule: Donors are matched based on ABO & Rh compatibility with ${uiState.selectedBloodGroup}.\n• Eligibility rule: Donors with < 90 days gap are explicitly flagged Ineligible.\n• Privacy rule: Contact numbers are masked until emergency consent is granted.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
          )
        }
      }
    }

    items(donors) { donor ->
      EmergencyDonorCard(
        donor = donor,
        targetBloodGroup = uiState.selectedBloodGroup,
        isContactRevealed = uiState.revealedDonorIds.contains(donor.donorId),
        onRevealConsent = { viewModel.revealDonorContact(donor.donorId) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(70.dp))
    }
  }
}

@Composable
private fun SosBroadcastsTab(
  viewModel: LifeLinkViewModel,
  uiState: LifeLinkUiState
) {
  val sosList = uiState.apiResponse?.activeSOSBroadcasts.orEmpty()

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .testTag("sos_broadcasts_list"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Active Emergency Broadcasts",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.error
          )
          Text(
            text = "High priority requests needing immediate response",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    items(sosList) { sos ->
      SosBroadcastCard(sos = sos)
    }

    item {
      Spacer(modifier = Modifier.height(70.dp))
    }
  }
}

@Composable
private fun EducationTab(
  viewModel: LifeLinkViewModel,
  uiState: LifeLinkUiState
) {
  val education = uiState.apiResponse?.education

  Column(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp)
      .verticalScroll(rememberScrollState()),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Interactive Quick Tools Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = "Interactive Medical Tools",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
          color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Check whether you are eligible to donate today or view the full blood compatibility grid.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
          OutlinedButton(
            onClick = { viewModel.setShowEligibilityChecker(true) },
            modifier = Modifier
              .weight(1f)
              .testTag("open_eligibility_checker_button")
          ) {
            Icon(Icons.Default.HealthAndSafety, contentDescription = "Check", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Self-Check Gap", fontSize = 12.sp)
          }

          OutlinedButton(
            onClick = { viewModel.setShowCompatibility(true) },
            modifier = Modifier.weight(1f)
          ) {
            Icon(Icons.Default.School, contentDescription = "Matrix", modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("ABO Matrix", fontSize = 12.sp)
          }
        }
      }
    }

    // Eligibility Rule Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = Color(0xFFE8F5E9),
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(Icons.Default.CheckCircle, contentDescription = "Rule", tint = Color(0xFF2E7D32), modifier = Modifier.size(20.dp))
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Donor Eligibility Rule",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = education?.eligibilityRule ?: "Minimum 90 days gap required between successive blood donations.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "• Platelet donations (apheresis) can be done every 14 days (up to 24 times a year).\n• Whole blood requires 90 full days to ensure complete red cell and iron reserve recovery.\n• Donors must be 18–65 years old, weight >= 45 kg, with hemoglobin >= 12.5 g/dL.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 18.sp
        )
      }
    }

    // Myth Buster Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = Color(0xFFFFF3E0),
            modifier = Modifier.size(36.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(Icons.Default.School, contentDescription = "Myth", tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
            }
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Myth Buster",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = education?.mythBuster ?: "Myth: Blood donation makes you weak. Fact: The body replenishes the lost fluid volume within 24-48 hours.",
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "• Myth: Donating blood is painful.\nFact: It only feels like a quick pinch, lasting under 10 minutes.\n• Myth: Vegetarians don't have enough iron to donate.\nFact: Healthy vegetarian diets provide ample iron stores for safe regular donations.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 18.sp
        )
      }
    }

    // JSON Data Core Engine Architecture Info Card
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Code, contentDescription = "Engine", tint = Color(0xFF81C784))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "JSON Data Core Engine Architecture",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
            color = Color.White
          )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "The application backend runs an internal deterministic data core engine applying ABO/Rh blood compatibility matching, zero-error fallback mechanism, 90-day eligibility logic, and privacy consent tokenization.",
          style = MaterialTheme.typography.bodySmall,
          color = Color.LightGray,
          fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
          onClick = { viewModel.setShowJsonEngineSheet(true) }
        ) {
          Icon(Icons.Default.DataObject, contentDescription = "JSON", modifier = Modifier.size(16.dp), tint = Color(0xFF81C784))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Inspect Live API Output", color = Color(0xFF81C784), fontSize = 12.sp)
        }
      }
    }

    Spacer(modifier = Modifier.height(40.dp))
  }
}
