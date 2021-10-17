package br.com.til.grpc.server.loadbalancing;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer2 {

	public static void main(String[] args) throws IOException, InterruptedException {
		Server server = ServerBuilder.forPort(9092)
		.addService(new BankService())
		.build();
		
		server.start();
		
		System.out.println("Server is up on port " + server.getPort());
		
		server.awaitTermination();
	}
}
