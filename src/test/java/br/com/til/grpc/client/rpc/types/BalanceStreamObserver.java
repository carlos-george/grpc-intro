package br.com.til.grpc.client.rpc.types;

import java.util.concurrent.CountDownLatch;

import br.com.til.models.Balance;
import io.grpc.stub.StreamObserver;

public class BalanceStreamObserver implements StreamObserver<Balance> {
	
	private CountDownLatch latch;

	public BalanceStreamObserver(CountDownLatch latch) {
		this.latch = latch;
	}

	@Override
	public void onNext(Balance balance) {

		System.out.println("Final Balance: " + balance.getAmount());
	}

	@Override
	public void onError(Throwable t) {
		latch.countDown();
	}

	@Override
	public void onCompleted() {
		System.out.println("Server done with balance!");
		latch.countDown();
	}

}
