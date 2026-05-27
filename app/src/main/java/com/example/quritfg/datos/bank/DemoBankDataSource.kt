package com.example.quritfg.datos.bank

import com.example.quritfg.datos.modelo.FechaQuri

class DemoBankDataSource : BankDataSource {
    override suspend fun getConnectionStatus(): BankConnectionStatus =
        BankConnectionStatus(
            connected = true,
            providerName = "Banco Quri Demo",
            readOnly = true,
            lastSyncDate = FechaQuri.hoyTexto()
        )

    override suspend fun getTransactions(): List<BankTransaction> =
        listOf(
            BankTransaction(
                id = "demo_salary_current_month",
                concept = "Nomina demo detectada",
                amountCentimos = 130_000L,
                date = FechaQuri.hoyTexto(),
                source = "Banco Quri Demo",
                type = BankTransactionType.INCOME,
                category = "Salario",
                isRecurring = true
            ),
            BankTransaction(
                id = "demo_rent_current_month",
                concept = "Alquiler mensual",
                amountCentimos = -520_00L,
                date = FechaQuri.hoyTexto(),
                source = "Banco Quri Demo",
                type = BankTransactionType.EXPENSE,
                category = "Vivienda",
                isRecurring = true
            )
        )
}
