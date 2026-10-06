package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.engine.BloodCompatibility
import com.example.data.engine.LifeLinkDataCoreEngine
import com.example.data.model.EmergencyDonor
import com.example.data.model.LifeLinkApiResponse
import com.example.data.model.SosBroadcast
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LifeLinkUiState(
  val selectedBloodGroup: String = "O+",
  val selectedLocation: String = "Chennai",
  val selectedComponent: String = "Whole Blood",
  val apiResponse: LifeLinkApiResponse? = null,
  val jsonExportText: String = "",
  val revealedDonorIds: Set<String> = emptySet(),
  val currentTab: Int = 0, // 0: Blood Banks, 1: Donors, 2: SOS, 3: Compatibility & Education
  val showRegisterDonorDialog: Boolean = false,
  val showCreateSosDialog: Boolean = false,
  val showCompatibilityDialog: Boolean = false,
  val showEligibilityChecker: Boolean = false,
  val showJsonEngineSheet: Boolean = false,
  val toastMessage: String? = null
)

class LifeLinkViewModel : ViewModel() {

  private val coreEngine = LifeLinkDataCoreEngine()

  private val _uiState = MutableStateFlow(LifeLinkUiState())
  val uiState: StateFlow<LifeLinkUiState> = _uiState.asStateFlow()

  init {
    refreshData()
  }

  fun selectBloodGroup(group: String) {
    _uiState.value = _uiState.value.copy(selectedBloodGroup = group)
    refreshData()
  }

  fun selectLocation(location: String) {
    _uiState.value = _uiState.value.copy(selectedLocation = location)
    refreshData()
  }

  fun selectComponent(component: String) {
    _uiState.value = _uiState.value.copy(selectedComponent = component)
    refreshData()
  }

  fun setTab(tabIndex: Int) {
    _uiState.value = _uiState.value.copy(currentTab = tabIndex)
  }

  fun refreshData() {
    val currentState = _uiState.value
    val response = coreEngine.query(
      rawBloodGroup = currentState.selectedBloodGroup,
      rawLocation = currentState.selectedLocation,
      rawComponent = currentState.selectedComponent
    )
    val json = coreEngine.toJson(response)
    _uiState.value = _uiState.value.copy(
      apiResponse = response,
      jsonExportText = json
    )
  }

  fun revealDonorContact(donorId: String) {
    val currentSet = _uiState.value.revealedDonorIds
    _uiState.value = _uiState.value.copy(
      revealedDonorIds = currentSet + donorId,
      toastMessage = "Consent confirmed. Emergency contact unmasked."
    )
  }

  fun clearToast() {
    _uiState.value = _uiState.value.copy(toastMessage = null)
  }

  fun setShowRegisterDonor(show: Boolean) {
    _uiState.value = _uiState.value.copy(showRegisterDonorDialog = show)
  }

  fun setShowCreateSos(show: Boolean) {
    _uiState.value = _uiState.value.copy(showCreateSosDialog = show)
  }

  fun setShowCompatibility(show: Boolean) {
    _uiState.value = _uiState.value.copy(showCompatibilityDialog = show)
  }

  fun setShowEligibilityChecker(show: Boolean) {
    _uiState.value = _uiState.value.copy(showEligibilityChecker = show)
  }

  fun setShowJsonEngineSheet(show: Boolean) {
    _uiState.value = _uiState.value.copy(showJsonEngineSheet = show)
  }

  fun registerDonor(
    fullName: String,
    bloodGroup: String,
    city: String,
    daysSinceLastDonation: Int,
    phoneNumber: String
  ) {
    val isEligible = daysSinceLastDonation >= 90
    val newId = "LL-${(7711..7999).random()}"
    val donor = EmergencyDonor(
      donorId = newId,
      fullName = fullName.trim(),
      bloodGroup = bloodGroup.trim().uppercase(),
      city = city.trim(),
      daysSinceLastDonation = daysSinceLastDonation,
      eligibilityStatus = if (isEligible) "Eligible" else "Ineligible",
      privacyConsent = true,
      hiddenContactToken = "REVEAL_VIA_CONSENT_TRIGGER",
      simulatedDistance = "0.8 km",
      rawContactNumber = phoneNumber.trim()
    )
    coreEngine.addDonor(donor)
    refreshData()
    _uiState.value = _uiState.value.copy(
      showRegisterDonorDialog = false,
      toastMessage = "Registered as $newId (${if (isEligible) "Eligible" else "Ineligible - 90d rule"})"
    )
  }

  fun createSosBroadcast(
    patientName: String,
    bloodGroup: String,
    unitsNeeded: Int,
    hospital: String,
    city: String,
    urgency: String,
    contact: String
  ) {
    val sos = SosBroadcast(
      patientName = patientName.trim(),
      bloodGroup = bloodGroup.trim().uppercase(),
      unitsNeeded = unitsNeeded,
      hospital = hospital.trim(),
      city = city.trim(),
      urgency = urgency.trim(),
      contact = contact.trim(),
      status = "Open"
    )
    coreEngine.addSosBroadcast(sos)
    refreshData()
    _uiState.value = _uiState.value.copy(
      showCreateSosDialog = false,
      toastMessage = "Emergency SOS Broadcast published!"
    )
  }
}
