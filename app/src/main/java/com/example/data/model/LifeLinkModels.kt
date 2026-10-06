package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LifeLinkApiResponse(
  @Json(name = "searchStatus") val searchStatus: String = "success",
  @Json(name = "systemTimestamp") val systemTimestamp: String,
  @Json(name = "queryParameters") val queryParameters: QueryParameters,
  @Json(name = "bloodBanks") val bloodBanks: List<BloodBank>,
  @Json(name = "emergencyDonors") val emergencyDonors: List<EmergencyDonor>,
  @Json(name = "activeSOSBroadcasts") val activeSOSBroadcasts: List<SosBroadcast>,
  @Json(name = "education") val education: EducationInfo
)

@JsonClass(generateAdapter = true)
data class QueryParameters(
  @Json(name = "bloodGroup") val bloodGroup: String,
  @Json(name = "location") val location: String,
  @Json(name = "component") val component: String
)

@JsonClass(generateAdapter = true)
data class BloodBank(
  @Json(name = "name") val name: String,
  @Json(name = "address") val address: String,
  @Json(name = "city") val city: String,
  @Json(name = "phone") val phone: String,
  @Json(name = "componentType") val componentType: String,
  @Json(name = "unitsAvailable") val unitsAvailable: Int,
  @Json(name = "lastUpdated") val lastUpdated: String,
  @Json(name = "stockStatus") val stockStatus: String,
  @Json(name = "expiryAlert") val expiryAlert: String
)

@JsonClass(generateAdapter = true)
data class EmergencyDonor(
  @Json(name = "donorId") val donorId: String,
  @Json(name = "fullName") val fullName: String,
  @Json(name = "bloodGroup") val bloodGroup: String,
  @Json(name = "city") val city: String,
  @Json(name = "daysSinceLastDonation") val daysSinceLastDonation: Int,
  @Json(name = "eligibilityStatus") val eligibilityStatus: String,
  @Json(name = "privacyConsent") val privacyConsent: Boolean,
  @Json(name = "hiddenContactToken") val hiddenContactToken: String,
  @Json(name = "simulatedDistance") val simulatedDistance: String,
  // Local helper for app actions once consent is granted:
  @Transient val rawContactNumber: String = "+919840123456"
)

@JsonClass(generateAdapter = true)
data class SosBroadcast(
  @Json(name = "patientName") val patientName: String,
  @Json(name = "bloodGroup") val bloodGroup: String,
  @Json(name = "unitsNeeded") val unitsNeeded: Int,
  @Json(name = "hospital") val hospital: String,
  @Json(name = "city") val city: String,
  @Json(name = "urgency") val urgency: String,
  @Json(name = "contact") val contact: String,
  @Json(name = "status") val status: String
)

@JsonClass(generateAdapter = true)
data class EducationInfo(
  @Json(name = "eligibilityRule") val eligibilityRule: String,
  @Json(name = "mythBuster") val mythBuster: String
)
