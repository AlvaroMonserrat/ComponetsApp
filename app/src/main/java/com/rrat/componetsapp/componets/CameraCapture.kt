package com.rrat.componetsapp.componets

import android.Manifest
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.*
import com.rrat.componetsapp.BuildConfig
import java.io.File


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraCapture(
    onImageTaken: (Uri)->Unit,
){

    val context = LocalContext.current
    val permissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)
    //var askPermissions by remember { mutableStateOf(false) }

    var imageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success){
                imageUri?.let { onImageTaken(it) }
            }
        }
    )

    Button(onClick = {
        //CHECK PERMISSIONS
        permissionController(
            permissionState,
            onGranted = {
                Log.i("CAMERA_PERMISSIONS", "GRANTED")
                val uri = getTmpFileUri(context)
                imageUri = uri
                cameraLauncher.launch(uri)
            },
            onDenied = {
                Log.i("CAMERA_PERMISSIONS", "DENIED")
                if (permissionState.status.shouldShowRationale){
                    Log.i("CAMERA_PERMISSIONS", "SHOULD SHOW RATIONALE")
                }else{
                    Log.i("CAMERA_PERMISSIONS", "NOT SHOW RATIONALE")
                    if(permissionState.status is PermissionStatus.Denied){
                        Log.i("CAMERA_PERMISSIONS", "DENIED DEFINED")
                    }
                }
                permissionState.launchPermissionRequest()
            }
        )

    }) {
        Text(text = "Take Picture")
    }

/*    if (askPermissions){
        if(permissionState.status is PermissionStatus.Denied){
            Rationale(
                text = "This permissions is important for this app. Please grant the permissions",
                onRequestPermissions = { permissionState.launchPermissionRequest()}
            )
        }
    }*/

/*    Permission(
        permission = Manifest.permission.CAMERA,
        rationale = "You said you wanted a picture, so I'm going to have to ask for permission.",
        permissionsNotAvailableContent = {
            Column() {
                Text(text = "No Camera!")
            }
        }
    )*/

}


@OptIn(ExperimentalPermissionsApi::class)
fun permissionController(
    permissionState: PermissionState,
    onGranted: ()->Unit,
    onDenied: ()->Unit
){
    when(permissionState.status){
        PermissionStatus.Granted -> onGranted()
        is PermissionStatus.Denied -> onDenied()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Permission(
        permission: String = Manifest.permission.CAMERA,
        rationale: String = "This permissions is important for this app. Please grant the permissions",
        permissionsNotAvailableContent: @Composable ()->Unit = {},
        content: @Composable ()->Unit = {}
){
    val permissionState = rememberPermissionState(permission = permission)
    when(permissionState.status){
        PermissionStatus.Granted -> content()
        is PermissionStatus.Denied -> {
                if (permissionState.status.shouldShowRationale){
                    Rationale(
                        text = rationale,
                        onRequestPermissions = { permissionState.launchPermissionRequest()}
                    )
                }else{
                    permissionsNotAvailableContent()
                }
        }
    }
}

@Composable
fun Rationale(
    text: String,
    onRequestPermissions: ()->Unit
){
    AlertDialog(
        onDismissRequest = { /*TODO*/ },
        title = { Text(text = "Permission request")},
        text = { Text(text = text)},
        confirmButton = { Button(onClick = onRequestPermissions) {
            Text(text = "OK")
        }}
    )
}

private fun getTmpFileUri(context: Context): Uri {
    val tmpFile = File.createTempFile("tmp_image_file", ".jpg", context.filesDir).apply {
        createNewFile()
        deleteOnExit()
    }
    return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", tmpFile)
}