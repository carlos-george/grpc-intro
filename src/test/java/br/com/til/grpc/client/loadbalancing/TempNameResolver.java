 package br.com.til.grpc.client.loadbalancing;

import java.util.List;

import io.grpc.EquivalentAddressGroup;
import io.grpc.NameResolver;

public class TempNameResolver extends NameResolver {
	
	private final String service;
	
	public TempNameResolver(String service) {
		super();
		this.service = service;
	}

	@Override
	public String getServiceAuthority() {
		return "temp";
	}

	@Override
	public void shutdown() {
		
	}

	@Override
	public void start(Listener2 listener) {
		
		List<EquivalentAddressGroup> addressGroups = ServiceRegistry.getInstances(this.service);
		
		ResolutionResult resolutionResult = ResolutionResult.newBuilder().setAddresses(addressGroups).build();
		
		listener.onResult(resolutionResult);
		
	}
	
	

}
