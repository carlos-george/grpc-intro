package br.com.til.grpc.client.deadline;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.grpc.CallOptions;
import io.grpc.Channel;
import io.grpc.ClientCall;
import io.grpc.ClientInterceptor;
import io.grpc.Deadline;
import io.grpc.MethodDescriptor;

public class DeadlineInterceptor implements ClientInterceptor{

	@Override
	public <ReqT, RespT> ClientCall<ReqT, RespT> interceptCall(MethodDescriptor<ReqT, RespT> method,
			CallOptions callOptions, Channel channel) {

		Deadline deadline = callOptions.getDeadline();
		
		if(Objects.isNull(deadline)) {
			callOptions = callOptions.withDeadline(Deadline.after(4, TimeUnit.SECONDS));
		}
			
		return channel.newCall(
					method, 
					callOptions
				);
	}

}
