package com.example.data.engine

object BloodCompatibility {
  val ALL_BLOOD_GROUPS = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")

  /**
   * Returns list of blood groups that can safely donate whole blood / red blood cells
   * to a recipient of the given blood group.
   *
   * Medical rule:
   * - O- is universal donor (matches all blood groups).
   * - AB+ is universal recipient (receives from all blood groups).
   */
  fun getCompatibleDonorGroups(recipientGroup: String): List<String> {
    return when (recipientGroup.uppercase().trim()) {
      "O-" -> listOf("O-")
      "O+" -> listOf("O+", "O-")
      "A-" -> listOf("A-", "O-")
      "A+" -> listOf("A+", "A-", "O+", "O-")
      "B-" -> listOf("B-", "O-")
      "B+" -> listOf("B+", "B-", "O+", "O-")
      "AB-" -> listOf("AB-", "A-", "B-", "O-")
      "AB+" -> listOf("AB+", "AB-", "A+", "A-", "B+", "B-", "O+", "O-")
      else -> listOf(recipientGroup)
    }
  }

  /**
   * Returns list of recipient blood groups that a donor of the given group can safely donate to.
   */
  fun getCompatibleRecipientGroups(donorGroup: String): List<String> {
    return when (donorGroup.uppercase().trim()) {
      "O-" -> listOf("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+")
      "O+" -> listOf("O+", "A+", "B+", "AB+")
      "A-" -> listOf("A-", "A+", "AB-", "AB+")
      "A+" -> listOf("A+", "AB+")
      "B-" -> listOf("B-", "B+", "AB-", "AB+")
      "B+" -> listOf("B+", "AB+")
      "AB-" -> listOf("AB-", "AB+")
      "AB+" -> listOf("AB+")
      else -> listOf(donorGroup)
    }
  }

  fun isCompatibleDonor(donorGroup: String, recipientGroup: String): Boolean {
    val compatible = getCompatibleDonorGroups(recipientGroup)
    return compatible.contains(donorGroup.uppercase().trim())
  }

  fun getCompatibilityBadge(donorGroup: String, targetGroup: String): String {
    val d = donorGroup.uppercase().trim()
    val t = targetGroup.uppercase().trim()
    return when {
      d == t -> "Exact Match"
      d == "O-" -> "Universal Donor (O-)"
      t == "AB+" -> "Compatible with AB+"
      isCompatibleDonor(d, t) -> "Compatible Group"
      else -> "Non-Compatible"
    }
  }

  fun getPlasmaDonorCompatibility(recipientGroup: String): List<String> {
    // For Plasma, AB is the universal donor, and O is the universal recipient!
    return when (recipientGroup.uppercase().trim()) {
      "AB+", "AB-" -> listOf("AB+", "AB-")
      "A+", "A-" -> listOf("A+", "A-", "AB+", "AB-")
      "B+", "B-" -> listOf("B+", "B-", "AB+", "AB-")
      "O+", "O-" -> listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")
      else -> listOf(recipientGroup)
    }
  }
}
