package com.nyaayneeti.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nyaayneeti.app.AppContainer

class NyaayVMFactory(private val c: AppContainer) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(AuthViewModel::class.java) -> AuthViewModel(c.authRepository) as T
        modelClass.isAssignableFrom(OnboardingViewModel::class.java) -> OnboardingViewModel(c.authRepository, c.firmRepository) as T
        modelClass.isAssignableFrom(CaseViewModel::class.java) -> CaseViewModel(c.caseRepository, c.authRepository) as T
        modelClass.isAssignableFrom(AIIntakeViewModel::class.java) -> AIIntakeViewModel(c.intakeEngine, c.caseRepository, c.authRepository) as T
        modelClass.isAssignableFrom(ChatViewModel::class.java) -> ChatViewModel(c.chatRepository, c.authRepository, c.database.userDao()) as T
        modelClass.isAssignableFrom(FirmViewModel::class.java) -> FirmViewModel(c.firmRepository, c.caseRepository, c.authRepository) as T
        modelClass.isAssignableFrom(InternshipViewModel::class.java) -> InternshipViewModel(c.internshipRepository, c.authRepository) as T
        modelClass.isAssignableFrom(ForumViewModel::class.java) -> ForumViewModel(c.forumRepository, c.authRepository) as T
        modelClass.isAssignableFrom(ConsultationViewModel::class.java) -> ConsultationViewModel(c.consultationRepository, c.authRepository) as T
        modelClass.isAssignableFrom(NotificationViewModel::class.java) -> NotificationViewModel(c.notifications, c.authRepository) as T
        modelClass.isAssignableFrom(BillingViewModel::class.java) -> BillingViewModel(c.billingRepository, c.authRepository) as T
        modelClass.isAssignableFrom(InquiryViewModel::class.java) -> InquiryViewModel(c.inquiryRepository, c.authRepository) as T
        modelClass.isAssignableFrom(VerificationViewModel::class.java) -> VerificationViewModel(c.verificationRepository, c.authRepository) as T
        modelClass.isAssignableFrom(ModerationViewModel::class.java) -> ModerationViewModel(c.moderationRepository) as T
        modelClass.isAssignableFrom(CaseAIViewModel::class.java) -> CaseAIViewModel(c.caseAIClient) as T
        modelClass.isAssignableFrom(ECourtsViewModel::class.java) -> ECourtsViewModel(c.ecourtsClient) as T
        else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
