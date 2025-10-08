package com.ucb.morfeo.features.login.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.ucb.morfeo.R
import com.ucb.morfeo.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    onNavigateRoute: (String) -> Unit = {},
    logInViewModel: LogInViewModel = koinViewModel()
) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) {innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.firefly)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors().copy(colorResource(R.color.cloud_burst),contentColor = colorResource(R.color.dodger_blue))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var togglePasswordVisibility by remember { mutableStateOf(false) }
                    var loginState = logInViewModel.logInState.collectAsState()
                    Text(
                        text = stringResource(R.string.log_in_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = "Email") },
                        shape = MaterialTheme.shapes.large,
                        colors = if(email.isEmpty()){
                            OutlinedTextFieldDefaults.colors().copy(
                                focusedIndicatorColor = colorResource(R.color.dodger_blue),
                                unfocusedIndicatorColor = Color.Red)
                            }else{
                                OutlinedTextFieldDefaults.colors().copy(
                                focusedIndicatorColor = colorResource(R.color.dodger_blue),
                                unfocusedIndicatorColor = colorResource(R.color.dodger_blue))
                            }
                    )
                    if(email.isEmpty()){
                        Text(
                            text = "Email is required",
                            color = Color.Red,
                            modifier = Modifier.padding(start = 16.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }else{
                        if(!email.contains("@")){
                            Text(
                                text = "Email is not valid",
                                color = Color.Red,
                                modifier = Modifier.padding(start = 16.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = "Password") },
                        visualTransformation =if(togglePasswordVisibility) VisualTransformation.None else PasswordVisualTransformation('*'),
                        shape = MaterialTheme.shapes.large,
                        trailingIcon = {
                            IconButton(
                                onClick = { togglePasswordVisibility = !togglePasswordVisibility }
                            ) {
                                Icon(
                                    imageVector = if(togglePasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        }
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "¿No tienes una cuenta?")
                        TextButton(
                            onClick = { onNavigateRoute(Screen.SignUp.route) }
                        ) {
                            Text(text = "Registrate")
                        }
                    }
                    Button(
                        onClick = {
                            logInViewModel.logIn(email, password)
                        }
                    ) {
                        Text(text = "Iniciar sesión")
                    }
                    when(val state = loginState.value){
                        is LogInViewModel.LogInUIState.Error -> {
                            Text(
                                text = state.message,
                                color = Color.Red,
                                modifier = Modifier.padding(start = 16.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        is LogInViewModel.LogInUIState.Success -> {
                            Toast.makeText(context,"Bienvenido ${state.userModel.nombre}",Toast.LENGTH_LONG).show()
                            onNavigateRoute(Screen.Home.route)
                        }
                        else -> {

                        }

                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun LoginScreenPreview() {
    LoginScreen()
}