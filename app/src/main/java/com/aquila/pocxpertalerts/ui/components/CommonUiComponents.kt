package com.aquila.pocxpertalerts.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aquila.pocxpertalerts.ui.theme.PlaceholderColor
import com.aquila.pocxpertalerts.ui.theme.SearchBorderColor
import com.aquila.pocxpertalerts.ui.theme.SearchDropdownDividerColor
import com.aquila.pocxpertalerts.ui.theme.SearchDropdownSelectedBackground
import com.aquila.pocxpertalerts.ui.theme.SearchFieldBackground
import com.aquila.pocxpertalerts.ui.theme.SearchSelectedBorderColor
import com.aquila.pocxpertalerts.ui.theme.SearchTextColor
import com.aquila.pocxpertalerts.ui.theme.SearchToolbarColor
import com.aquila.pocxpertalerts.ui.theme.TextDark
import com.aquila.pocxpertalerts.ui.theme.ToolbarTitleColor
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.TextButton
import com.aquila.pocxpertalerts.ui.theme.ToolbarBackground
import com.aquila.pocxpertalerts.ui.theme.ToolbarIconColor
import com.aquila.pocxpertalerts.ui.theme.SearchIconColor

// ============================================================
// SHARED SEARCH SCREEN TOOLBAR
// ============================================================



@Composable
fun AppTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onResetClick: (() -> Unit)? = null,
    trailingActionText: String? = null,
    onTrailingActionClick: (() -> Unit)? = null,
    onMoreClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ToolbarBackground)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left action
            Box(
                modifier = Modifier.width(56.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ToolbarIconColor
                    )
                }
            }

            // Center title
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = ToolbarTitleColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }


            // Right action
            Box(
                modifier = Modifier.width(56.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                when {
                    onMoreClick != null -> {
                        IconButton(onClick = onMoreClick) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "More options",
                                tint = ToolbarIconColor
                            )
                        }
                    }

                    trailingActionText != null &&
                            onTrailingActionClick != null -> {
                        Text(
                            text = trailingActionText,
                            modifier = Modifier
                                .clickable(
                                    onClick = onTrailingActionClick
                                )
                                .padding(
                                    horizontal = 2.dp,
                                    vertical = 12.dp
                                ),
                            color = ToolbarTitleColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    onResetClick != null -> {
                        Text(
                            text = "Reset",
                            modifier = Modifier
                                .clickable(onClick = onResetClick)
                                .padding(
                                    horizontal = 2.dp,
                                    vertical = 12.dp
                                ),
                            color = ToolbarTitleColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    else -> {
                        Spacer(
                            modifier = Modifier.width(44.dp)
                        )
                    }
                }
            }

        }
    }
}


@Composable
fun SearchScreenTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    onResetClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ToolbarBackground)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Fixed left slot.
            Box(
                modifier = Modifier.width(56.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = ToolbarIconColor
                    )
                }
            }

            // Centered title.
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = ToolbarTitleColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Fixed right slot keeps the title centered.
            Box(
                modifier = Modifier.width(56.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                if (onResetClick != null) {
                    Text(
                        text = "Reset",
                        modifier = Modifier
                            .clickable(onClick = onResetClick)
                            .padding(
                                horizontal = 2.dp,
                                vertical = 12.dp
                            ),
                        color = ToolbarTitleColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}


@Composable
fun AppListSearchBar(
    placeholder: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = SearchFieldBackground,
        border = BorderStroke(
            width = 1.dp,
            color = SearchBorderColor
        )

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = placeholder,
                modifier = Modifier.weight(1f),
                color = PlaceholderColor,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = placeholder,
                tint = SearchIconColor,
                modifier = Modifier.size(19.dp)
            )
        }
    }
}


// ============================================================
// SHARED FORM LABEL
// ============================================================

@Composable
fun AppFieldLabel(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier.padding(bottom = 6.dp),
        color = SearchTextColor,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )
}

// ============================================================
// SHARED TEXT FIELD
// ============================================================

@Composable
fun AppSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    label: String? = null,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        readOnly = readOnly,
        label = label?.let {
            { Text(text = it, fontSize = 13.sp) }
        },
        placeholder = {
            Text(
                text = placeholder,
                color = PlaceholderColor,
                fontSize = 14.sp
            )
        },
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = SearchSelectedBorderColor,
            unfocusedBorderColor = SearchBorderColor,
            focusedLabelColor = SearchSelectedBorderColor,
            unfocusedLabelColor = PlaceholderColor,
            cursorColor = SearchToolbarColor,
            focusedTextColor = SearchTextColor,
            unfocusedTextColor = SearchTextColor,
            focusedContainerColor = SearchFieldBackground,
            unfocusedContainerColor = SearchFieldBackground,
            disabledContainerColor = SearchFieldBackground,
            disabledTextColor = SearchTextColor,
            disabledBorderColor = SearchBorderColor
        )
    )
}
@Composable
fun AppScreenHeading(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = TextDark,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold
    )
}
// ============================================================
// SHARED INTERACTIVE DROPDOWN
// ============================================================




@Composable
fun AppSearchDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onValueSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Select $label",
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        AppFieldLabel(text = label)

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = enabled) {
                        expanded = true
                    },
                readOnly = true,
                enabled = enabled,
                singleLine = true,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = PlaceholderColor,
                        fontSize = 14.sp
                    )
                },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Show $label options",
                        tint = SearchTextColor
                    )
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SearchSelectedBorderColor,
                    unfocusedBorderColor = SearchBorderColor,
                    focusedTextColor = SearchTextColor,
                    unfocusedTextColor = SearchTextColor,
                    focusedContainerColor = SearchFieldBackground,
                    unfocusedContainerColor = SearchFieldBackground,
                    disabledContainerColor = SearchFieldBackground,
                    disabledTextColor = SearchTextColor,
                    disabledBorderColor = SearchBorderColor
                )
            )

            // Transparent click target over the complete field.
            // The dropdown icon remains decorative.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(enabled = enabled) {
                        expanded = true
                    }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 280.dp)
            ) {
                val uniqueOptions = options.distinct()

                if (uniqueOptions.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "No options available",
                                color = PlaceholderColor
                            )
                        },
                        onClick = { expanded = false },
                        enabled = false
                    )
                } else {
                    uniqueOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    color = SearchTextColor
                                )
                            },
                            onClick = {
                                onValueSelected(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}
