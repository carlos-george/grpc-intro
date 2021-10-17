package br.com.til.grpc.server.metadata;

import java.util.Objects;

import io.grpc.Context;
import io.grpc.Contexts;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCall.Listener;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import io.grpc.Status;

/*
 
  user-secret-3:prime
  user-secret-2:regular
 
 */

public class AuthInterceptor implements ServerInterceptor {

	@Override
	public <ReqT, RespT> Listener<ReqT> interceptCall(
				ServerCall<ReqT, RespT> call, 
				Metadata headers,
				ServerCallHandler<ReqT, RespT> next
			) {
		
		String clientToken = headers.get(ServerConstants.USER_TOKEN);
		
		if(this.validateUserToken(clientToken)) {
			
			UserRole userRole = this.extractUserRole(clientToken);
			
			Context context = Context.current().withValue(ServerConstants.CTX_USER_ROLE, userRole);
			
			return Contexts.interceptCall(context, call, headers, next);
			
//			return next.startCall(call, headers);
		} else {
			
			Status status = Status.UNAUTHENTICATED.withDescription("Invalid token/expired token");
			call.close(status, headers);
		}
		
		return new ServerCall.Listener<ReqT>() {
				};
	}

	private boolean validate(String token) {
		return Objects.nonNull(token) && token.startsWith("bank-client-secret");
	}
	
	private boolean validateUserToken(String token) {
		return Objects.nonNull(token) && 
				(token.startsWith("user-secret-3") || token.startsWith("user-secret-2"));
	}
	
	private UserRole extractUserRole(String jwt) {
		return jwt.endsWith("prime") ? UserRole.PRIME : UserRole.STANDARD;
	}
}
