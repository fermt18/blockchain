package com.fermt.blockchain

import NoobChain
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.security.Security

class TestHash {

    private lateinit var keySet1: KeySet
    private lateinit var keySet2: KeySet

    @BeforeEach
    fun init(){
        Security.addProvider(BouncyCastleProvider())
        keySet1 = HashUtils.generateKeyPair()
        keySet2 = HashUtils.generateKeyPair()
    }

    @Test
    fun test_hashLength(){
        val firstBlock = Block("first")
        assertEquals(64, firstBlock.hash.length)
    }

    // Transaction
    @Test
    fun test_processTransactionWithOutSignature(){
        val tx = Transaction(keySet1.publicKey, keySet2.publicKey, 100f, null)
        assertFalse(tx.processTransaction())
    }

    @Test
    fun test_processTransactionWithSignature(){
        val tx = Transaction(keySet1.publicKey, keySet2.publicKey, 100f, null)
        tx.generateSignature(keySet1.privateKey)
        assertTrue(tx.processTransaction())
        assertEquals(64, tx.transactionId.length)
    }

    // Block
    @Test
    fun test_addValidTransactionToABlock(){
        val tx = Transaction(keySet1.publicKey, keySet2.publicKey, 100f, null)
        tx.generateSignature(keySet1.privateKey)
        val block = Block(tx.transactionId)
        assertTrue(block.addTransaction(tx))
    }

    // Wallet
    @Test
    fun test_walletCreation(){
        assertEquals(0F, Wallet().getBalance())
    }

    // HashUtils
    @Test
    fun test_difficultyString(){
        assertEquals("000", HashUtils.getDifficultyString(3))
    }

    @Test
    fun test_main(){
        NoobChain.main()
    }


}