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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
                    val context = LocalContext.current

                    RegisterScreen(
                        onRegisterClick = { name, email, password ->
                            val interactor = RegisterInteractor(context)

                            interactor.output = object : RegisterInteractorOutput {
                                override fun onRegisterSuccess(user: User) {
                                    Toast.makeText(
                                        context,
                                        "Usuario logado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.navigate("login")
                                }

                                override fun onRegisterFailure(error: String) {
                                    Toast.makeText(
                                        context,
                                        "erro de registro: $error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                            interactor.register(name, password, email)
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
                        onGoToExerciseList = { navController.navigate("exercise_list") },
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
                composable("exercise_list") {
                    ExerciseListView(context = LocalContext.current)
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

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        when (currentStep) {
            1 -> {
                Text("Digite seu e-mail", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("E-mail") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { if (email.isNotBlank()) currentStep = 2 }, modifier = Modifier.fillMaxWidth()) {
                    Text("Continuar")
                }
                TextButton(onClick = onBackToRegister) {
                    Text("Ainda não tem uma conta? Cadastre-se")
                }
            }
            2 -> {
                Text("Digite sua senha", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Senha") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { if (password.isNotBlank()) onLoginSuccess() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Entrar")
                }
                TextButton(onClick = { currentStep = 1; password = "" }) {
                    Text("Voltar")
                }
            }
        }
    }
}

// DATA CLASSES PARA DIFERENTES TIPOS DE EXERCÍCIO
data class Exercise(val name: String, val repetitions: Int, val series: Int, val intervalSeconds: Int, var paid: Boolean = false)

data class ExerciseModel(
    val name: String,
    val pago: Boolean,
    val repeticoes: Int,
    val series: Int,
    val intervalo: Double
)

// CLASSE PARA UNIFICAR OS TIPOS DE EXERCÍCIO
data class UnifiedExercise(
    val name: String,
    val repetitions: Int,
    val series: Int,
    val intervalSeconds: Int,
    val paid: Boolean
)

// EXTENSÕES PARA CONVERTER ENTRE OS TIPOS
fun Exercise.toUnified() = UnifiedExercise(name, repetitions, series, intervalSeconds, paid)
fun ExerciseModel.toUnified() = UnifiedExercise(name, repeticoes, series, intervalo.toInt(), pago)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WorkoutScreen(
    onGoToCalendar: () -> Unit,
    onGoToExerciseList: () -> Unit,
    onWorkoutMarked: (LocalDate) -> Unit
) {
    val context = LocalContext.current
    val dateNow = LocalDate.now()
    val dayOfWeek = dateNow.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
    val formattedDate = dateNow.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", Locale("pt", "BR")))

    // EXERCÍCIOS PADRÃO (FIXOS)
    val defaultExercises = listOf(
        Exercise("Supino Reto", 12, 4, 60),
        Exercise("Agachamento", 12, 3, 90),
        Exercise("Rosca Direta", 15, 4, 45),
        Exercise("Puxada Alta", 12, 4, 60),
        Exercise("Remada Curvada", 10, 4, 60),
        Exercise("Desenvolvimento", 12, 4, 60)
    )

    // CARREGAR EXERCÍCIOS SALVOS E COMBINÁ-LOS COM OS PADRÃO
    val store = remember { ExerciseStore(context) }
    val savedExercises = remember { store.load() }

    // CONVERTER EXERCÍCIOS SALVOS PARA O TIPO Exercise
    val convertedSavedExercises = savedExercises.map { saved ->
        Exercise(
            name = saved.name,
            repetitions = saved.repeticoes,
            series = saved.series,
            intervalSeconds = saved.intervalo.toInt(),
            paid = saved.pago
        )
    }

    // RECARREGAR EXERCÍCIOS SEMPRE QUE A TELA FOR RECOMPOSTA
    val allExercises = remember(savedExercises) {
        mutableStateListOf<Exercise>().apply {
            addAll(defaultExercises)
            addAll(convertedSavedExercises)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Exercícios", style = MaterialTheme.typography.headlineSmall)
        Text(dayOfWeek.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.bodyMedium)
        Text(formattedDate, style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Pago:")
            Checkbox(
                checked = allExercises.all { it.paid },
                onCheckedChange = { isChecked ->
                    allExercises.indices.forEach { index ->
                        allExercises[index] = allExercises[index].copy(paid = isChecked)
                    }
                    if (isChecked) onWorkoutMarked(dateNow)
                }
            )
        }

        allExercises.forEachIndexed { index, exercise ->
            // USANDO O EXERCISECARD UNIFICADO - SEM BOTÃO REMOVER
            ExerciseCard(
                exercise = exercise.toUnified(),
                onPaidChange = { isChecked ->
                    allExercises[index] = exercise.copy(paid = isChecked)
                    if (allExercises.all { it.paid }) onWorkoutMarked(dateNow)
                }
                // onRemove não é passado, então não aparece botão remover
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onGoToCalendar,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            ) {
                Text("Ver Calendário")
            }

            Button(
                onClick = onGoToExerciseList,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Gerenciar Exercícios")
            }
        }
    }
}

// EXERCISECARD UNIFICADO - FUNCIONA PARA AMBAS AS TELAS
@Composable
fun ExerciseCard(
    exercise: UnifiedExercise,
    onPaidChange: (Boolean) -> Unit,
    onRemove: (() -> Unit)? = null // PARÂMETRO OPCIONAL
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // CABEÇALHO - NOME E BOTÃO REMOVER (SE DISPONÍVEL)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    exercise.name,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                // SÓ MOSTRA O BOTÃO REMOVER SE onRemove FOR FORNECIDO
                if (onRemove != null) {
                    TextButton(
                        onClick = onRemove,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.Red
                        )
                    ) {
                        Text("Remover", fontSize = 12.sp)
                    }
                } else {
                    // SE NÃO TEM BOTÃO REMOVER, MOSTRA O CHECKBOX À DIREITA
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Pago: ")
                        Checkbox(checked = exercise.paid, onCheckedChange = onPaidChange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // INFORMAÇÕES DO EXERCÍCIO
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Repetições: ${exercise.repetitions}")
                Text(
                    "Séries: ${exercise.series}",
                    color = Color(0xFFCCE075)
                )
                Text(
                    "Intervalo: ${exercise.intervalSeconds}s",
                    color = Color(0xFFFFBDBD)
                )
            }

            // SE TEM BOTÃO REMOVER, CHECKBOX VAI EMBAIXO
            if (onRemove != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Pago: ")
                    Checkbox(
                        checked = exercise.paid,
                        onCheckedChange = onPaidChange
                    )
                }
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

    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Formulário de Exercício", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                errorMessage = ""
            },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = repeticoes,
            onValueChange = {
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    repeticoes = it
                    errorMessage = ""
                }
            },
            label = { Text("Repetições") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = series,
            onValueChange = {
                if (it.isEmpty() || it.all { char -> char.isDigit() }) {
                    series = it
                    errorMessage = ""
                }
            },
            label = { Text("Séries") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = intervalo,
            onValueChange = {
                if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                    intervalo = it
                    errorMessage = ""
                }
            },
            label = { Text("Intervalo (segundos)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = pago, onCheckedChange = { pago = it })
            Text("Pago")
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                try {
                    when {
                        name.isBlank() -> {
                            errorMessage = "Nome do exercício é obrigatório"
                            return@Button
                        }
                        repeticoes.isBlank() -> {
                            errorMessage = "Número de repetições é obrigatório"
                            return@Button
                        }
                        series.isBlank() -> {
                            errorMessage = "Número de séries é obrigatório"
                            return@Button
                        }
                        intervalo.isBlank() -> {
                            errorMessage = "Intervalo é obrigatório"
                            return@Button
                        }
                    }

                    val repeticoesInt = repeticoes.toIntOrNull()
                    val seriesInt = series.toIntOrNull()
                    val intervaloDouble = intervalo.toDoubleOrNull()

                    when {
                        repeticoesInt == null || repeticoesInt <= 0 -> {
                            errorMessage = "Repetições deve ser um número válido maior que 0"
                            return@Button
                        }
                        seriesInt == null || seriesInt <= 0 -> {
                            errorMessage = "Séries deve ser um número válido maior que 0"
                            return@Button
                        }
                        intervaloDouble == null || intervaloDouble < 0 -> {
                            errorMessage = "Intervalo deve ser um número válido maior ou igual a 0"
                            return@Button
                        }
                    }

                    val exercise = ExerciseModel(
                        name = name.trim(),
                        pago = pago,
                        repeticoes = repeticoesInt,
                        series = seriesInt,
                        intervalo = intervaloDouble
                    )

                    onSave(exercise)

                } catch (e: Exception) {
                    errorMessage = "Erro ao salvar exercício: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }
    }
}

// EXERCISELISTVIEW COM SCROLL
@Composable
fun ExerciseListView(context: Context) {
    val store = remember { ExerciseStore(context) }
    val exercises = remember { mutableStateListOf<ExerciseModel>().apply { addAll(store.load()) } }
    var showForm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Lista de Exercícios", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // USANDO O EXERCISECARD UNIFICADO - COM BOTÃO REMOVER
        exercises.forEachIndexed { index, exercise ->
            ExerciseCard(
                exercise = exercise.toUnified(),
                onPaidChange = { isChecked ->
                    exercises[index] = exercise.copy(pago = isChecked)
                    store.save(exercises)
                },
                onRemove = { // PASSANDO onRemove PARA MOSTRAR BOTÃO
                    exercises.removeAt(index)
                    store.save(exercises)
                }
            )
        }

        if (exercises.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Nenhum exercício cadastrado",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Text(
                        "Clique em 'Adicionar Exercício' para começar",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showForm = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Adicionar Exercício")
        }

        if (showForm) {
            Spacer(modifier = Modifier.height(16.dp))
            ExerciseFormView(
                onSave = { newExercise ->
                    exercises.add(newExercise)
                    store.save(exercises)
                    showForm = false
                },
                onCancel = { showForm = false }
            )
        }
    }
}