package edu.buffalo.cse.jive.model;

import java.util.List;

import edu.buffalo.cse.jive.model.IContourModel.IContextContour;
import edu.buffalo.cse.jive.model.IContourModel.IContour;
import edu.buffalo.cse.jive.model.IContourModel.IContourMember;
import edu.buffalo.cse.jive.model.IContourModel.IMethodContour;
import edu.buffalo.cse.jive.model.IContourModel.IObjectContour;
import edu.buffalo.cse.jive.model.IExecutionModel.IStateChange;

public interface IEventModel extends IModel
{
  /**
   * Every data event is of a particular kind.
   */
  public enum EventKind
  {
    //
    // Exception control events
    //
    EXCEPTION_CATCH("Exception Catch"),
    EXCEPTION_THROW("Exception Throw"),
    //
    // Field data events
    //
    FIELD_READ("Field Read"),
    FIELD_WRITE("Field Write"),
    //
    // Line control event
    //
    LINE_STEP("Line Step"),
    //
    // Method control events
    //
    METHOD_CALL("Method Call"),
    METHOD_ENTERED("Method Entered"),
    METHOD_EXIT("Method Exit"),
    METHOD_RETURNED("Method Returned"),
    //
    // Monitor related events
    //
    MONITOR_LOCK_BEGIN("Monitor Lock Begin"),
    MONITOR_LOCK_END("Monitor Lock End"),
    MONITOR_LOCK_FAST("Monitor Lock Fast"),
    MONITOR_RELOCK("Monitor Relock"),
    MONITOR_UNLOCK_BEGIN("Monitor Unlock Begin"),
    MONITOR_UNLOCK_COMPLETE("Monitor Unlock Complete"),
    MONITOR_UNLOCK_END("Monitor Unlock End"),
    MONITOR_UNLOCK_FAST("Monitor Unlock Fast"),
    //
    // Object Allocation
    //
    OBJECT_DESTROY("Destroy Object"),
    OBJECT_NEW("New Object"),
    //
    // Scope control events
    //
    SCOPE_ALLOC("Scope Alloc"),
    SCOPE_ASSIGN("Scope Assign"),
    SCOPE_BACKING_ALLOC("Scope Backing Alloc"),
    SCOPE_BACKING_FREE("Scope Backing Free"),
    SCOPE_ENTER("Scope Enter"),
    SCOPE_EXIT("Scope Exit"),
    SCOPE_FREE("Scope Free"),
    SCOPE_POP("Scope Pop"),
    SCOPE_PUSH("Scope Push"),
    //
    // System start/end markers
    //
    SYSTEM_END("System End"),
    SYSTEM_START("System Start"),
    //
    // Thread control events
    //
    THREAD_CREATE("Thread Create"),
    THREAD_END("Thread End"),
    THREAD_LOCK("Lock State"),
    THREAD_PRIORITY("Thread Priority"),
    THREAD_SLEEP("Thread Sleep"),
    THREAD_START("Thread Start"),
    THREAD_WAKE("Thread Wake"),
    THREAD_YIELD("Thread Yield"),
    //
    // Type allocation
    //
    TYPE_LOAD("Type Load"),
    //
    // Local variable data events
    //
    VAR_ASSIGN("Variable Write"),
    VAR_DELETE("Variable Delete");
    private final String eventName;

    private EventKind(final String eventName)
    {
      this.eventName = eventName;
    }

    public String eventName()
    {
      return this.eventName;
    }

    @Override
    public String toString()
    {
      return this.eventName;
    }
  }

  /**
   * Generic assignment event.
   */
  public interface IAssignEvent extends IDataEvent
  {
    public IValue getLastValue();

    /**
     * New value assigned to the variable.
     * 
     * @return the variable's new value
     */
    public IValue newValue();

    /**
     * Used during slicing to keep track of the in-slice old value of this event's contour member.
     */
    public void setLastAssignment(IAssignEvent event);
  }

  /**
   * Generic event containing one data (contour member) reference.
   */
  public interface IDataEvent extends IMethodBodyEvent
  {
    /**
     * Get the contour that contains the variable that changed. If this is a variable assignment,
     * then the contour is a method contour, otherwise it is a field's instance or static contour.
     * 
     * @return method contour containing the changed variable
     */
    public IContour contour();

    /**
     * Member that has been assigned a value. This is a member of the contour with the given
     * {@code contourId()} value.
     * 
     * @return member that changed
     */
    public IContourMember member();
  }

