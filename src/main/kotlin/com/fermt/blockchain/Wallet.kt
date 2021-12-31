package com.fermt.blockchain

import com.fermt.blockchain.NoobChain.Companion.UTXOs
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.spec.ECGenParameterSpec


class Wallet {

    lateinit var privateKey: PrivateKey
    lateinit var publicKey: PublicKey

    init {
        generateKeyPair()
    }

    fun generateKeyPair(){
        val keyGen = KeyPairGenerator.getInstance("ECDSA", "BC")
        val random = SecureRandom.getInstance("SHA1PRNG")
        val ecSpec = ECGenParameterSpec("prime192v1")
        keyGen.initialize(ecSpec, random)
        val keyPair = keyGen.generateKeyPair()
        privateKey = keyPair.private
        publicKey = keyPair.public
    }

    fun getBalance(): Float {
        var total = 0F
        for(entry in NoobChain.UTXOs.entries){
            val UTXO = entry.value
            if(UTXO.isMine(publicKey)){
                UTXOs.put(UTXO.id, UTXO)
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