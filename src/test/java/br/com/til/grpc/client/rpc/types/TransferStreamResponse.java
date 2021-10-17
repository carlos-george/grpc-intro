package br.com.til.grpc.client.rpc.types;

import java.util.concurrent.CountDownLatch;

import br.com.til.models.TransferResponse;
import io.grpc.stub.StreamObserver;

public class TransferStreamResponse implements StreamObserver<TransferResponse> {
	
	private CountDownLatch latch;
	
	public TransferStreamResponse(CountDownLatch latch) {
		super();
		this.latch = latch;
	}

	@Override
	public void onNext(TransferResponse response) {
		
		System.out.println("Status: " + response.getStatus());
		
		response.getAccountsList()
			.stream()
			.map(account -> account.getAccountNumber() + " : " + account.getAmount())
			.forEach(System.out::println);
		
		System.out.println("--------------------------");
		
		this.latch.countDown();
	}

	@Override
	public void onError(Throwable t) {

		this.latch.countDown();
	}

	@Override
	public void onCompleted() {
		System.out.println("All transfer was done!");
		
		this.latch.countDown();
		
	}

}