  /**
   * An event corresponding to the destruction of an object. When an object is destructed, contours
   * are removed for it and all of its superclass objects.
   */
  public interface IDestroyObjectEvent extends IJiveEvent
  {
    /**
     * Innermost contour destroyed in response to this event. Traversing the parent axis provides
     * all instance contours destroyed for this object.
     */
    public IObjectContour destroyedContour();
  }

  /**
   * Classes that wish to be notified of the occurrence of data events.
   */
  public interface IEventListener
  {
    /**
     * Called when an event is created by this event source.
     * 
     * @param source
     *          the event source
     * @param event
     *          the event that occurred
     */
    public void eventOccurred(IEventProducer source, List<IJiveEvent> events);
  }

  /**
   * Producer data events.
   */
  public interface IEventProducer
  {
    /**
     * Add a listener to this event source.
     * 
     * @param listener
     */
    public void subscribe(IEventListener listener);

    /**
     * Remove a listener from this event source.
     * 
     * @param listener
     */
    public void unsubscribe(IEventListener listener);
  }

  /**
   * TODO: This method doesn't need the catcher nor the member. These are naturally captured by the
   * VarAssignEvent when the exception is assigned to the variable. Likewise, the exception caught
   * form query can be executed as a variable changed query, using the appropriate variable type
   * and/or name.
   * 
   * An event corresponding to an exception being caught.
   */
  public interface IExceptionCatchEvent extends IDataEvent
  {
    /**
     * Returns the contour of the method activation that caught the exception.
     * 
     * @return the catcher's contour
     */
    @Override
    public IMethodContour contour();

    /**
     * Returns the exception that was caught.
     * 
     * @return the caught exception
     */
    public IValue exception();
  }

  /**
   * Event representing an exception thrown by the application. This event is generated at the
   * initial throw (regardless of whether the exception is caught there) and also for any methods
   * that do not handle the exception.
   */
  public interface IExceptionThrowEvent extends IMethodTerminatorEvent
  {
    /**
     * Exception that was thrown.
     */
    public IValue exception();

    /**
     * Method activation that (originally) threw the exception.
     */
    public IValue thrower();
  }

  /**
   * Field assignment.
   */
  public interface IFieldAssignEvent extends IAssignEvent
  {
    /**
     * Context contour that contains the field. The context contour is either a static or instance
     * contour and must be in the model, since we only track changes to monitored contours.
     */
    @Override
    public IContextContour contour();
  }

  /**
   * Field read event.
   */
  public interface IFieldReadEvent extends IDataEvent
  {
    /**
     * Context contour that contains the field. The context contour is either a static or instance
     * contour and must be in the model, since we only track changes to monitored contours.
     */
    @Override
    public IContextContour contour();
  }

  /**
   * Events that mark the beginning of an execution unit. Since initiator events uniquely identify
   * its associated execution unit, the event also provides services to explore and analyze the
   * underlying execution.
   */
  public interface IInitiatorEvent extends IJiveEvent
  {
    /**
     * Duration of the execution in terms of the number of events occurring while the execution
     * occurrence is active. This includes events
     * <ul>
     * <li>in this execution,</li>
     * <li>in child executions, and
     * <li>in other threads before this execution completes.</li>
     * </ul>
     * 
     * @return number of events elapsing while the execution is active
     */
    public long duration();

    /**
     * Events occurring within this method's execution.
     */
    public List<IJiveEvent> events();

    /**
     * Identifier of the method environment (e.g., method contour) that represents the execution
     * initiated by this event.
     */
    public IMethodContour execution();

    /**
     * Identifier of the context (e.g., static or instance contour) that represents the environment
     * within which the execution initiated by this event takes place.
     */
    public IContextContour executionContext();

    /**
     * Indicates whether some event has occurred in the context of this initiator.
     */
    public boolean hasChildren();

    /**
     * Indicates whether the event initiates an in-model execution.
     */
    public boolean inModel();

    /**
     * Last event to occur within the execution initiated by this event. Will coincide with the
     * event's terminator only when the initiated execution terminates.
     */
    public IJiveEvent lastChildEvent();

    /**
     * Events that initiated nested executions in the context of the initiated execution.
     */
    public List<? extends IInitiatorEvent> nestedInitiators();

