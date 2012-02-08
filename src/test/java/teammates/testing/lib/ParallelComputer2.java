package teammates.testing.lib;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.runner.Computer;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;

/**
 * Modified from:
 * https://github.com/KentBeck/junit/blob/master/src/main/java/org
 * /junit/experimental/ParallelComputer.java
 * 
 * to limit number of threads executing at the same time. Also see:
 * http://stackoverflow
 * .com/questions/949355/newcachedthreadpool-v-s-newfixedthreadpool
 * 
 * @author huy
 */
public class ParallelComputer2 extends Computer {
	private final boolean fClasses;

	/**
	 * Maximum number of threads used
	 */
	private static final int NUM_THREADS = 4;

	private final boolean fMethods;

	public ParallelComputer2(boolean classes, boolean methods) {
		fClasses = classes;
		fMethods = methods;
	}

	public static Computer classes() {
		return new ParallelComputer2(true, false);
	}

	public static Computer methods() {
		return new ParallelComputer2(false, true);
	}

	private static <T> Runner parallelize(Runner runner) {
		if (runner instanceof ParentRunner<?>) {
			((ParentRunner<?>) runner).setScheduler(new RunnerScheduler() {
				private final List<Future<Object>> fResults = new ArrayList<Future<Object>>();

				private final ExecutorService fService = Executors.newFixedThreadPool(NUM_THREADS);

				public void schedule(final Runnable childStatement) {
					fResults.add(fService.submit(new Callable<Object>() {
						public Object call() throws Exception {
							childStatement.run();
							return null;
						}
					}));
				}

				public void finished() {
					for (Future<Object> each : fResults)
						try {
							each.get();
						} catch (Exception e) {
							e.printStackTrace();
						}
				}
			});
		}
		return runner;
	}

	@Override
	public Runner getSuite(RunnerBuilder builder, java.lang.Class<?>[] classes)
		throws InitializationError {
		Runner suite = super.getSuite(builder, classes);
		return fClasses ? parallelize(suite) : suite;
	}

	@Override
	protected Runner getRunner(RunnerBuilder builder, Class<?> testClass)
		throws Throwable {
		Runner runner = super.getRunner(builder, testClass);
		return fMethods ? parallelize(runner) : runner;
	}
}