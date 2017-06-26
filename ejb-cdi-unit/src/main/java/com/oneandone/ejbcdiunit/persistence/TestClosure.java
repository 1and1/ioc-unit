package com.oneandone.ejbcdiunit.persistence;

/**
 * This Interface should be implemented by Test-Parts which will be encapsulated in transactions.
 * To avoid having to implement try/catch in these parts, execute may throw Exception.
 * The Lambda executing code must handle this correctly. In case of Tests it is feasible to resend the Exception as
 * RuntimeException.
 * @author aschoerk
 */
public interface TestClosure {

    /**
     * The code to be executed as lambda
     * @throws Exception The checked Exception the code may throw. The caller of the lambda could encapsulate it as
     * unchecked exception
     */
    void execute() throws Exception;
}