    /**
     * Event that marks the end of the execution of this unit.
     * 
     * @return the terminator event, if this execution unit is complete, otherwise {@code null}
     */
    public ITerminatorEvent terminator();
  }

  /**
   * Base type for all trace events. Some of members actually contain new model information while
   * others simply compute their data from the event log, as necessary.
   */
  public interface IJiveEvent extends IModel, Comparable<IJiveEvent>
  {
    /**
     * Details about this event.
     */
    public String details(OutputFormat format);

    /**
     * Identifier of this event. Each event has a unique identifier with respect to an execution.
     */
    public long eventId();

    /**
     * Determines if this event is visible in the current view of the model. False only when a
     * filter is active and this method does not satisfy the filter condition.
     */
    public boolean isVisible();

    /**
     * Kind of the event that determines the data encoded by the event.
     */
    public EventKind kind();

    /**
     * Source line causing the event.
     */
    public ILineValue line();

    /**
     * Convenience method to get the next event in the trace.
     */
    public IJiveEvent next();

    /**
     * Event that identifies the execution context within which this event occurred.
     */
    public IInitiatorEvent parent();

    /**
     * Convenience method to get the prior event in the trace.
     */
    public IJiveEvent prior();

    /**
     * Thread on which the event takes place.
     */
    public IThreadValue thread();

    /**
     * Transaction encapsulating the state changes caused by this event.
     */
    public ITransaction transaction();
  }

  /**
   * An begin-of-statement event. Such events are produced when a statement is about to be executed
   * by the underlying program.
   * 
   * @see {@code com.sun.jdi.event.StepEvent}
   */
  public interface ILineStepEvent extends IMethodBodyEvent
  {
  }

  public interface ILockEvent extends IJiveEvent
  {
    public IContour lock();

    public String lockDescription();

    public LockOperation lockOperation();
  }

  /**
   * All events signaled from a method execution.
   */
  public interface IMethodBodyEvent extends IJiveEvent
  {
    /**
     * Event that identifies the execution context within which this event occurred.
     */
    @Override
    public IMethodCallEvent parent();
  }

  /**
   * An event corresponding to a method call. The terminator can be either a return or throw event.
   * The former is the standard and happens when a method execution terminates normally, while the
   * latter indicates that the execution terminated in response to an exception.
   */
  public interface IMethodCallEvent extends IInitiatorEvent
  {
    /**
     * Entity that represents the method where the call originates. For in-model callers,
     * {@code Value} is a reference value.
     */
    public IValue caller();

    /**
     * Method call events that initiated nested executions in the context of the initiated
     * execution.
     */
    @Override
    public List<IMethodCallEvent> nestedInitiators();

    /**
     * Entity that represents the called method. For in-model targets, {@code Value} is a an
     * in-model target value ({@code Value.InModelTargetValue}).
     */
    public IValue target();

    /**
     * Can only be terminated by a method exit or exception.
     */
    @Override
    public IMethodTerminatorEvent terminator();
  }

  /**
   * Signals that the body of a method (parent initiator) is about to execute.
   */
  public interface IMethodEnteredEvent extends IMethodBodyEvent
  {
  }

  /**
   * Event that represents a method's return. The returning method may be in the model or outside of
   * the model.
   */
  public interface IMethodExitEvent extends IMethodTerminatorEvent
  {
    /**
     * Entity that represents the returning method. For in-model targets, {@code Value} is a a
     * reference value.
     */
    public IValue returnContext();

    /**
     * The value returned by the method, if it is known, or an {@code UninitializedValue} otherwise.
     * If the method's return type is <tt>void</tt>, the result of this call is meaningless. For the
     * time being, this is only useful for querying method return.
     */
    public IValue returnValue();
  }

  public interface IMethodReturnedEvent extends IJiveEvent
  {
    public IMethodTerminatorEvent terminator();
  }

  /**
   * Signals the end of the execution of a method.
   */
  public interface IMethodTerminatorEvent extends ITerminatorEvent
  {
    /**
     * Indicates whether the terminator causes a frame to be popped. Method returns always return
     * true, whereas an exception throw only returns true if the exception was not handled locally
     * within its execution context.
     */
    public boolean framePopped();

    /**
     * Event that identifies the method call execution context within which this event occurred.
     */
    @Override
    public IMethodCallEvent parent();
  }

  /**
   * Events occurring as part of the JVM thread processing. As of this time (June/12) this method is
   * only relevant to Fiji.
   * 
   */
  public interface IMonitorEvent extends IRealTimeEvent
  {
    /**
     * Monitor hexadecimal identifier on which the event occurred.
     */
    public String monitor();
  }

