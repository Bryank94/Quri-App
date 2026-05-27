package com.example.quritfg.datos.bank

enum class BankTransactionType {
    INCOME,
    EXPENSE
}

data class BankTransaction(
    val id: String,
    val concept: String,
    val amountCentimos: Long,
    val date: String,
    val source: String,
    val type: BankTransactionType,
    val category: String? = null,
    val isRecurring: Boolean = false
)

data class BankConnectionStatus(
    val connected: Boolean,
    val providerName: String? = null,
    val readOnly: Boolean = true,
    val lastSyncDate: String? = null
)

interface BankDataSource {
    suspend fun getConnectionStatus(): BankConnectionStatus
    suspend fun getTransactions(): List<BankTransaction>
}
