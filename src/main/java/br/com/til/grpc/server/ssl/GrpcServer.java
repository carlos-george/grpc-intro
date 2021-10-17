package br.com.til.grpc.server.ssl;

import java.io.File;
import java.io.IOException;

import io.grpc.Server;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContext;
import io.grpc.netty.shaded.io.netty.handler.ssl.SslContextBuilder;

public class GrpcServer {

	public static void main(String[] args) throws IOException, InterruptedException {
		
		SslContext sslContext = GrpcSslContexts.configure(
				SslContextBuilder.forServer(
						new File("./ssl-tls/localhost.crt"),
						new File("./ssl-tls/localhost.pem")
				)
		).build();
		
		Server server = NettyServerBuilder.forPort(9095)
				.sslContext(sslContext)
				.addService(new BankService())
				.build();
		
		server.start();
		
		System.out.println("Server is up on port " + server.getPort());
		
		server.awaitTermination();
	}
}
