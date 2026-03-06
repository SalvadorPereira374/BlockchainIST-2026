package pt.tecnico.blockchainist.node.domain;

public class Transaction{
    private String operation;
    private String userId;
    private String walletId;
    private String dstWalletID;
    private long amount; //ver se usar com minuscula ou n 

    public Transaction(String operation,String userId,String walletId,String dstWalletId,long amount){
        this.operation = operation;
        this.userId = userId;
        this.walletId = walletId;
        this.dstWalletID = dstWalletId;
        this.amount = amount;
    }
    public String getOperation(){
        return this.operation;
    }
    public String getUserId(){
        return this.userId;
    }
    public String getWalletId(){
        return this.walletId;
    }
    public long getAmount(){
        return this.amount;
    }
    public String getDstWalletId(){
        return this.dstWalletID;
    }
}
