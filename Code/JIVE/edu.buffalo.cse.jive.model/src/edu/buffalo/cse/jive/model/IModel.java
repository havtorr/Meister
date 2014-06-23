package edu.buffalo.cse.jive.model;

import edu.buffalo.cse.jive.model.IContourModel.IContour;
import edu.buffalo.cse.jive.model.IContourModel.IMethodContour;

/**
 * Base interface for all model elements in Jive's model.
 */
public interface IModel
{
  /**
   * Execution model to which the model element is associated.
   */
  public IExecutionModel model();

  /**
   * In-model reference, that is, a reference to a known model environment.
   */
  public interface IContourReference extends IInModelValue
  {
    /**
     * Identifier of the contour referenced by the value.
     */
    public IContour contour();
  }

  /**
   * Represents a file in the source code.
   */
  public interface IFileValue extends IModel
  {
    /**
     * Unique value identifier.
     */
    public long id();

    public String name();
  }

  /**
   * In-model value that may represent a primitive value or an in-model reference value.
   */
  public interface IInModelValue extends IValue
  {
  }

  /**
   * Represents a line in the source code.
   */
  public interface ILineValue extends IModel
  {
    public IFileValue file();

    /**
     * Unique value identifier.
     */
    public long id();

    public int lineNumber();
  }

  /**
   * Represents an in-model call target.
   */
  public interface IMethodContourReference extends IContourReference
  {
    /**
     * Actual method environment created as a result of the underlying method call.
     */
    @Override
    public IMethodContour contour();
  }

  /**
   * Represents an out-of-model call target. The key value represents the method signature of the
   * target method.
   */
  public interface IOutOfModelMethodKeyReference extends IOutOfModelValue
  {
    public String key();
  }

  /**
   * Represents an out-of-model call target.
   */
  public interface IOutOfModelMethodReference extends IOutOfModelValue
  {
    /**
     * Reference to the top in-model method contour at the time of the call to this method.
     */
    public IMethodContour method();
  }

  /**
   * Out-of-model value with a default literal representation.
   */
  public interface IOutOfModelValue extends IValue
  {
  }

  /**
   * Out-of-model value whose literal representation is resolved.
   */
  public interface IResolvedValue extends IOutOfModelValue
  {
    public String typeName();
  }

  /**
   * A thread identifier within the execution model.
   */
  public interface IThreadValue extends IModel
  {
    /**
     * Unique value identifier.
     */
    public long id();

    /**
     * Name of the thread, not necessarily unique or constant.
     */
    public String name();
  }

  /**
   * Representation of program values within the model. Values for variables may be encoded
   * literals, references to contours, references outside of the model, etc. Values for other
   * elements of contours' member tables, which are methods and inner classes, are the definitions
   * of those methods and inner classes.
   * 
   * Values that can be associated with a contour member. This includes fields, variables, and the
   * special RPDL variable.
   */
  public interface IValue extends IModel
  {
    /**
     * Unique value identifier.
     */
    public long id();

    public boolean isContourReference();

    public boolean isGarbageCollected(long eventId);

    public boolean isInModel();

    public boolean isMethodContourReference();

    public boolean isNull();

    public boolean isOutOfModel();

    public boolean isOutOfModelMethodKeyReference();

    public boolean isOutOfModelMethodReference();

    public boolean isPrimitive();

    public boolean isResolved();

    public boolean isUninitialized();

    public ValueKind kind();

    /**
     * String representation of this assignable value.
     */
    public String value();
  }

  public enum ValueKind
  {
    IM_CONTOUR_REFERENCE,
    IM_METHOD_CONTOUR_REFERENCE,
    IM_PRIMITIVE,
    NULL,
    OM_METHOD_KEY_REFERENCE,
    OM_METHOD_REFERENCE,
    OM_RESOLVED,
    OUT_OF_MODEL,
    SYSTEM_CALLER,
    UNINITIALIZED,
  }
}