package pt.tecnico.blockchainist.client.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.tecnico.blockchainist.contract.*;

public class ClientNodeService {
    private final ManagedChannel channel;
    private final NodeServiceGrpc.NodeServiceBlockingStub Stub;
    
    public ClientNodeService(String host, int port, String organization) {
        this.channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().build();
        this.Stub = NodeServiceGrpc.newBlockingStub(this.channel);

    }
    public CreateWalletResponse createWallet(CreateWalletRequest request){
        return this.Stub.createWallet(request);
    }
    public DeleteWalletResponse deleteWallet(DeleteWalletRequest request){ 
        return this.Stub.deleteWallet(request);
    }
    public ReadBalanceResponse readBalance(ReadBalanceRequest request){
        return this.Stub.readBalance(request);
    }
    public TransferResponse transfer(TransferRequest request){
        return this.Stub.transfer(request);
    }
    public GetBlockchainStateResponse getBlockchainState(GetBlockchainStateRequest request){
        return this.Stub.getBlockchainState(request);
    }
    public void close() {
        if (this.channel != null) {
            this.channel.shutdown();
        }
    }
}
//duvida se faco metodo para fehcar canal 