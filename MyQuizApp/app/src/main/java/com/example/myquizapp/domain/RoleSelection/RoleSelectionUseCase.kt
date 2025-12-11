package com.example.myquizapp.domain.RoleSelection

import javax.inject.Inject

/**
 * RoleSelectionUseCase.kt - Use case for role selection operations
 */
class RoleSelectionUseCase @Inject constructor() {

    fun selectRole(role: UserRole): UserRole {
        return role
    }
}
