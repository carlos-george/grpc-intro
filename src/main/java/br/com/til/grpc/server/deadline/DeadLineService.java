package br.com.til.grpc.server.deadline;


import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;

import br.com.til.grpc.server.rpc.types.AccountDataBase;
import br.com.til.grpc.server.rpc.types.CashStreamRequest;
import br.com.til.models.Balance;
import br.com.til.models.BalanceCheckRequest;
import br.com.til.models.BankServiceGrpc.BankServiceImplBase;
import br.com.til.models.DepositRequest;
import br.com.til.models.Money;
import br.com.til.models.WithdrawRequest;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

//import br.com.til.models.BankServiceGrpc;

public class DeadLineService extends BankServiceImplBase {

	@Override
	public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {

		int accountNumber = request.getAccountNumber();
		
		Balance balance = Balance.newBuilder()
		.setAmount(AccountDataBase.getBalance(accountNumber))
		.build();
		
		Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
		responseObserver.onNext(balance);
		responseObserver.onCompleted();
	}

	@Override
	public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
		
		int accountNumber = request.getAccountNumber();
		
		int amount = request.getAmount();
		
		int balance = AccountDataBase.getBalance(accountNumber);
		
		if(balance < amount) {
			Status status = Status.FAILED_PRECONDITION.withDescription("No enough money. You have only $" + balance);
			responseObserver.onError(status.asRuntimeException());
			return;
		}
		
		for (int i = 0; i < (amount/10); i++) {
			Money money = Money.newBuilder()
			.setValue(10)
			.build();
			
			Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
			if(!Context.current().isCancelled()) {
				responseObserver.onNext(money);
				System.out.println("Delivery $10");
				AccountDataBase.deductBalance(accountNumber, 10);
			} else {
				break;
			}
		}
		
		System.out.println("Completed!");
		responseObserver.onCompleted();
	}

	@Override
	public StreamObserver<DepositRequest> cashDeposit(StreamObserver<Balance> responseObserver) {

		return new CashStreamRequest(responseObserver);
	}
	
	

}
