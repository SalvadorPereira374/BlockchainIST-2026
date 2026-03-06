package pt.tecnico.blockchainist.node.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class NodeState {
    
    // TODO Declare state maintained by each node
    // - The set of wallets, indexed by their identifiers, and their owner user identifiers (including the 'bc' wallet)
    // - The balance of each wallet
    // - The transaction ledger (up to A.2, a chain of individual transactions; after B.1, a chain of blocks)
    //entrega A1 o node guarda apenas a copia do estado global da aplicacao
    // Um conjunto de carteiras existentes (incluindo a `bc`) e os utilizadores que as possuem;
   //Um saldo associado a cada carteira, que nunca pode ser negativo.
   //Existe um utilizador especial, chamado *central bank* (BC), cuja carteira `bc` é pré-existente.
   //e tem inicialmente saldo de 1000 unidades de criptomeda.
    private final ConcurrentHashMap<String,String> Wallets = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String,Long> WalletMoney = new ConcurrentHashMap<>();
    private final List<Transaction> BlockChain = new ArrayList<>();
    //investigar lista de transacoes
    public NodeState() {
        Wallets.put("bc","BC");
        WalletMoney.put("bc",1000L);
        Transaction newTransaction = new Transaction("CREATE","BC","bc",null,1000L);
        BlockChain.add(newTransaction);
    }

    public synchronized void createWallet(String userId, String walletId) throws Exception {
        if (Wallets.contains(walletId)){
            throw new Exception(("A carteira com id " + walletId + " já existe."));
        }
        Wallets.put(walletId,userId);
        WalletMoney.put(walletId,0L);
        Transaction newTRansaction = new Transaction("CREATE",userId,walletId,null,0);
        BlockChain.add(newTRansaction);
        System.out.println("Sucesso");
    }

    public synchronized void deleteWallet(String userId, String walletId) throws Exception {
        if (!Wallets.containsKey(walletId)){
            throw new Exception("A carteira '" + walletId + "' não existe.");
        }
        if (!Wallets.get(walletId).equals(userId)) {
        throw new Exception("O utilizador '" + userId + "' não é dono.");
        }
        if (WalletMoney.get(walletId)!=0){
            throw new Exception("A carteira tem "+ walletId+ " saldo diferente de zero");
        }
        Wallets.remove(walletId);
        WalletMoney.remove(walletId);
        Transaction newTRansaction = new Transaction("DELETE",userId,walletId,null,0L);
        BlockChain.add(newTRansaction);
    }

    public synchronized void transfer(String srcUserId, String srcWalletId, String dstWalletId, Long amount) throws Exception {
        if (!Wallets.containsKey(srcWalletId)||!Wallets.containsKey(dstWalletId)){
            throw new Exception("Carteira inexistente");
        }
        if (!Wallets.get(srcWalletId).equals(srcUserId)) {
        throw new Exception("O utilizador '" + srcUserId + "' não é dono.");
        }
        if(WalletMoney.get(srcWalletId)< amount){
            throw new Exception("Dinheiro insuficiente para transferir.");
        }
        if(amount<=0){
            throw new Exception("Quantia insuficiente.");
        }
        long new_dst_value = WalletMoney.get(dstWalletId) + amount;
        long new_src_value = WalletMoney.get(srcWalletId) - amount;
        WalletMoney.put(dstWalletId,new_dst_value);
        WalletMoney.put(srcWalletId,new_src_value);
        Transaction newTRansaction = new Transaction("TRANSFER",srcUserId,srcWalletId,dstWalletId,amount);
        BlockChain.add(newTRansaction);
    }

    public long readBalance(String walletId) {
        // ver se verifico se a carteira existe 
        return WalletMoney.get(walletId);
    }

    public  List<Transaction> getBlockchainState(){
        return BlockChain;
    }


}
