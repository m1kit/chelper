package net.egork.chelper.parser;

import net.egork.chelper.task.Task;
import net.egork.chelper.task.TestType;

import javax.swing.*;
import java.util.Collection;

/**
 * @author Egor Kulikov (kulikov@devexperts.com)
 */
public interface Parser {
	default String getName() {
		return this.getClass().getSimpleName().replace("Parser", "");
	}

	default TestType defaultTestType() {
		return TestType.SINGLE;
	}

	Collection<Task> parse(String content);
}
