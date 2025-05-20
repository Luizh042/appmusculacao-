package com.example.appmusculacao

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.appmusculacao.ui.theme.AppmusculacaoTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "register") {
                composable("register") {
                    RegisterScreen(
                        onRegisterClick = { name, email, password ->
                            // Aqui pode fazer o cadastro e redirecionar
                            Log.d("Register", "Nome: $name, Email: $email")
                            navController.navigate("login")
                        },
                        onLoginClick = {
                            navController.navigate("login")
                        }
                    )
                }
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = {
                            // Redirecionar para próxima tela após login
                            Log.d("Login", "Login realizado com sucesso")
                        },
                        onBackToRegister = {
                            navController.navigate("register") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }
                composable("workout") {
                    WorkoutScreen()
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterClick: (String, String, String) -> Unit,
    onLoginClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Criar Conta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-mail") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                    onRegisterClick(name, email, password)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Criar Conta")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onLoginClick) {
            Text("Já tenho uma conta")
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onBackToRegister: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        when (currentStep) {
            1 -> {
                Text("Digite seu e-mail", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("E-mail") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            currentStep = 2
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continuar")
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = { onBackToRegister() }) {
                    Text("Ainda não tem uma conta? Cadastre-se")
                }
            }

            2 -> {
                Text("Digite sua senha", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Senha") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (password.isNotBlank()) {
                            onLoginSuccess()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Entrar")
                }

                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = {
                    currentStep = 1
                    password = ""
                }) {
                    Text("Voltar")
                }
            }
        }
    }
}

data class Exercise(
    val name: String,
    val repetitions: Int,
    val series: Int,
    val intervalSeconds: Int,
    var paid: Boolean = false
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutScreen() {
    val dateNow = remember { LocalDate.now() }
    val dayOfWeek = dateNow.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
    val formattedDate = dateNow.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")))

    val exercises = remember {
        mutableStateListOf(
            Exercise("Supino Reto", 12, 4, 60),
            Exercise("Agachamento", 12, 3, 90),
            Exercise("Rosca Direta", 15, 4, 45),
            Exercise("Puxada Alta", 12, 4, 60),
            Exercise("Remada Curvada", 10, 4, 60),
            Exercise("Desenvolvimento", 12, 4, 60)
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Exercícios", style = MaterialTheme.typography.headlineSmall)
        Text(dayOfWeek.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium)
        Text(formattedDate, style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pago:")
            Checkbox(
                checked = exercises.all { it.paid },
                onCheckedChange = { isChecked ->
                    exercises.forEachIndexed { index, ex ->
                        exercises[index] = ex.copy(paid = isChecked)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        exercises.forEachIndexed { index, exercise ->
            ExerciseCard(
                exercise = exercise,
                onPaidChange = { isChecked ->
                    exercises[index] = exercise.copy(paid = isChecked)
                }
            )
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onPaidChange: (Boolean) -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = androidx.compose.material3.CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(exercise.name, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pago: ")
                    Checkbox(checked = exercise.paid, onCheckedChange = onPaidChange)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Repetições: ${exercise.repetitions}")
                Text("Séries: ${exercise.series}", color = androidx.compose.ui.graphics.Color(0xFFCCE075))
                Text("Intervalo: ${exercise.intervalSeconds}s", color = androidx.compose.ui.graphics.Color(0xFFFFBDBD))
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen(
        onRegisterClick = { _, _, _ -> },
        onLoginClick = {}
    )
}