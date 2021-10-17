package br.com.til.grpc.client.rpc.types;

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import br.com.til.models.Balance;
import br.com.til.models.BalanceCheckRequest;
import br.com.til.models.BankServiceGrpc;
import br.com.til.models.BankServiceGrpc.BankServiceBlockingStub;
import br.com.til.models.BankServiceGrpc.BankServiceStub;
import br.com.til.models.DepositRequest;
import br.com.til.models.WithdrawRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BankClientTest {
	
	
	private BankServiceBlockingStub blockingStub;
	
	private BankServiceStub bankServiceStub;
	
	@BeforeAll
	public void setUp() {
		
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9094)
			.usePlaintext()
			.build();
		
		this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
		
		this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
		
	}
	
	@Test
	public void balanceTest() {
		
		BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
		.setAccountNumber(2)
		.build();
		
		Balance balance = this.blockingStub.getBalance(balanceCheckRequest);
		
		System.out.println("Received: "+ balance.getAmount());
	}
	
	@Test
	public void withdrawTest() {
		WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
		.setAccountNumber(7)
		.setAmount(40)
		.build();
//		try {
			
			this.blockingStub.withdraw(withdrawRequest)
			.forEachRemaining(money -> System.out.println("Received : " + money.getValue()));
//		} catch (StatusRuntimeException e) {
//			
//			Assertions.assertTrue(e.getMessage().contains("No enough money. You have only $"));
//		}
	}
	
	@Test
	public void withdrawAsync() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		
		WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
				.setAccountNumber(10)
				.setAmount(60)
				.build();
		this.bankServiceStub.withdraw( withdrawRequest, new MoneyStreamingResponse(latch));
		latch.await();
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
