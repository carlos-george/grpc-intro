package br.com.til.grpc.server.rpc.types;

import br.com.til.models.Account;
import br.com.til.models.TransferRequest;
import br.com.til.models.TransferResponse;
import br.com.til.models.TransferStatus;
import io.grpc.stub.StreamObserver;

public class TransferStreamRequest implements StreamObserver<TransferRequest> {

	private StreamObserver<TransferResponse> responseObserver;
	
	public TransferStreamRequest(StreamObserver<TransferResponse> responseObserver) {
		super();
		this.responseObserver = responseObserver;
	}

	@Override
	public void onNext(TransferRequest request) {
		int fromAccount = request.getFromAccount();
		int toAccount = request.getToAccount();
		int amount = request.getAmount();
		int balance = AccountDataBase.getBalance(fromAccount);
		
		TransferStatus status = TransferStatus.FAILED;
		
		if (balance >= amount && fromAccount != toAccount) {
			AccountDataBase.deductBalance(fromAccount, amount);

			AccountDataBase.addBalance(toAccount, amount);
			
			status = TransferStatus.SUCCESS;
		}
		
		Account fromAccountInfo = Account.newBuilder()
			.setAccountNumber(fromAccount)
			.setAmount(AccountDataBase.getBalance(fromAccount))
			.build();
		Account toAccountInfo = Account.newBuilder()
				.setAccountNumber(toAccount)
				.setAmount(AccountDataBase.getBalance(toAccount))
				.build();
		
		TransferResponse transferResponse = TransferResponse.newBuilder()
			.setStatus(status)
			.addAccounts(fromAccountInfo)
			.addAccounts(toAccountInfo)
			.build();
		
		this.responseObserver.onNext(transferResponse);
	}

	@Override
	public void onError(Throwable t) {
		
	}

	@Override
	public void onCompleted() {
		
		AccountDataBase.printAccountDetails();
		this.responseObserver.onCompleted();
	}

}
