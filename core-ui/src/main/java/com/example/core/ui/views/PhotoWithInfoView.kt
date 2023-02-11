package com.example.core.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.core.ui.R
import com.example.core.ui.theme.WasimTheme

@Composable
fun PhotoWithInfoView(
    userName: String,
    tags: String,
    imageUrl: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Box(modifier = modifier
        .fillMaxSize()
        .testTag(stringResource(id = R.string.photo_text_view))
    ) {
        ImageView(
            url = imageUrl,
            contentScale = ContentScale.FillWidth,
            modifier = modifier
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .background(color = WasimTheme.color.background)
        ) {
            Text(
                text = userName,
                style = WasimTheme.typography.body2,
                color = WasimTheme.color.grey200,
                modifier = Modifier.padding(horizontal = WasimTheme.spacing.spacing4),
            )
            Text(
                text = tags,
                style = WasimTheme.typography.caption,
                color = WasimTheme.color.teal200,
                modifier = Modifier.padding(horizontal = WasimTheme.spacing.spacing4),
            )
            content()
        }
    }
}

@Preview
@Composable
private fun ImageTextPreview() {
    WasimTheme {
        Column(modifier = Modifier.fillMaxSize()) {
            PhotoWithInfoView(
                userName = "userName",
                tags = "tags",
                modifier = Modifier,
                imageUrl = "imageUrl"
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "inner Text",
                        style = WasimTheme.typography.caption,
                        color = WasimTheme.color.teal200
                    )
                }
            }
        }
    }
}
