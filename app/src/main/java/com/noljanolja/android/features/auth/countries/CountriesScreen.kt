package com.noljanolja.android.features.auth.countries

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.noljanolja.android.R
import com.noljanolja.android.common.country.Countries
import com.noljanolja.android.common.country.Country
import com.noljanolja.android.ui.composable.CommonTopAppBar
import com.noljanolja.android.ui.composable.SearchBar
import org.koin.androidx.compose.getViewModel

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

    Surface(
        modifier = Modifier.fillMaxSize()
            .background(MaterialTheme.colorScheme.primary).padding(top = 50.dp)
    ) {
        Scaffold(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clip(
                    RoundedCornerShape(
                        topEnd = 15.dp,
                        topStart = 15.dp
                    )
                ).fillMaxSize(),
            topBar = {
                CommonTopAppBar(
                    title = stringResource(id = R.string.countries_title),
                    centeredTitle = true
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
    val name = country.nameId?.let { stringResource(id = it) } ?: country.name
    Text(
        text = name,
        modifier = modifier.fillMaxWidth().clickable { onClick(country) }.padding(vertical = 12.dp),
        style = MaterialTheme.typography.bodyLarge,
    )
}