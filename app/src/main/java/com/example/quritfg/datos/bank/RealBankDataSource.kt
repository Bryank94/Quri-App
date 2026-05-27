package com.example.quritfg.datos.bank

class RealBankDataSource(
    private val provider: BankProvider
) : BankDataSource {
    override suspend fun getConnectionStatus(): BankConnectionStatus =
        BankConnectionStatus(
            connected = false,
            providerName = provider.displayName,
            readOnly = true,
            lastSyncDate = null
        )

    override suspend fun getTransactions(): List<BankTransaction> {
        // Fase 1: aqui se conectara Tink, TrueLayer o GoCardless en modo solo lectura.
        // Todavia no se mueve dinero real, no hay wallet y no hay IBAN propio.
        return emptyList()
    }
}

enum class BankProvider(val displayName: String) {
    TINK("Tink"),
    TRUELAYER("TrueLayer"),
    GOCARDLESS("GoCardless Bank Account Data")
}
