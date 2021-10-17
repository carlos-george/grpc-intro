package br.com.til.grpc.client.loadbalancing;

import java.net.URI;

import io.grpc.NameResolver;
import io.grpc.NameResolver.Args;
import io.grpc.NameResolverProvider;

public class TempNameResolverProvider extends NameResolverProvider {

	@Override
	protected boolean isAvailable() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected int priority() {
		// TODO Auto-generated method stub
		return 5;
	}

	@Override
	public String getDefaultScheme() {
		// TODO Auto-generated method stub
		return "dns";
	}

	@Override
	public NameResolver newNameResolver(URI targetUri, Args args) {

		System.out.println("Looking for service: " + targetUri.toString());
		
		return new TempNameResolver(targetUri.toString());
	}
	
	

}
