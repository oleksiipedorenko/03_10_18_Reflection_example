package ua.skillsup.practice.reflection.service;

public class MainServiceImpl implements MainService {

	private final SlaveService slave;

	public MainServiceImpl(SlaveServiceImpl slave) {
		this.slave = slave;
	}

	public void doSmt() {
		System.out.println("I'm a master, lets slave work");
		slave.serve();
	}
}
