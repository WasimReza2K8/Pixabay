package com.example.core.ui.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest

@Composable
fun ImageView(
    url: String,
    contentScale: ContentScale,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(url)
                .allowHardware(false)
                .build(),
            error = ColorPainter(Color.LightGray),
        ),
        contentScale = contentScale,
        contentDescription = "PhotoListItem",
        modifier = modifier.fillMaxSize()
    )
}
