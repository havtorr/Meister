package edu.buffalo.cse.jive.model.factory;

import edu.buffalo.cse.jive.model.IQueryModel;

public interface IQueryFactory extends IQueryModel
{
  public EventPredicate createExceptionCaughtQuery(final ExceptionQueryParams params);

  public EventPredicate createExceptionThrownQuery(final ExceptionQueryParams params);

  public EventPredicate createInvariantViolatedQuery(final InvariantViolatedQueryParams params);

  public EventPredicate createLineExecutedQuery(final LineExecutedQueryParams params);

  public EventPredicate createMethodCalledQuery(final MethodQueryParams params);

  public EventPredicate createMethodReturnedQuery(final MethodReturnedQueryParams params);

  public EventPredicate createObjectCreatedQuery(final ObjectCreatedQueryParams params);

  public EventPredicate createSlicingQuery(final SlicingQueryParams params);

  public EventPredicate createVariableChangedQuery(final VariableChangedQueryParams params);
}