  /**
   * An event corresponding to the creation of an object. When an object is created, contours are
   * introduced for it and all of its superclass objects.
   */
  public interface INewObjectEvent extends IJiveEvent
  {
    /**
     * Innermost contour created in response to this event. Traversing the parent axis provides all
     * instance contours created for this object.
     */
    public IObjectContour newContour();
  }

  public interface INewThreadEvent extends INewObjectEvent, IRealTimeEvent
  {
    public long newThreadId();
  }

  public interface IRealTimeEvent
  {
    public long timestamp();
  }

  public interface IRealTimeThreadEvent extends IRealTimeEvent
  {
    public int priority();

    public String scheduler();
  }

  public interface IRTDestroyObjectEvent extends IDestroyObjectEvent, IRealTimeEvent
  {
  }

  public interface IScopeAllocEvent extends IScopeEvent
  {
    public boolean isImmortal();

    public int size();
  }

  public interface IScopeAssignEvent extends IScopeEvent
  {
    public int indexLHS();

    public int indexRHS();

    public long lhs();

    public long rhs();

    public String scopeRHS();
  }

  public interface IScopeBackingAllocEvent extends IRealTimeEvent
  {
    public int size();
  }

  public interface IScopeBackingFreeEvent extends IRealTimeEvent
  {
  }

  public interface IScopeEvent extends IRealTimeEvent
  {
    public String scope();
  }

  /**
   * Termination of a virtual machine instance.
   */
  public interface ISystemExitEvent extends ITerminatorEvent
  {
  }

  /**
   * Start of a virtual machine instance on which the subject program executes.
   */
  public interface ISystemStartEvent extends IInitiatorEvent
  {
    /**
     * Thread start events that executed in the context of the system execution.
     */
    @Override
    public List<IThreadStartEvent> nestedInitiators();

    /**
     * System exit event that marks the end of the execution.
     */
    @Override
    public ISystemExitEvent terminator();
  }

  /**
   * Signals the termination of an execution unit.
   */
  public interface ITerminatorEvent extends IJiveEvent
  {
  }

  /**
   * Signals the termination of a thread execution.
   */
  public interface IThreadEndEvent extends ITerminatorEvent
  {
  }

  /**
   * Start of a thread execution.
   */
  public interface IThreadStartEvent extends IInitiatorEvent
  {
    /**
     * List of children method executions occurring in the context of this execution.
     */
    @Override
    public List<IMethodCallEvent> nestedInitiators();

    /**
     * Thread end event that marks the end of this thread's execution.
     */
    @Override
    public IThreadEndEvent terminator();
  }

  public interface IThreadTimedEvent extends IRealTimeEvent
  {
    public long wakeTime();
  }

  /**
   * A container that encapsulates a list of atomic changes associated with an event. A transaction
   * can be in one of two states, committed or uncommitted, which can be used by clients to replay
   * the state of executions.
   */
  public interface ITransaction
  {
    public List<IStateChange> changes();

    public boolean isCommitted();
  }

  /**
   * An event corresponding to a class' loading. A load event may be fired for one class' loading,
   * or it can represent the loading of a set of classes. For example, the first class loaded
   * usually has the <tt>main</tt> method in it; this means that all ancestors of the class are also
   * loaded and hence get static contours.
   */
  public interface ITypeLoadEvent extends IJiveEvent
  {
    /**
     * Innermost contour created in response to this event. Traversing the parent axis provides all
     * static contours containing this contour.
     */
    public IContextContour newContour();
  }

  /**
   * Local variable assignment.
   */
  public interface IVarAssignEvent extends IAssignEvent
  {
    @Override
    public IMethodContour contour();
  }

  /**
   * Signals that a local variable is deleted from scope.
   */
  public interface IVarDeleteEvent extends IDataEvent
  {
  }

  public enum LockOperation
  {
    LOCK_ACQUIRE("ACQUIRE"),
    LOCK_RELEASE("RELEASE"),
    LOCK_WAIT("WAITING");
    private final String lock;

    private LockOperation(final String lock)
    {
      this.lock = lock;
    }

    @Override
    public String toString()
    {
      return lock;
    }
  }

  public enum OutputFormat
  {
    OUT_CSV,
    OUT_STRING,
    OUT_XML;
  }
}
