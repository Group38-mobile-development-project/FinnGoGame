
package com.example.gamestore.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.gamestore.ui.theme.ForSearchBarBackgroundColor
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
// File: GlobalSearchBarWithFilters.kt
fun GlobalSearchBarWithFilters(
    query: String,
    onQueryChange: (String) -> Unit,
    genres: List<Pair<String, String>>,
    platforms: List<Pair<String, String>>,
    selectedGenre: String?,
    onGenreSelected: (String?) -> Unit,
    selectedPlatform: String?,
    onPlatformSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    onSearchDone: () -> Unit = {},
    autoFocus: Boolean = true
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            delay(300)
            focusRequester.requestFocus()
        }
    }

    Column(modifier = modifier.padding(16.dp)) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("Search games...") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(2.dp, RoundedCornerShape(25.dp), clip = true)
                .background(ForSearchBarBackgroundColor, RoundedCornerShape(25.dp))
                .focusRequester(focusRequester),
            singleLine = true,
            shape = RoundedCornerShape(25.dp),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                    onSearchDone()
                }
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            FilterDropdown(
                label = "Genre",
                items = genres,
                selectedItem = selectedGenre,
                onItemSelected = onGenreSelected
            )

            FilterDropdown(
                label = "Platform",
                items = platforms,
                selectedItem = selectedPlatform,
                onItemSelected = onPlatformSelected
            )
        }
    }
}

@Composable
fun FilterDropdown(
    label: String,
    items: List<Pair<String, String>>,
    selectedItem: String?,
    onItemSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedDisplay = items.find { it.first == selectedItem }?.second ?: label

    Box(modifier = modifier) {
        OutlinedButton(onClick = { expanded = true }) {
            Text(selectedDisplay)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 300.dp)
        ) {
            DropdownMenuItem(
                text = { Text("All") },
                onClick = {
                    onItemSelected(null)
                    expanded = false
                }
            )
            items.forEach { (value, displayText) ->
                DropdownMenuItem(
                    text = { Text(displayText) },
                    onClick = {
                        onItemSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}
