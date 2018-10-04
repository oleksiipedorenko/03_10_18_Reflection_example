package ua.skillsup.practice.reflection;

import ua.skillsup.practice.reflection.context.Context;
import ua.skillsup.practice.reflection.service.MainService;
import ua.skillsup.practice.reflection.service.MainServiceImpl;
import ua.skillsup.practice.reflection.service.SlaveServiceImpl;

import java.lang.reflect.InvocationTargetException;

public class App {

	public static void main(String[] args) throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Context context = new Context(MainServiceImpl.class, SlaveServiceImpl.class);

		MainService service = context.getBean(MainService.class);
		service.doSmt();
	}
}
