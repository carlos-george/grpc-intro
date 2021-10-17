package br.com.til.grpc.client.metadata;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import br.com.til.grpc.client.rpc.types.MoneyStreamingResponse;
import br.com.til.models.Balance;
import br.com.til.models.BalanceCheckRequest;
import br.com.til.models.BankServiceGrpc;
import br.com.til.models.BankServiceGrpc.BankServiceBlockingStub;
import br.com.til.models.BankServiceGrpc.BankServiceStub;
import br.com.til.models.WithdrawRequest;
import io.grpc.Deadline;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.MetadataUtils;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MetadataClientTest {
	
	
	private BankServiceBlockingStub blockingStub;
	
	private BankServiceStub bankServiceStub;
	
	@BeforeAll
	public void setUp() {
		
		ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost", 9094)
			.intercept(MetadataUtils.newAttachHeadersInterceptor(ClientConstants.getClientToken()))
			.usePlaintext()
			.build();
		
		this.blockingStub = BankServiceGrpc.newBlockingStub(managedChannel);
		
		this.bankServiceStub = BankServiceGrpc.newStub(managedChannel);
		
	}
	
	@Test
	public void balanceTest() {
		
		BalanceCheckRequest balanceCheckRequest = BalanceCheckRequest.newBuilder()
		.setAccountNumber(7)
		.build();
		
		for (int i = 0; i < 10; i++) {
			try {
				
				int random = ThreadLocalRandom.current().nextInt(1,4);
				
				System.out.println("------------------------");
				System.out.println("Random: " + random);
				Balance balance = this.blockingStub
						.withCallCredentials(new UserSessionToken("user-secret-"+ random + ":standard"))
						.getBalance(balanceCheckRequest);

				System.out.println("Received: "+ balance.getAmount());
			} catch (StatusRuntimeException e) {
				System.out.println("Unauthorization!");
			}
		}
	}
	
	@Test
	public void withdrawTest() {
		WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
						.setAccountNumber(6)
						.setAmount(50)
						.build();
		try {
			
			this.blockingStub
					.withDeadline(Deadline.after(2, TimeUnit.SECONDS))
					.withdraw(withdrawRequest)
					.forEachRemaining(money -> System.out.println("Received : " + money.getValue()));
		} catch (StatusRuntimeException e) {
			e.printStackTrace();
//			Assertions.assertTrue(e.getMessage().contains("No enough money. You have only $"));
		}
	}
	
	@Test
	public void withdrawAsync() throws InterruptedException {
		CountDownLatch latch = new CountDownLatch(1);
		
		WithdrawRequest withdrawRequest = WithdrawRequest.newBuilder()
				.setAccountNumber(10)
				.setAmount(50)
				.build();
		this.bankServiceStub.withdraw( withdrawRequest, new MoneyStreamingResponse(latch));
		latch.await();
	}
	
}
