package com.example.booktrackerapp.widgets

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
@Composable
fun CameraInstructions(isBook: Boolean){
    
    if (isBook){
        Box(
            modifier = Modifier
                .width(350.dp)
                .height(80.dp)// Add padding to adjust the frame size as needed
                .border(4.dp, Color.White)  // Set border thickness and color
        ){
            Text(text = "Please fit the ISBN inside the border", modifier = Modifier.align(Alignment.TopCenter))
        }
    } else {
        Box(
            modifier = Modifier
                .width(350.dp)
                .height(600.dp)// Add padding to adjust the frame size as needed
                .border(4.dp, Color.White)  // Set border thickness and color
        ){
            Text(text = "Please place your book inside the border", modifier = Modifier.align(Alignment.TopCenter))
        }
    }

}

@Composable
fun CameraControls(
    modifier: Modifier = Modifier,
    onTakePicture: () -> Unit,
    onSwitchCamera: () -> Unit,
    onCancelPreview: () -> Unit,
    onOpenGallery: () -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        IconButton(onClick = onCancelPreview, modifier = Modifier.align(Alignment.TopStart)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cancel Preview",
                tint = Color.White
            )
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = onOpenGallery , modifier = Modifier.size(60.dp)) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = "Switch Camera",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = {
                onTakePicture()
            }, modifier = Modifier.size(60.dp)) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Take Picture",
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onSwitchCamera, modifier = Modifier.size(60.dp)) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Switch Camera",
                    tint = Color.White
                )
            }
        }
    }
}