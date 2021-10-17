package br.com.til.grpc.client.rpc.types;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import br.com.til.models.TransferRequest;
import br.com.til.models.TransferServiceGrpc;
import br.com.til.models.TransferServiceGrpc.TransferServiceStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransferClientTest {
	
	private TransferServiceStub stub;

	
	@BeforeAll
	public void setUp() {
		
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
			.usePlaintext()
			.build();
		
		this.stub = TransferServiceGrpc.newStub(managedChannel);
		
	}
	
	@Test
	public void transfer() throws InterruptedException {
		
		CountDownLatch latch = new CountDownLatch(1);
		
		TransferStreamResponse response = new TransferStreamResponse(latch);
		
		StreamObserver<TransferRequest> transferObserver = this.stub.transfer(response);
		
		for (int i = 0; i < 100; i++) {
			TransferRequest transferRequest = TransferRequest.newBuilder()
				.setFromAccount(ThreadLocalRandom.current().nextInt(1, 11))
				.setToAccount(ThreadLocalRandom.current().nextInt(1, 11))
				.setAmount(ThreadLocalRandom.current().nextInt(1, 20))
				.build();
			
			transferObserver.onNext(transferRequest);
		}
		
		transferObserver.onCompleted();
		latch.await();
	}
}
