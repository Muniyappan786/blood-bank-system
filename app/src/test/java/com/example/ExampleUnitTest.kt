package com.example

import com.example.data.engine.BloodCompatibility
import com.example.data.engine.LifeLinkDataCoreEngine
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

  private val engine = LifeLinkDataCoreEngine()

  @Test
  fun testFallbackMechanism_withEmptyOrInvalidInput() {
    val response = engine.query(rawBloodGroup = "", rawLocation = "hello", rawComponent = null)
    assertEquals("success", response.searchStatus)
    assertEquals("O+", response.queryParameters.bloodGroup)
    assertEquals("Chennai", response.queryParameters.location)
    assertEquals("Whole Blood", response.queryParameters.component)
    assertTrue(response.bloodBanks.isNotEmpty())
    assertTrue(response.emergencyDonors.isNotEmpty())
  }

  @Test
  fun testMedicalBloodCompatibility_universalDonorAndRecipient() {
    // Universal donor O- matches all 8 groups
    val oMinusRecipients = BloodCompatibility.getCompatibleRecipientGroups("O-")
    assertEquals(8, oMinusRecipients.size)
    assertTrue(oMinusRecipients.contains("AB+"))
    assertTrue(oMinusRecipients.contains("O+"))

    // Universal recipient AB+ receives from all 8 groups
    val abPlusDonors = BloodCompatibility.getCompatibleDonorGroups("AB+")
    assertEquals(8, abPlusDonors.size)
    assertTrue(abPlusDonors.contains("O-"))
    assertTrue(abPlusDonors.contains("B+"))

    // O- recipient can only receive from O-
    val oMinusDonors = BloodCompatibility.getCompatibleDonorGroups("O-")
    assertEquals(listOf("O-"), oMinusDonors)
  }

  @Test
  fun testDonorEligibilityLogic_90DayRule() {
    val response = engine.query("O+", "Chennai", "Whole Blood")
    response.emergencyDonors.forEach { donor ->
      if (donor.daysSinceLastDonation < 90) {
        assertEquals("Ineligible", donor.eligibilityStatus)
      } else {
        assertEquals("Eligible", donor.eligibilityStatus)
      }
    }
  }

  @Test
  fun testPrivacyArchitecture_hiddenContactTokens() {
    val response = engine.query("O+", "Chennai", "Whole Blood")
    response.emergencyDonors.forEach { donor ->
      assertEquals("REVEAL_VIA_CONSENT_TRIGGER", donor.hiddenContactToken)
      assertTrue(donor.privacyConsent)
    }
  }

  @Test
  fun testJsonOutputGeneration() {
    val response = engine.query("O+", "Chennai", "Whole Blood")
    val json = engine.toJson(response)
    assertNotNull(json)
    assertTrue(json.contains("\"searchStatus\": \"success\""))
    assertTrue(json.contains("\"bloodGroup\": \"O+\""))
    assertTrue(json.contains("\"location\": \"Chennai\""))
    assertTrue(json.contains("\"hiddenContactToken\": \"REVEAL_VIA_CONSENT_TRIGGER\""))
    assertTrue(json.contains("\"education\""))
  }
}
