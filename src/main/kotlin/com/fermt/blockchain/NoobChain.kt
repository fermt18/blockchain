package com.fermt.blockchain

class NoobChain {
    companion object {
        val blockChain = arrayListOf<Block>()
        val UTXOs = hashMapOf<String, TransactionOutput>()
        val minimumTransaction = 0.1F
        val difficulty = 3

        fun addBlock(newBlock: Block){
            newBlock.mineBlock(difficulty)
            blockChain.add(newBlock)
        }
    }
}