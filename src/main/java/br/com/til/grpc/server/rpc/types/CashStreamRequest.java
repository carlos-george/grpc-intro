package br.com.til.grpc.server.rpc.types;

import br.com.til.models.Balance;
import br.com.til.models.DepositRequest;
import io.grpc.stub.StreamObserver;

public class CashStreamRequest implements StreamObserver<DepositRequest> {
	
	private StreamObserver<Balance> streamObserver;
	
	private int accountBalance; 
	
	public CashStreamRequest(StreamObserver<Balance> streamObserver) {
		this.streamObserver = streamObserver;
	}

	@Override
	public void onNext(DepositRequest request) {
		int accountNumber = request.getAccountNumber();
		
		int amount = request.getAmount();
		
		this.accountBalance = AccountDataBase.addBalance(accountNumber, amount);
		
		
	}

	@Override
	public void onError(Throwable t) {
		
	}

	@Override
	public void onCompleted() {
		Balance balance = Balance.newBuilder().setAmount(this.accountBalance).build();
		
		this.streamObserver.onNext(balance);
		this.streamObserver.onCompleted();
	}

	
}
