package com.fermt.blockchain

import NoobChain.Companion.UTXOs
import NoobChain.Companion.UTXs
import com.fermt.blockchain.HashUtils.Companion.generateKeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec


class Wallet {

    var privateKey: PrivateKey
    var publicKey: PublicKey

    init {
        val keySet = generateKeyPair()
        privateKey = keySet.privateKey
        publicKey = keySet.publicKey
    }

    fun getBalance(): Float {
        var total = 0F
        for(entry in NoobChain.UTXs.entries){
            val UTXO = entry.value
            if(UTXO.isMine(publicKey)){
                UTXs.put(UTXO.transactionId, UTXO)
                total += UTXO.value
            }
        }
        return total
    }

    fun sendFunds(_recipient: PublicKey?, value: Float): Transaction? {
        if (getBalance() < value) {
            println("#Not Enough funds to send transaction. Transaction Discarded.")
            return null
        }
        val inputs = ArrayList<TransactionInput>()
        var total = 0f
        for (entry in UTXOs) {
            val UTXO = entry.value
            total += UTXO.value
            inputs.add(TransactionInput(UTXO.id))
            if (total > value) break
        }
        val newTransaction = Transaction(publicKey, _recipient!!, value, inputs)
        newTransaction.generateSignature(privateKey)
        for (input in inputs) {
            UTXOs.remove(input.transactionOutputId)
        }
        return newTransaction
    }
}