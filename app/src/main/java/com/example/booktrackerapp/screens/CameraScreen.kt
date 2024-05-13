import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

@Composable
fun CameraScreen(navController: NavController, imageViewModel: ImageViewModel){

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraController = remember { LifecycleCameraController(context)}

    val lensFacing = remember { mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ){

        //Camera Preview
        AndroidView(
            modifier = Modifier,
            factory = { context ->
                PreviewView(context).apply{
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    setBackgroundColor(Color.BLACK)
                    scaleType = PreviewView.ScaleType.FILL_START
                }.also {previewView ->
                    previewView.controller = cameraController
                    cameraController.bindToLifecycle(lifecycleOwner)
                }
            })

        //Camera Button-Funktionen
        CameraControls(
            modifier = Modifier.align(Alignment.BottomCenter),
            context = context,
            lifecycleOwner = lifecycleOwner,
            cameraController = cameraController,
            imageViewModel = imageViewModel,

            onTakePicture = {
                val mainExecutor = ContextCompat.getMainExecutor(context)
                cameraController.takePicture(mainExecutor, object : ImageCapture.OnImageCapturedCallback(){
                    override fun onCaptureSuccess(image: ImageProxy) {
                        val uri = bitmapToUriConverter(image.toBitmap(), context)
                        imageViewModel.setImageUri(uri)
                        navController.navigate("previewScreen")
                    }
                })
            },

            onSwitchCamera = {
                lensFacing.value = if (lensFacing.value == CameraSelector.DEFAULT_BACK_CAMERA) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }
                cameraController.cameraSelector = lensFacing.value
            },

            onCancelPreview = {
                navController.navigate("homescreen")
            },

            onOpenGallery = {
                // TODO
            }
        )
    }
}


//ICONS
@Composable
fun CameraControls(
    modifier: Modifier = Modifier,
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraController: LifecycleCameraController,
    imageViewModel: ImageViewModel,
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
                tint = androidx.compose.ui.graphics.Color.White
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
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = {
                onTakePicture()
            }, modifier = Modifier.size(60.dp)) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Take Picture",
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onSwitchCamera, modifier = Modifier.size(60.dp)) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Switch Camera",
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    }
}

fun bitmapToUriConverter(bitmap: Bitmap, context: Context): Uri {
    val imagesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    val image = File(imagesDir, "${UUID.randomUUID()}.jpg")

    try {
        val stream: OutputStream = FileOutputStream(image)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.flush()
        stream.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return Uri.fromFile(image)
}

