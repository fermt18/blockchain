package com.fermt.blockchain

class TransactionInput(val transactionOutputId: String) {

    lateinit var UTXO: TransactionOutput // Unspent Transaction Output
}