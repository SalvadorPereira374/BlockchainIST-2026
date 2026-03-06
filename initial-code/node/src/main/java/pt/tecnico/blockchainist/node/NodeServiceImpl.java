package pt.tecnico.blockchainist.node;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import pt.tecnico.blockchainist.contract.*;
import pt.tecnico.blockchainist.node.domain.Transaction;
import pt.tecnico.blockchainist.node.domain.NodeState;
import java.util.List;

public class NodeServiceImpl extends NodeServiceGrpc.NodeServiceImplBase{
    private final NodeState state;

    public NodeServiceImpl(NodeState state) {
        this.state = state;
    }
    @Override
    public void createWallet(CreateWalletRequest request, StreamObserver<CreateWalletResponse> response){
        try{
            String userID = request.getUserId();
            String walletID = request.getWalletId();
            state.createWallet(userID,walletID);
            CreateWalletResponse resp = CreateWalletResponse.newBuilder().build();
            response.onNext(resp);
            response.onCompleted();
        }catch (Exception e){
            response.onError(Status.ALREADY_EXISTS
            .withDescription(e.getMessage())
            .asRuntimeException());
        }
    }
    @Override
    public void transfer(TransferRequest request,StreamObserver<TransferResponse> response){
        try {
            state.transfer(request.getSrcUserId(),request.getSrcWalletId(),
            request.getDstWalletId(),request.getValue());
            TransferResponse resp = TransferResponse.newBuilder().build();
            response.onNext(resp);
            response.onCompleted();

        } catch (Exception e) {
            response.onError(Status.INVALID_ARGUMENT
                .withDescription(e.getMessage())
                .asRuntimeException());
        }
    }
    @Override
    public void deleteWallet(DeleteWalletRequest request, StreamObserver<DeleteWalletResponse> response){
        try{
            String userID = request.getUserId();
            String walletID = request.getWalletId();
            state.deleteWallet(userID,walletID);
            DeleteWalletResponse resp = DeleteWalletResponse.newBuilder().build();
            response.onNext(resp);
            response.onCompleted();
        } catch (Exception e){
            response.onError(Status.PERMISSION_DENIED
                .withDescription(e.getMessage())
                .asRuntimeException());
        }
    }

    @Override
    public void readBalance(ReadBalanceRequest request,StreamObserver<ReadBalanceResponse> response ){
        try{
            String walletID = request.getWalletId();
            long balance = state.readBalance(walletID);
            ReadBalanceResponse resp = ReadBalanceResponse.newBuilder().setBalance(balance).build();
            response.onNext(resp);
            response.onCompleted();
        } catch (Exception e){
            response.onError(Status.NOT_FOUND
                .withDescription(e.getMessage())
                .asRuntimeException());
        }
    }
    @Override
    public void getBlockchainState(GetBlockchainStateRequest request, StreamObserver<GetBlockchainStateResponse> response){
        List<Transaction> curBlockChain = state.getBlockchainState();
        GetBlockchainStateResponse.Builder resp = GetBlockchainStateResponse.newBuilder();
        for (Transaction t : curBlockChain) {
            pt.tecnico.blockchainist.contract.Transaction.Builder grpcTxBuilder = 
            pt.tecnico.blockchainist.contract.Transaction.newBuilder();
        String op = t.getOperation().toUpperCase();

        if (op.equals("CREATE")) {
            grpcTxBuilder.setCreateWallet(CreateWalletRequest.newBuilder()
                .setUserId(t.getUserId())
                .setWalletId(t.getWalletId())
                .build());
        } 
        else if (op.equals("TRANSFER")) {
            grpcTxBuilder.setTransfer(TransferRequest.newBuilder()
                .setSrcUserId(t.getUserId())     
                .setSrcWalletId(t.getWalletId())
                .setDstWalletId(t.getDstWalletId())
                .setValue(t.getAmount()) 
                .build());
        }
        else if (op.equals("DELETE")) {
            grpcTxBuilder.setDeleteWallet(DeleteWalletRequest.newBuilder()
                .setUserId(t.getUserId())
                .setWalletId(t.getWalletId())
                .build());
        }
        resp.addTransactions(grpcTxBuilder.build());

        }
        response.onNext(resp.build());
        response.onCompleted();
    }
    
}