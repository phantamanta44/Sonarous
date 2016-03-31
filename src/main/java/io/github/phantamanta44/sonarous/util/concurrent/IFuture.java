package io.github.phantamanta44.sonarous.util.concurrent;

public interface IFuture<T> {
	
	boolean isDone();
	
	T getResult();
	
}
