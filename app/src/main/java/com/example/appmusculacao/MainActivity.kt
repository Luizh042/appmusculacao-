package com.example.appmusculacao

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
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
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import java.util.UUID

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val markedDates = remember { mutableStateListOf<LocalDate>() }

            NavHost(navController, startDestination = "register") {
                composable("register") {
                    val context = LocalContext.current //aqui e para usarmos o Toast

                    RegisterScreen(
                        onRegisterClick = { name, email, password ->

                            val interactor = RegisterInteractor(context)

                            interactor.output = object : RegisterInteractorOutput {
                                override fun onRegisterSuccess(user: User) {
                                    // Sucesso já estava implementado
                                    Toast.makeText(
                                        context,
                                        "Usuario logado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("login")
                                }

                                override fun onRegisterFailure(error: String) {
                                    //erro no registro
                                    Toast.makeText(
                                        context,
                                        "erro de registro: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            interactor.register(name, password, email) // testado

                        },
                        onLoginClick = { navController.navigate("login") }
                    )
                }
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = { navController.navigate("workout") },
                        onBackToRegister = {
                            navController.navigate("register") {
                                popUpTo("register") { inclusive = true }
                            }
                        }
                    )
                }
                composable("workout") {
                    WorkoutScreen(
                        onGoToCalendar = { navController.navigate("calendar") },
                        onWorkoutMarked = { date ->
                            if (!markedDates.contains(date)) {
                                markedDates.add(date)
                            }
                        }
                    )
                }
                composable("calendar") {
                    CalendarScreen(markedDates = markedDates)
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(onRegisterClick: (String, String, String) -> Unit, onLoginClick: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Criar Conta", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onRegisterClick(name, email, password) }, modifier = Modifier.fillMaxWidth()) {
            Text("Criar Conta")
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onLoginClick) {
            Text("Já tenho uma conta")
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onBackToRegister: () -> Unit) {
    var currentStep by remember { mutableStateOf(1) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { if (email.isNotBlank()) currentStep = 2 },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continuar")
                }
                TextButton(onClick = onBackToRegister) {
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
                            // Integração com o LoginInteractor testado
                            val interactor = LoginInteractor()

                            interactor.output = object : LoginInteractorOutput {
                                override fun onLoginSuccess(user: User) {
                                    // Sucesso no login
                                    Toast.makeText(
                                        context,
                                        "Login realizado com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    onLoginSuccess() // Chama o callback original
                                }

                                override fun onLoginFailure(error: String) {
                                    // Erro no login
                                    Toast.makeText(
                                        context,
                                        "Erro: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            // Usa email como username (adaptação para seu caso)
                            interactor.login(email, password, context)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Entrar")
                }
                TextButton(onClick = { currentStep = 1; password = "" }) {
                    Text("Voltar")
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutScreen(onGoToCalendar: () -> Unit, onWorkoutMarked: (LocalDate) -> Unit) {
    val dateNow = LocalDate.now()
    val dayOfWeek = dateNow.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
    val formattedDate = dateNow.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")))

    val exercises = remember {
        mutableStateListOf(
            Exercise(UUID.randomUUID().toString(), "Supino Reto", 12, 4, 60),
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Exercícios", style = MaterialTheme.typography.headlineSmall)
        Text(dayOfWeek.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium)
        Text(formattedDate, style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Pago:")
            Checkbox(
                checked = exercises.all { it.paid },
                onCheckedChange = { isChecked ->
                    exercises.indices.forEach { index -> exercises[index] = exercises[index].copy(paid = isChecked) }
                    if (isChecked) onWorkoutMarked(dateNow)
                }
            )
        }

        exercises.forEachIndexed { index, exercise ->
            ExerciseCard(
                exercise = exercise,
                onPaidChange = { isChecked ->
                    exercises[index] = exercise.copy(paid = isChecked)
                    if (exercises.all { it.paid }) onWorkoutMarked(dateNow)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onGoToCalendar, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text("Ver Calendário")
        }
    }
}

@Composable
fun ExerciseCard(exercise: Exercise, onPaidChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(exercise.name, style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Pago: ")
                    Checkbox(checked = exercise.paid, onCheckedChange = onPaidChange)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Repetições: ${exercise.repetitions}")
                Text("Séries: ${exercise.series}", color = Color(0xFFCCE075))
                Text("Intervalo: ${exercise.intervalSeconds}s", color = Color(0xFFFFBDBD))
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarScreen(markedDates: List<LocalDate>) {
    val currentMonth = remember { YearMonth.now() }
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Calendário de Exercícios", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(currentMonth.month.getDisplayName(TextStyle.FULL, Locale("pt", "BR")).replaceFirstChar { it.uppercase() })
            Text("${currentMonth.year}")
        }

        Spacer(modifier = Modifier.height(8.dp))

        val days = (1..daysInMonth).map { day -> currentMonth.atDay(day) }

        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("D", "S", "T", "Q", "Q", "S", "S").forEach {
                    Text(it, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }

            days.chunked(7).forEach { week ->
                Row(Modifier.fillMaxWidth()) {
                    week.forEach { date ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .background(if (markedDates.contains(date)) Color.Green else Color.LightGray)
                                .height(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${date.dayOfMonth}", color = Color.Black)
                        }
                    }
                }
            }
        }
    }
}

data class ExerciseModel(
    val name: String,
    val pago: Boolean,
    val repeticoes: Int,
    val series: Int,
    val intervalo: Double
)

class ExerciseStore(context: Context) {
    private val prefs = context.getSharedPreferences("exercises", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun save(exercises: List<ExerciseModel>) {
        val json = gson.toJson(exercises)
        prefs.edit().putString("exercise_list", json).apply()
    }

    fun load(): List<ExerciseModel> {
        val json = prefs.getString("exercise_list", null) ?: return emptyList()
        return gson.fromJson(json, object : TypeToken<List<ExerciseModel>>() {}.type)
    }
}

@Composable
fun ExerciseFormView(
    initial: ExerciseModel? = null,
    onSave: (ExerciseModel) -> Unit,
    onCancel: () -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var pago by remember { mutableStateOf(initial?.pago ?: false) }
    var repeticoes by remember { mutableStateOf(initial?.repeticoes?.toString() ?: "") }
    var series by remember { mutableStateOf(initial?.series?.toString() ?: "") }
    var intervalo by remember { mutableStateOf(initial?.intervalo?.toString() ?: "") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Formulário de Exercício", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nome") })
        OutlinedTextField(value = repeticoes, onValueChange = { repeticoes = it }, label = { Text("Repetições") })
        OutlinedTextField(value = series, onValueChange = { series = it }, label = { Text("Séries") })
        OutlinedTextField(value = intervalo, onValueChange = { intervalo = it }, label = { Text("Intervalo (s)") })

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = pago, onCheckedChange = { pago = it })
            Text("Pago")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (name.isNotBlank() && repeticoes.isNotBlank() && series.isNotBlank() && intervalo.isNotBlank()) {
                onSave(
                    ExerciseModel(
                        name,
                        pago,
                        repeticoes.toInt(),
                        series.toInt(),
                        intervalo.toDouble()
                    )
                )
            }
        }) {
            Text("Salvar")
        }

        TextButton(onClick = onCancel) {
            Text("Cancelar")
        }
    }
}

@Composable
fun ExerciseListView(context: Context) {
    val store = remember { ExerciseStore(context) }
    val exercises = remember { mutableStateListOf<ExerciseModel>().apply { addAll(store.load()) } }
    var showForm by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Lista de Exercícios", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        exercises.forEachIndexed { index, exercise ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${exercise.name} (${exercise.series}x${exercise.repeticoes}) - ${exercise.intervalo}s")
                TextButton(onClick = {
                    exercises.removeAt(index)
                    store.save(exercises)
                }) {
                    Text("Remover")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = { showForm = true }) {
            Text("Adicionar Exercício")
        }

        if (showForm) {
            ExerciseFormView(
                onSave = {
                    exercises.add(it)
                    store.save(exercises)
                    showForm = false
                },
                onCancel = { showForm = false }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview(){
    RegisterScreen(
        onRegisterClick = {name, _, _ ->
            println("login ${name.uppercase()}")
        },
        onLoginClick = {}
    )

}