package com.roland.android.domain.usecase

import com.roland.android.domain.model.SearchData
import com.roland.android.domain.repository.SearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class GetSearchDataUseCase : KoinComponent {
	private val searchRepository: SearchRepository by inject()

	operator fun invoke(searchQuery: String): Flow<SearchData> = combine(
		searchRepository.getSongsFromSearch(searchQuery),
		searchRepository.getSearchHistory()
	) { searchResult, searchHistory ->
		SearchData(
			searchResult = searchResult, // in the implementation, return empty result if query is empty
			searchHistory = searchHistory
		)
	}
}