package com.example.demo;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;

interface Exports extends JSObject {
	void greet();
	String hello(String... args);
}

public class HelloWorld  implements Exports  {

	public static void main(String[] args) {
		export(new HelloWorld());
	}
	
	public String hello(String... args) {
		return Arrays.asList(args).stream().collect(Collectors.joining(" "));
	}

	public void greet() {
		System.out.println(hello("Hello", "World"));
	}
	
	@JSBody(params = "arg", script = "main.exports = arg;")
	public static native void export(Exports arg);

}