package com.fermt.blockchain

import com.fermt.blockchain.NoobChain.Companion.UTXOs
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.security.Security

class TestHash {

    @Test
    fun testBasicHash(){
        val firstBlock = Block("first")
        assertEquals(64, firstBlock.hash.length)
    }

    @Test
    fun testMain(){
        Security.addProvider(BouncyCastleProvider())
        val walletA = Wallet()
        val walletB = Wallet()
        val coinbase = Wallet()

        val genesisTransaction = Transaction(coinbase.publicKey, walletA.publicKey, 100f, null)
        genesisTransaction.generateSignature(coinbase.privateKey)
        genesisTransaction.transactionId = "0"
        genesisTransaction.outputs.add(
            TransactionOutput(
            genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId))
        UTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0))

        println("Creating and mining Genesis Block...")
        val genesis = Block("0")
        genesis.addTransaction(genesisTransaction)
        NoobChain.addBlock(genesis)

        // testing

    }
}