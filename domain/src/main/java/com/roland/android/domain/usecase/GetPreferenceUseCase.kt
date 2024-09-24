package com.roland.android.domain.usecase

import com.roland.android.domain.model.Preference
import com.roland.android.domain.repository.MusicUtilRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetPreferenceUseCase : KoinComponent {
	private val musicUtilRepository: MusicUtilRepository by inject()

	operator fun invoke(): Flow<Preference> = combine(
		musicUtilRepository.getSortOption(),
		musicUtilRepository.getPermissionStatus()
	) { sortOption, permissionStatus ->
		Preference(
			sortOption = sortOption,
			permissionStatus = permissionStatus
		)
	}
}