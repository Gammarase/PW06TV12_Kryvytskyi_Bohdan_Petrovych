package com.example.pw06tv12_kryvytskyi_bohdan_petrovych

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElectricalCalculationApp()
        }
    }
}

@Composable
fun ElectricalCalculationApp() {
    var power by remember { mutableStateOf("26") }
    var coef by remember { mutableStateOf("0.27") }
    var tan by remember { mutableStateOf("1.62") }
    var results by remember { mutableStateOf<ElectricalResults?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Electrical Calculation", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields
        TextField(
            value = power,
            onValueChange = { power = it },
            label = { Text("Nominal Power") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = coef,
            onValueChange = { coef = it },
            label = { Text("Usage Coefficient") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = tan,
            onValueChange = { tan = it },
            label = { Text("Tangent") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            results = calculateElectricalParameters(
                power.toDouble(),
                coef.toDouble(),
                tan.toDouble()
            )
        }) {
            Text("Calculate")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results Display
        results?.let { ResultsDisplay(it) }
    }
}

data class ElectricalResults(
    val groupKv: Double,
    val effectiveEPnum: Int,
    val estimatedActiveLoad: Double,
    val estimatedReactiveLoad: Double,
    val fullPower: Double,
    val groupElectricity: Double,
    val totalKv: Double,
    val totalEffectiveEPnum: Int,
    val estimatedTireActiveLoad: Double,
    val estimatedTireReactiveLoad: Double,
    val fullTirePower: Int,
    val tireGroupElectricity: Double
)

fun calculateElectricalParameters(
    pn: Double,  // Nominal Power
    kv: Double,  // Usage Coefficient
    tg: Double   // Tangent
): ElectricalResults {
    val productNominalSand = 4 * pn

    val sumProductNominalMultKv = (productNominalSand * 0.15) + 3.36 + 25.2 + 10.8 + 10 + (40 * kv) + 12.8 + 13
    val sumProductNominal = productNominalSand + 28 + 168 + 36 + 20 + 40 + 64 + 20
    val squarePSumProductNominal = (4 * pn.pow(2)) + 392 + 7056 + 1296 + 400 + 1600 + 2048 + 400
    val sumProductNominalMultKvWithTg = (productNominalSand * 0.15 * 1.33) + 3.36 + 33.5 + (36 * 0.3 * tg) + 7.5 + (40 * kv * 1) + 12.8 + 9.5

    val groupKv = sumProductNominalMultKv / sumProductNominal
    val effectiveEPnum = (sumProductNominal.pow(2) / squarePSumProductNominal) + 1
    val estimatedActLoad = 1.25 * sumProductNominalMultKv
    val estimatedREactLoad = 1 * sumProductNominalMultKvWithTg
    val fullPower = sqrt(estimatedActLoad.pow(2) + estimatedREactLoad.pow(2))
    val groupElectricity = estimatedActLoad / 0.38

    val totalKv = 752.0 / 2330
    val totalEffectiveEPnum = 2330.0.pow(2) / 96399
    val estimatedTireActLoad = 0.7 * 752
    val estimatedTireREactLoad = 0.7 * 657
    val fullTirePower = sqrt(estimatedTireActLoad.pow(2) + estimatedTireREactLoad.pow(2))
    val tireGroupElectricity = estimatedTireActLoad / 0.38

    return ElectricalResults(
        groupKv = groupKv,
        effectiveEPnum = effectiveEPnum.toInt(),
        estimatedActiveLoad = estimatedActLoad,
        estimatedReactiveLoad = estimatedREactLoad,
        fullPower = fullPower,
        groupElectricity = groupElectricity,
        totalKv = totalKv,
        totalEffectiveEPnum = totalEffectiveEPnum.toInt(),
        estimatedTireActiveLoad = estimatedTireActLoad,
        estimatedTireReactiveLoad = estimatedTireREactLoad,
        fullTirePower = fullTirePower.toInt(),
        tireGroupElectricity = tireGroupElectricity
    )
}

@Composable
fun ResultsDisplay(results: ElectricalResults) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Results:", style = MaterialTheme.typography.titleMedium)

            ResultRow("Group Kv", "%.4f".format(results.groupKv))
            ResultRow("Effective EPnum", results.effectiveEPnum.toString())
            ResultRow("Active Power", "1.25")
            ResultRow("Estimated Active Load", "%.2f".format(results.estimatedActiveLoad))
            ResultRow("Estimated Reactive Load", "%.2f".format(results.estimatedReactiveLoad))
            ResultRow("Full Power", "%.3f".format(results.fullPower))
            ResultRow("Group Electricity", "%.2f".format(results.groupElectricity))
            ResultRow("Total Kv", "%.2f".format(results.totalKv))
            ResultRow("Total Effective EPnum", results.totalEffectiveEPnum.toString())
            ResultRow("Total Active Power", "0.7")
            ResultRow("Estimated Tire Active Load", "%.1f".format(results.estimatedTireActiveLoad))
            ResultRow("Estimated Tire Reactive Load", "%.1f".format(results.estimatedTireReactiveLoad))
            ResultRow("Full Tire Power", results.fullTirePower.toString())
            ResultRow("Tire Group Electricity", "%.2f".format(results.tireGroupElectricity))
        }
    }
}

@Composable
fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}