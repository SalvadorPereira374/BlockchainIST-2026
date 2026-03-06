package pt.tecnico.blockchainist.node;

import java.io.IOException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.tecnico.blockchainist.node.domain.NodeState;

public class NodeMain {
    //Quando é lançado, recebe o porto em que deve oferecer o seu serviço remoto, 
    //o nome da sua organização (uma _string_ sem espaços) assim como 
    //o nome de máquina e porto do sequenciador com o qual vai interagir.
    public static void main(String[] args) throws IOException, InterruptedException {
        int port = Integer.parseInt(args[0]);
        String organizationName = args[1];
        String machineNameSeq = args[2];
        ManagedChannel sequencerChannel = ManagedChannelBuilder.forTarget(machineNameSeq)
                .usePlaintext()
                .build();
        NodeState state = new NodeState();
        NodeServiceImpl service = new NodeServiceImpl(state);
        Server server = ServerBuilder.forPort(port)
                .addService(service)
                .build();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
            sequencerChannel.shutdown(); // Desligar também o cabo do sequenciador!
        }));
        server.awaitTermination();   
    }

}
