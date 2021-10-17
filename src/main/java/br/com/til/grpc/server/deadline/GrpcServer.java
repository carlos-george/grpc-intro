package br.com.til.grpc.server.deadline;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;

public class GrpcServer {

	public static void main(String[] args) throws IOException, InterruptedException {
		Server server = ServerBuilder.forPort(9093)
		.addService(new DeadLineService())
		.build();
		
		server.start();
		
		System.out.println("Server is up on port " + server.getPort());
		
		server.awaitTermination();
	}
}
