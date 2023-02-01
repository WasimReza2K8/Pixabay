package com.jet.feature.search.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.core.ui.theme.JetTheme
import com.example.core.ui.views.PhotoWithInfoView
import com.jet.feature.search.R
import com.jet.search.presentation.model.PhotoUiModel

@Composable
fun PhotoListItem(
    photo: PhotoUiModel,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier
        .fillMaxWidth()
        .height(200.dp)
        .padding(JetTheme.spacing.spacing8)
        .clickable {
            onClick(photo.localId)
        }
        .testTag(stringResource(id = R.string.search_item))
    ) {
        PhotoWithInfoView(
            userName = photo.userName,
            tags = photo.tags,
            imageUrl = photo.previewURL
        )
    }
}

@Preview
@Composable
private fun PhotoItemPreview() {
    val photo = PhotoUiModel(
        localId = "as_283",
        id = 8734,
        comments = 7,
        downloads = 5,
        largeImageURL = "test",
        previewURL = "https://cdn.pixabay.com/photo/2013/10/15/09/12/flower-195893_150.jpg",
        likes = 2,
        tags = "test, test, test",
        userName = "test123",
        userId = 123,
    )
    Column {
        PhotoListItem(
            photo = photo,
            onClick = {}
        )
    }
}
