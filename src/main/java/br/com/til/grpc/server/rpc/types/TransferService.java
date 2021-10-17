package br.com.til.grpc.server.rpc.types;

import br.com.til.models.TransferRequest;
import br.com.til.models.TransferResponse;
import br.com.til.models.TransferServiceGrpc.TransferServiceImplBase;
import io.grpc.stub.StreamObserver;

public class TransferService extends TransferServiceImplBase {

	@Override
	public StreamObserver<TransferRequest> transfer(StreamObserver<TransferResponse> responseObserver) {

		
		
		return new TransferStreamRequest(responseObserver);
	}

	
}
