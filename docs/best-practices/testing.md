<frontmatter>
  title: "Best Practices: Testing"
</frontmatter>

# Testing Best Practices

The goal is to make the system as fully covered by automated tests as possible.

## General guidelines

* **Try <tooltip content="Test Driven Development">TDD</tooltip>**: In particular, when fixing a bug, write the test case first before fixing it.
* **Write independent tests**: A test should have minimal to zero impact to global state/other tests that are running (possibly in parallel).
* **Write short tests**: It is better to have many `@Test`/`it` methods than to group all testing into one `@Test`/`it` method. This helps in debugging and rerunning failed tests.
* **Avoid redundant tests**: Be mindful of the cost of running a test. Do not add redundant tests.
* **Strive for 100% coverage**: Try to write tests to cover 100% of the functional code you wrote. We have configured JaCoCo for Java and Jest for TypeScript for your usage.
* **Coverage is not enough**: Be mindful of missing test cases (e.g., boundary values). Just because you have 100% coverage does not mean the code is error free. You could have missed execution paths in the test and in the SUT at the same time.
* **Test at different levels**: Test units at unit level. Do not attempt to test lower level components from higher level functionality.
* **Testing private methods**: It is OK to omit testing private methods if they are fully covered by tests written against public methods of the same class. If the private method is complex enough to warrant its own test method, you can use reflection to access the private method from the test class.
