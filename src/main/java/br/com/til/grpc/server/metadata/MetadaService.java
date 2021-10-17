package br.com.til.grpc.server.metadata;


import br.com.til.grpc.server.rpc.types.AccountDataBase;
import br.com.til.grpc.server.rpc.types.CashStreamRequest;
import br.com.til.models.Balance;
import br.com.til.models.BalanceCheckRequest;
import br.com.til.models.BankServiceGrpc.BankServiceImplBase;
import br.com.til.models.DepositRequest;
import br.com.til.models.ErrorMessage;
import br.com.til.models.Money;
import br.com.til.models.WithdrawRequest;
import br.com.til.models.WithdrawalError;
import io.grpc.Metadata;
import io.grpc.Status;
import io.grpc.protobuf.ProtoUtils;
import io.grpc.stub.StreamObserver;

//import br.com.til.models.BankServiceGrpc;

public class MetadaService extends BankServiceImplBase {

	@Override
	public void getBalance(BalanceCheckRequest request, StreamObserver<Balance> responseObserver) {

		int accountNumber = request.getAccountNumber();
		
		int amount = AccountDataBase.getBalance(accountNumber);
		
		UserRole userRole = ServerConstants.CTX_USER_ROLE.get();
		UserRole userRole1 = ServerConstants.CTX_USER_ROLE1.get();
		
		amount = UserRole.PRIME.equals(userRole) ? amount : amount - 15;
		
		System.out.println(userRole + " : " + userRole1);
		
		Balance balance = Balance.newBuilder()
		.setAmount(amount)
		.build();
		
//		Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
		responseObserver.onNext(balance);
		System.out.println("Get Balance Completed!");
		responseObserver.onCompleted();
	}

	@Override
	public void withdraw(WithdrawRequest request, StreamObserver<Money> responseObserver) {
		
		int accountNumber = request.getAccountNumber();
		
		int amount = request.getAmount();
		
		int balance = AccountDataBase.getBalance(accountNumber);
		
		
		if(amount < 10 || (amount % 10) != 0) {
			
			Metadata metadata = new Metadata();
			
			Metadata.Key<WithdrawalError> errorKey = ProtoUtils.keyForProto(WithdrawalError.getDefaultInstance());
			
			WithdrawalError withdrawalError = WithdrawalError.newBuilder().setAmount(balance).setErrorMessage(ErrorMessage.ONLY_TEN_MULTIPLES).build();
			
			metadata.put(errorKey, withdrawalError);
			
//			Status status = Status.FAILED_PRECONDITION.withDescription("No enough money. You have only $" + balance);
			
			responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException(metadata));
			
			return;
		}
		
		
		if(balance < amount) {
			
			Metadata metadata = new Metadata();
			
			Metadata.Key<WithdrawalError> errorKey = ProtoUtils.keyForProto(WithdrawalError.getDefaultInstance());
			
			WithdrawalError withdrawalError = WithdrawalError.newBuilder().setAmount(balance).setErrorMessage(ErrorMessage.INSUFFICIENT_BALANCE).build();
			
			metadata.put(errorKey, withdrawalError);
			
//			Status status = Status.FAILED_PRECONDITION.withDescription("No enough money. You have only $" + balance);
			
			responseObserver.onError(Status.FAILED_PRECONDITION.asRuntimeException(metadata));
			
			return;
		}
		
		for (int i = 0; i < (amount/10); i++) {
			Money money = Money.newBuilder().setValue(10).build();
			
//			Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
//			if(!Context.current().isCancelled()) {
				responseObserver.onNext(money);
//				System.out.println("Delivery $10");
				AccountDataBase.deductBalance(accountNumber, 10);
//			} else {
//				break;
//			}
		}
		
		System.out.println("WithDraw Completed!");
		responseObserver.onCompleted();
	}

	@Override
	public StreamObserver<DepositRequest> cashDeposit(StreamObserver<Balance> responseObserver) {

		return new CashStreamRequest(responseObserver);
	}
	
	

}
