package br.com.til.grpc.client.loadbalancing;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import br.com.til.grpc.client.rpc.types.BalanceStreamObserver;
import br.com.til.models.Balance;
import br.com.til.models.BalanceCheckRequest;
import br.com.til.models.BankServiceGrpc;
import br.com.til.models.BankServiceGrpc.BankServiceBlockingStub;
import br.com.til.models.BankServiceGrpc.BankServiceStub;
import br.com.til.models.DepositRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NginxClientTest {
	
	private BankServiceBlockingStub blockingStub;
	
	private BankServiceStub bankServiceStub;
	
	
	@BeforeAll
	public void setUp() {
		
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 8585)
			.usePlaintext()
			.build();
		
		this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
		
		this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
		
	}
	
	@Test
	public void balanceTest() {
		
		for (int i = 0; i < 100; i++) {
			
			BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
					.setAccountNumber(ThreadLocalRandom.current().nextInt(1,11))
					.build();
			
			Balance balance = this.blockingStub.getBalance(balanceCheckRequest);
			
			System.out.println("Received: "+ balance.getAmount());
		}
		
	}
	
	@Test
	public void cashStreamingRequest() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		StreamObserver<DepositRequest> cashDepositStream = this.bankServiceStub.cashDeposit(new BalanceStreamObserver(latch));
		
		for (int i = 0; i < 10; i++) {
			DepositRequest request = DepositRequest.newBuilder().setAccountNumber(8).setAmount(10).build();
			
			cashDepositStream.onNext(request);
		}
		
		cashDepositStream.onCompleted();
		latch.await();
	}

}
