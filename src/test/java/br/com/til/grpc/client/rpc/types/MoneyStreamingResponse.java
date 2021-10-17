 package br.com.til.grpc.client.rpc.types;

import java.util.concurrent.CountDownLatch;

import br.com.til.grpc.client.metadata.ClientConstants;
import br.com.til.models.Money;
import br.com.til.models.WithdrawalError;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class MoneyStreamingResponse implements StreamObserver<Money>{
	
	private CountDownLatch latch;

	public MoneyStreamingResponse(CountDownLatch latch) {
		this.latch = latch;
	}

	@Override
	public void onNext(Money money) {
		System.out.println("Received async : " + money.getValue());
		latch.countDown();
	}

	@Override
	public void onError(Throwable t) {
		
		Metadata metadata = Status.trailersFromThrowable(t);
		
		WithdrawalError withdrawalError = metadata.get(ClientConstants.WITHDRAWAL_ERROR_KEY);
		
		System.out.println(withdrawalError.getAmount() + " : " + withdrawalError.getErrorMessage());
		
		latch.countDown();
	}

	@Override
	public void onCompleted() {
		System.out.println("Server done with the task!!");
		latch.countDown();
	}

	
}
