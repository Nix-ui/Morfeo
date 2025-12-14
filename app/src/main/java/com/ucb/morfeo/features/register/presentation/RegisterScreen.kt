package com.ucb.morfeo.features.register.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ucb.morfeo.R
import com.ucb.morfeo.navigation.Screen
import org.koin.androidx.compose.koinViewModel

@Composable
fun RegisterScreen(
    onNavigateRoute: (String) -> Unit = {},
    registerViewModel: RegisterViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(colorResource(R.color.firefly))
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors().copy(containerColor = colorResource(R.color.cloud_burst), contentColor = colorResource(R.color.dodger_blue))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var nombre by remember { mutableStateOf("") }
                    var email by remember { mutableStateOf("") }
                    var password by remember { mutableStateOf("") }
                    var edad by remember { mutableStateOf("") }
                    var peso by remember { mutableStateOf("") }
                    var altura by remember { mutableStateOf("") }
                    var togglePasswordVisibility by remember { mutableStateOf(false) }
                    val registrationState by registerViewModel.registrationState.collectAsState()

                    Text(
                        text = stringResource(id = R.string.register_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text(text = stringResource(id = R.string.register_name_label)) },
                        placeholder = { Text(text = stringResource(id = R.string.register_name_placeholder)) },
                        maxLines = 1,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.width(300.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(text = stringResource(id = R.string.register_email_label)) },
                        placeholder = { Text(text = stringResource(id = R.string.register_email_placeholder)) },
                        maxLines = 1,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.width(300.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    OutlinedTextField(
                        value = edad,
                        onValueChange = { edad = it },
                        label = { Text(text = stringResource(id = R.string.register_age_label)) },
                        placeholder = { Text(text = stringResource(id = R.string.register_age_placeholder)) },
                        maxLines = 1,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.width(300.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    OutlinedTextField(
                        value = peso,
                        onValueChange = { peso = it },
                        label = { Text(text = stringResource(id = R.string.register_weight_label)) },
                        placeholder = { Text(text = stringResource(id = R.string.register_weight_placeholder)) },
                        maxLines = 1,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.width(300.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    OutlinedTextField(
                        value = altura,
                        onValueChange = { altura = it },
                        label = { Text(text = stringResource(id = R.string.register_height_label)) },
                        placeholder = { Text(text = stringResource(id = R.string.register_height_placeholder)) },
                        maxLines = 1,
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.width(300.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Number
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(text = stringResource(id = R.string.register_password_label)) },
                        placeholder = { Text(text = stringResource(id = R.string.register_password_placeholder)) },
                        maxLines = 1,
                        visualTransformation = if (togglePasswordVisibility) VisualTransformation.None else PasswordVisualTransformation('*'),
                        shape = MaterialTheme.shapes.large,
                        trailingIcon = {
                            IconButton(
                                onClick = { togglePasswordVisibility = !togglePasswordVisibility }
                            ) {
                                Icon(
                                    imageVector = if (togglePasswordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                                    contentDescription = stringResource(id = R.string.register_toggle_password_visibility)
                                )
                            }
                        },
                        modifier = Modifier.width(300.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                registerViewModel.register(email, password, nombre, edad, peso, altura)
                            }
                        )
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = stringResource(id = R.string.register_already_have_account))
                        TextButton(
                            onClick = { onNavigateRoute(Screen.LogIn.route) }
                        ) {
                            Text(text = stringResource(id = R.string.register_login_button))
                        }
                    }

                    Button(
                        onClick = {
                            registerViewModel.register(email, password, nombre, edad, peso, altura)
                        }
                    ) {
                        Text(text = stringResource(id = R.string.register_button))
                    }

                    when (val state = registrationState) {
                        is RegistrationState.Error -> {
                            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                        }
                        is RegistrationState.Success -> {
                            Toast.makeText(context, stringResource(id = R.string.register_success_message), Toast.LENGTH_LONG).show()
                            onNavigateRoute(Screen.Home.route)
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun RegisterScreenPreview() {
    RegisterScreen()
}
