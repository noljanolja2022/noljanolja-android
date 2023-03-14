package com.noljanolja.android.features.auth.countries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import com.noljanolja.android.R
import com.noljanolja.core.country.domain.model.Countries
import com.noljanolja.core.country.domain.model.Country
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.SearchBar

@Composable
fun CountriesScreen(
    viewModel: CountriesViewModel = getViewModel(),
) {
    CountriesScreenContent(
        viewModel::handleEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountriesScreenContent(
    event: (CountriesEvent) -> Unit,
) {
    var searchText by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                CommonTopAppBar(
                    title = stringResource(id = R.string.countries_title),
                )
            }
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(it)
            ) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    searchText = searchText,
                    hint = stringResource(R.string.countries_search),
                    onSearch = { searchText = it }
                )
                CountryList(
                    Countries.filter {
                        with(searchText.trim()) {
                            it.name.contains(this, true) ||
                                it.nameCode.contains(this, true) ||
                                it.phoneCode.contains(this, true)
                        }
                    },
                    onItemClick = {
                        event(CountriesEvent.SelectCountry(it.nameCode))
                    }
                )
            }
        }
    }
}

@Composable
private fun CountryList(
    countries: List<Country>,
    onItemClick: (Country) -> Unit,
) {
    LazyColumn {
        items(countries) { country ->
            CountryRow(Modifier.padding(horizontal = 24.dp), country) { onItemClick(it) }
            Divider(Modifier.padding(horizontal = 24.dp))
        }
    }
}

@Composable
private fun CountryRow(
    modifier: Modifier,
    country: Country,
    onClick: (Country) -> Unit,
) {
    Text(
        country.name,
        modifier = modifier.fillMaxWidth().clickable { onClick(country) }.padding(vertical = 12.dp),
        style = MaterialTheme.typography.bodyLarge,
    )
}