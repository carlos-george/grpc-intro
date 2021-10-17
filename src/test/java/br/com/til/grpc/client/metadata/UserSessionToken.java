package br.com.til.grpc.client.metadata;

import java.util.concurrent.Executor;

import io.grpc.CallCredentials;
import io.grpc.Metadata;

public class UserSessionToken extends CallCredentials {
	
	
	private String jwt;
	

	public UserSessionToken(String jwt) {
		super();
		this.jwt = jwt;
	}

	@Override
	public void applyRequestMetadata(RequestInfo requestInfo, Executor appExecutor, MetadataApplier applier) {
		
		appExecutor.execute(() -> {
			Metadata metadata = new Metadata();
			
			metadata.put(ClientConstants.USER_TOKEN, this.jwt);
			
			applier.apply(metadata);
			
//			applier.fail(null);
		});
		
	}

	@Override
	public void thisUsesUnstableApi() {
		// may change in future
	}

}
