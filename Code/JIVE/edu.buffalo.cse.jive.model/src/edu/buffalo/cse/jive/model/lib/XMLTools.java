package edu.buffalo.cse.jive.model.lib;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.buffalo.cse.jive.model.IEventModel.IAssignEvent;
import edu.buffalo.cse.jive.model.IEventModel.IExceptionCatchEvent;
import edu.buffalo.cse.jive.model.IEventModel.IExceptionThrowEvent;
import edu.buffalo.cse.jive.model.IEventModel.IFieldReadEvent;
import edu.buffalo.cse.jive.model.IEventModel.IJiveEvent;
import edu.buffalo.cse.jive.model.IEventModel.ILockEvent;
import edu.buffalo.cse.jive.model.IEventModel.IMethodCallEvent;
import edu.buffalo.cse.jive.model.IEventModel.IMethodExitEvent;
import edu.buffalo.cse.jive.model.IEventModel.INewObjectEvent;
import edu.buffalo.cse.jive.model.IEventModel.ITypeLoadEvent;
import edu.buffalo.cse.jive.model.IEventModel.IVarDeleteEvent;
import edu.buffalo.cse.jive.model.IStaticAnalysis.IMethodDependenceGraph;
import edu.buffalo.cse.jive.model.IStaticAnalysis.IResolvedCall;
import edu.buffalo.cse.jive.model.IStaticAnalysis.IResolvedData;
import edu.buffalo.cse.jive.model.IStaticAnalysis.IResolvedLine;
import edu.buffalo.cse.jive.model.IStaticAnalysis.IResolvedNode;
import edu.buffalo.cse.jive.model.IStaticAnalysis.IResolvedThis;
import edu.buffalo.cse.jive.model.IStaticModel.IDataNode;
import edu.buffalo.cse.jive.model.IStaticModel.IFileNode;
import edu.buffalo.cse.jive.model.IStaticModel.IMethodNode;
import edu.buffalo.cse.jive.model.IStaticModel.INode;
import edu.buffalo.cse.jive.model.IStaticModel.IRootNode;
import edu.buffalo.cse.jive.model.IStaticModel.ITypeNode;
import edu.buffalo.cse.jive.model.IStaticModel.ITypeNodeRef;
import edu.buffalo.cse.jive.model.IStaticModel.NodeKind;
import edu.buffalo.cse.jive.model.IStaticModel.NodeModifier;
import edu.buffalo.cse.jive.model.IStaticModel.NodeOrigin;
import edu.buffalo.cse.jive.model.IStaticModel.NodeVisibility;

public class XMLTools
{
  public static String eventToXML(final IJiveEvent event)
  {
    final StringBuffer buffer = new StringBuffer(XML.tagOpen("event"));
    EventXML.eventToXML(buffer, event);
    buffer.append(XML.tagOpen("details"));
    if (event instanceof IAssignEvent)
    {
      EventXML.assignToXML(buffer, (IAssignEvent) event);
    }
    else if (event instanceof IExceptionCatchEvent)
    {
      EventXML.exceptionCatchToXML(buffer, (IExceptionCatchEvent) event);
    }
    else if (event instanceof IExceptionThrowEvent)
    {
      EventXML.exceptionThrowToXML(buffer, (IExceptionThrowEvent) event);
    }
    else if (event instanceof IFieldReadEvent)
    {
      EventXML.fieldReadToXML(buffer, (IFieldReadEvent) event);
    }
    else if (event instanceof ILockEvent)
    {
      EventXML.lockToXML(buffer, (ILockEvent) event);
    }
    else if (event instanceof IMethodCallEvent)
    {
      EventXML.methodCallToXML(buffer, (IMethodCallEvent) event);
    }
    else if (event instanceof IMethodExitEvent)
    {
      EventXML.methodExitToXML(buffer, (IMethodExitEvent) event);
    }
    else if (event instanceof INewObjectEvent)
    {
      EventXML.newObjectToXML(buffer, (INewObjectEvent) event);
    }
    else if (event instanceof ITypeLoadEvent)
    {
      EventXML.typeLoadToXML(buffer, (ITypeLoadEvent) event);
    }
    else if (event instanceof IVarDeleteEvent)
    {
      EventXML.varDeleteToXML(buffer, (IVarDeleteEvent) event);
    }
    buffer.append(XML.tagClose("details"));
    buffer.append(XML.tagClose("event"));
    return buffer.toString();
  }

  public static String javaDependenceGraph(final IRootNode root)
  {
    final Map<String, String> attributes = Tools.newLinkedHashMap();
    final StringBuffer buffer = new StringBuffer("");
    buffer.append(XML.tagOpen("jdg"));
    for (final IFileNode fn : root.files())
    {
      attributes.clear();
      attributes.put("id", String.valueOf(fn.id()));
      attributes.put("name", fn.name());
      buffer.append(XML.tagOpen("file", attributes));
      for (final ITypeNode tn : fn.types())
      {
        XMLTools.typeMDGsToXML(buffer, tn);
      }
      buffer.append(XML.tagClose("file"));
    }
    buffer.append(XML.tagClose("jdg"));
    return buffer.toString();
  }

  public static String nodeToXML(final INode node)
  {
    final StringBuffer buffer = new StringBuffer(XML.tagOpen(NodeXML.nodeName(node)));
    if (node instanceof IRootNode)
    {
      NodeXML.rootToXML(buffer, (IRootNode) node);
    }
    else if (node instanceof IFileNode)
    {
      NodeXML.fileToXML(buffer, (IFileNode) node);
    }
    else if (node instanceof ITypeNode)
    {
      NodeXML.typeToXML(buffer, (ITypeNode) node);
    }
    else if (node instanceof IMethodNode)
    {
      NodeXML.methodToXML(buffer, (IMethodNode) node);
    }
    else if (node instanceof IDataNode)
    {
      NodeXML.dataToXML(buffer, (IDataNode) node);
    }
    else
    {
      NodeXML.nodeToXML(buffer, node);
    }
    buffer.append(XML.tagClose(NodeXML.nodeName(node)));
    return buffer.toString();
  }

  public static String resolvedLineToXML(final StringBuffer buffer, final IResolvedLine line)
  {
    final Map<String, String> attributes = Tools.newLinkedHashMap();
    attributes.put("kind", line.kind().toString());
    attributes.put("line", String.valueOf(line.lineNumber()));
    buffer.append(XML.tagOpen("line", attributes));
    buffer.append(XML.tagOpen("defs"));
    for (final IResolvedData node : line.definitions())
    {
      XMLTools.resolvedNodeToXML(buffer, "def", node, true);
    }
    buffer.append(XML.tagClose("defs"));
    buffer.append(XML.tagOpen("uses"));
    for (final IResolvedNode node : line.uses())
    {
      XMLTools.resolvedNodeToXML(buffer, "use", node, true);
    }
    buffer.append(XML.tagClose("uses"));
    final List<IResolvedLine> inheritedSet = Tools.newArrayList();
    IResolvedLine inherited = line.parent();
    while (inherited != null)
    {
      inheritedSet.add(inherited);
      inherited = inherited.parent();
    }
    buffer.append(XML.tagOpen("inherited"));
    for (final IResolvedLine node : inheritedSet)
    {
      attributes.clear();
      attributes.put("kind", node.kind().toString());
      attributes.put("line", String.valueOf(node.lineNumber()));
      buffer.append(XML.tagOpen("line", attributes));
      buffer.append(XML.tagClose("line"));
    }
    buffer.append(XML.tagClose("inherited"));
    buffer.append(XML.tagOpen("propagated"));
    for (final IResolvedLine node : line.jumpDependences())
    {
      attributes.clear();
      attributes.put("kind", node.kind().toString());
      attributes.put("line", String.valueOf(node.lineNumber()));
      buffer.append(XML.tagOpen("line", attributes));
      buffer.append(XML.tagClose("line"));
    }
    buffer.append(XML.tagClose("propagated"));
    buffer.append(XML.tagClose("line"));
    return buffer.toString();
  }

  private static void argumentPositionToXML(final StringBuffer buffer, final IResolvedCall call,
      final int index)
  {
    buffer.append(XML.tagOpen("vars"));
    for (final IResolvedNode rv : call.uses(index))
    {
      if (rv instanceof IResolvedData)
      {
        XMLTools.resolvedNodeToXML(buffer, "var", rv, false);
      }
    }
    buffer.append(XML.tagClose("vars"));
    buffer.append(XML.tagOpen("calls"));
    for (final IResolvedNode rc : call.uses(index))
    {
      if (rc instanceof IResolvedCall)
      {
        XMLTools.resolvedNodeToXML(buffer, "call", rc, false);
      }
    }
    buffer.append(XML.tagClose("calls"));
  }

  @SuppressWarnings("unchecked")
  private static void mdgToXML(final StringBuffer buffer, final IMethodNode method)
  {
    final Map<String, String> attributes = Tools.newLinkedHashMap();
    attributes.put("id", String.valueOf(method.id()));
    attributes.put("method", method.name());
    buffer.append(XML.tagOpen("mdg", attributes));
    final IMethodDependenceGraph mdg = method.getDependenceGraph();
    final List<Integer> allLines = mdg != null ? new ArrayList<Integer>(mdg.dependenceMap()
        .keySet()) : (List<Integer>) Collections.EMPTY_LIST;
    Collections.sort(allLines);
    for (final Integer key : allLines)
    {
      XMLTools.resolvedLineToXML(buffer, mdg.dependenceMap().get(key));
    }
    buffer.append(XML.tagClose("mdg"));
  }

  private static void resolvedNodeToXML(final StringBuffer buffer, final String tagName,
      final IResolvedNode node, final boolean exposeArguments)
  {
    final Map<String, String> attributes = Tools.newLinkedHashMap();
    attributes.put("index", String.valueOf(node.sourceIndex()));
    attributes.put("isLHS", String.valueOf(node.isLHS()));
    attributes.put("isActual", String.valueOf(node.isActual()));
    if (node.qualifierOf() != null)
    {
      attributes.put("qualifierOf", String.valueOf(node.qualifierOf().sourceIndex()));
    }
    if (node instanceof IResolvedCall)
    {
      final IResolvedCall call = (IResolvedCall) node;
      attributes.put("kind", "call");
      if (call.call().node() != null)
      {
        attributes.put("id", String.valueOf(call.call().node().id()));
      }
      attributes.put("method", call.methodName());
      buffer.append(XML.tagOpen(tagName, attributes));
      if (exposeArguments)
      {
        buffer.append(XML.tagOpen("arguments"));
        for (int i = 0; i < call.size(); i++)
        {
          attributes.clear();
          attributes.put("index", String.valueOf(i));
          buffer.append(XML.tagOpen("pos", attributes));
          XMLTools.argumentPositionToXML(buffer, call, i);
          buffer.append(XML.tagClose("pos"));
        }
        buffer.append(XML.tagClose("arguments"));
      }
    }
    else if (node instanceof IResolvedData)
    {
      final IResolvedData data = (IResolvedData) node;
      attributes.put("kind",
          data.data() == null ? "lazy field" : data.data().kind() == NodeKind.NK_FIELD ? "field"
              : (data.data().modifiers().contains(NodeModifier.NM_ARGUMENT) ? "argument"
                  : "variable"));
      if (data.data() != null)
      {
        attributes.put("id", String.valueOf(data.data().id()));
      }
      attributes.put("isDef", String.valueOf(data.isDef()));
      attributes.put("name", data.name());
      if (data.data() != null && data.data().kind() == NodeKind.NK_FIELD)
      {
        attributes.put("declaration", data.data().parent().name());
      }
      buffer.append(XML.tagOpen(tagName, attributes));
    }
    else if (node instanceof IResolvedThis)
    {
      final IResolvedThis rthis = (IResolvedThis) node;
      attributes.put("kind", "this");
      attributes.put("id", String.valueOf(rthis.type().id()));
      attributes.put("type", rthis.type().name());
      buffer.append(XML.tagOpen(tagName, attributes));
    }
    buffer.append(XML.tagClose(tagName));
  }

  private static void typeMDGsToXML(final StringBuffer buffer, final ITypeNode tn)
  {
    final Map<String, String> attributes = Tools.newLinkedHashMap();
    attributes.put("id", String.valueOf(tn.id()));
    attributes.put("name", tn.name());
    buffer.append(XML.tagOpen("type", attributes));
    if (tn.origin() == NodeOrigin.NO_AST)
    {
      for (final IMethodNode method : tn.methodMembers().values())
      {
        if (method.origin() == NodeOrigin.NO_AST)
        {
          XMLTools.mdgToXML(buffer, method);
        }
      }
    }
    // nested member types
    for (final ITypeNode ntn : tn.typeMembers())
    {
      XMLTools.typeMDGsToXML(buffer, ntn);
    }
    buffer.append(XML.tagClose("type"));
  }

  public static enum XMLEventField
  {
    CALLER("caller"),
    CATCHER("catcher"),
    CONTEXT("context"),
    ELEMENTS("elements"),
    EXCEPTION("exception"),
    FIELD("field"),
    FILE("file"),
    FRAME_POPPED("framePopped"),
    ID("id"),
    IMMORTAL("immortal"),
    INLINE("inline"),
    KIND("kind"),
    LINE("line"),
    LOCK("lock"),
    MONITOR("monitor"),
    NEWTHREAD("newthread"),
    OBJECT("object"),
    OPERATION("operation"),
    PRIORITY("priority"),
    RETURNER("returner"),
    SCHEDULER("scheduler"),
    SCOPE("scope"),
    SIZE("size"),
    SIGNATURE("signature"),
    TARGET("target"),
    THREAD("thread"),
    THROWER("thrower"),
    TIMESTAMP("timestamp"),
    TYPE("type"),
    VALUE("value"),
    VARIABLE("variable"),
    WAKETIME("waketime");
    private final String name;

    private XMLEventField(final String name)
    {
      this.name = name;
    }

    public String fieldName()
    {
      return name;
    }

    @Override
    public String toString()
    {
      return name;
    }
  }

  private static final class EventXML
  {
    public static void assignToXML(final StringBuffer buffer, final IAssignEvent event)
    {
      buffer.append(XML.tagOpen(XMLEventField.CONTEXT.fieldName()));
      buffer.append(XML.CData(event.contour().signature()));
      buffer.append(XML.tagClose(XMLEventField.CONTEXT.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.VARIABLE.fieldName()));
      buffer.append(XML.CData(event.member().name()));
      buffer.append(XML.tagClose(XMLEventField.VARIABLE.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.VALUE.fieldName()));
      buffer.append(XML.CData(event.newValue().toString()));
      buffer.append(XML.tagClose(XMLEventField.VALUE.fieldName()));
    }

    public static void exceptionCatchToXML(final StringBuffer buffer,
        final IExceptionCatchEvent event)
    {
      buffer.append(XML.tagOpen(XMLEventField.EXCEPTION.fieldName()));
      buffer.append(XML.CData(event.exception().toString()));
      buffer.append(XML.tagClose(XMLEventField.EXCEPTION.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.CATCHER.fieldName()));
      if (event.contour() != null)
      {
        buffer.append(XML.CData(event.contour().signature()));
      }
      buffer.append(XML.tagClose(XML.tagOpen(XMLEventField.CATCHER.fieldName())));
      buffer.append(XML.tagOpen(XML.tagOpen(XMLEventField.VARIABLE.fieldName())));
      if (event.member() != null)
      {
        buffer.append(XML.CData(event.member().schema().name()));
      }
      buffer.append(XML.tagClose(XMLEventField.VARIABLE.fieldName()));
    }

    public static void exceptionThrowToXML(final StringBuffer buffer,
        final IExceptionThrowEvent event)
    {
      buffer.append(XML.tagOpen(XMLEventField.EXCEPTION.fieldName()));
      buffer.append(XML.CData(event.exception().toString()));
      buffer.append(XML.tagClose(XMLEventField.EXCEPTION.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.THROWER.fieldName()));
      buffer.append(XML.CData(event.thrower().toString()));
      buffer.append(XML.tagClose(XMLEventField.THROWER.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.FRAME_POPPED.fieldName()));
      buffer.append(XML.CData(String.valueOf(event.framePopped())));
      buffer.append(XML.tagClose(XMLEventField.FRAME_POPPED.fieldName()));
    }

    public static void fieldReadToXML(final StringBuffer buffer, final IFieldReadEvent event)
    {
      buffer.append(XML.tagOpen(XMLEventField.CONTEXT.fieldName()));
      buffer.append(XML.CData(event.contour().signature()));
      buffer.append(XML.tagClose(XMLEventField.CONTEXT.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.VARIABLE.fieldName()));
      buffer.append(XML.CData(event.member().schema().name()));
      buffer.append(XML.tagClose(XMLEventField.VARIABLE.fieldName()));
    }

    public static void lockToXML(final StringBuffer buffer, final ILockEvent event)
    {
      buffer.append(XML.tagOpen(XMLEventField.OPERATION.fieldName()));
      buffer.append(XML.CData(event.lockOperation().toString()));
      buffer.append(XML.tagClose(XMLEventField.OPERATION.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.LOCK.fieldName()));
      buffer.append(XML.CData(event.lockDescription()));
      buffer.append(XML.tagClose(XMLEventField.LOCK.fieldName()));
    }

    public static void methodCallToXML(final StringBuffer buffer, final IMethodCallEvent event)
    {
      buffer.append(XML.tagOpen(XMLEventField.CALLER.fieldName()));
      buffer.append(XML.CData(event.caller().toString()));
      buffer.append(XML.tagClose(XMLEventField.CALLER.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.TARGET.fieldName()));
      buffer.append(XML.CData(event.target().toString()));
      buffer.append(XML.tagClose(XMLEventField.TARGET.fieldName()));
    }

    public static void methodExitToXML(final StringBuffer buffer, final IMethodExitEvent event)
    {
      buffer.append(XML.tagOpen(XMLEventField.RETURNER.fieldName()));
      buffer.append(XML.CData(event.returnContext().toString()));
      buffer.append(XML.tagClose(XMLEventField.RETURNER.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.VALUE.fieldName()));
      if (event.returnValue() != null && !event.returnValue().isUninitialized())
      {
        buffer.append(XML.CData(event.returnValue().toString()));
      }
      buffer.append(XML.tagClose(XMLEventField.VALUE.fieldName()));
    }

    public static void newObjectToXML(final StringBuffer buffer, final INewObjectEvent event)
    {
      buffer.append(XML.tagOpen(XMLEventField.OBJECT.fieldName()));
      buffer.append(XML.CData(event.newContour().signature()));
      buffer.append(XML.tagClose(XMLEventField.OBJECT.fieldName()));
    }

    public static void typeLoadToXML(final StringBuffer buffer, final ITypeLoadEvent event)
    {
      buffer.append(XML.tagOpen(XMLEventField.TYPE.fieldName()));
      buffer.append(XML.CData(event.newContour().signature()));
      buffer.append(XML.tagClose(XMLEventField.TYPE.fieldName()));
    }

    public static void varDeleteToXML(final StringBuffer buffer, final IVarDeleteEvent event)
    {
      buffer.append(XML.tagOpen(XMLEventField.CONTEXT.fieldName()));
      buffer.append(XML.CData(event.contour().signature()));
      buffer.append(XML.tagClose(XMLEventField.CONTEXT.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.VARIABLE.fieldName()));
      buffer.append(XML.CData(event.member().schema().name()));
      buffer.append(XML.tagClose(XMLEventField.VARIABLE.fieldName()));
    }

    private static void eventToXML(final StringBuffer buffer, final IJiveEvent event)
    {
      buffer.append(XML.tagOpen(XMLEventField.ID.fieldName()));
      buffer.append(XML.PCData(event.eventId()));
      buffer.append(XML.tagClose(XMLEventField.ID.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.THREAD.fieldName()));
      buffer.append(XML.CData(event.thread().name()));
      buffer.append(XML.tagClose(XMLEventField.THREAD.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.KIND.fieldName()));
      buffer.append(XML.CData(event.kind().toString()));
      buffer.append(XML.tagClose(XMLEventField.KIND.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.FILE.fieldName()));
      buffer.append(XML.CData(event.line().file().name()));
      buffer.append(XML.tagClose(XMLEventField.FILE.fieldName()));
      buffer.append(XML.tagOpen(XMLEventField.LINE.fieldName()));
      buffer.append(XML.PCData(event.line().lineNumber()));
      buffer.append(XML.tagClose(XMLEventField.LINE.fieldName()));
    }
  }

  private static final class NodeXML
  {
    private static void dataToXML(final StringBuffer buffer, final IDataNode node)
    {
      // common
      NodeXML.nodeToXML(buffer, node);
      buffer.append(XML.tagOpen("defaultValue"));
      buffer.append(XML.CData(node.defaultValue().toString()));
      buffer.append(XML.tagClose("defaultValue"));
      buffer.append(XML.tagOpen("index"));
      buffer.append(XML.PCData(node.index()));
      buffer.append(XML.tagClose("index"));
      NodeXML.xmlNodeReference(buffer, "type", node.type().node());
    }

    private static void fileToXML(final StringBuffer buffer, final IFileNode node)
    {
      // common
      NodeXML.nodeToXML(buffer, node);
      buffer.append(XML.tagOpen("types"));
      NodeXML.xmlTypes(buffer, node.types());
      buffer.append(XML.tagClose("types"));
    }

    private static void methodToXML(final StringBuffer buffer, final IMethodNode node)
    {
      // common
      NodeXML.nodeToXML(buffer, node);
      buffer.append(XML.tagOpen("localVariables"));
      for (final Integer variableId : node.dataMembers().keySet())
      {
        buffer.append(XML.tagOpen("variable"));
        NodeXML.nodeToXML(buffer, node.dataMembers().get(variableId));
        buffer.append(XML.tagClose("variable"));
      }
      buffer.append(XML.tagClose("localVariables"));
      buffer.append(XML.tagOpen("localTypes"));
      NodeXML.xmlTypes(buffer, node.localTypes());
      buffer.append(XML.tagClose("localTypes"));
    }

    private static String nodeModifier(final NodeModifier modifier)
    {
      return modifier.name().substring(3).toLowerCase();
    }

    private static String nodeName(final INode node)
    {
      return node.kind().name().substring(3).toLowerCase();
    }

    private static String nodeOrigin(final NodeOrigin origin)
    {
      return origin.name().substring(3).toLowerCase();
    }

    private static void nodeToXML(final StringBuffer buffer, final INode node)
    {
      buffer.append(XML.tagOpen("id"));
      buffer.append(XML.PCData(node.id()));
      buffer.append(XML.tagClose("id"));
      buffer.append(XML.tagOpen("name"));
      buffer.append(XML.CData(node.name()));
      buffer.append(XML.tagClose("name"));
      buffer.append(XML.tagOpen("origin"));
      buffer.append(XML.CData(NodeXML.nodeOrigin(node.origin())));
      buffer.append(XML.tagClose("origin"));
      buffer.append(XML.tagOpen("visibility"));
      buffer.append(XML.CData(NodeXML.nodeVisibility(node.visibility())));
      buffer.append(XML.tagClose("visibility"));
      if (node.lineFrom() == -1 || node.lineTo() == -1)
      {
        buffer.append(XML.tagOpen("lines"));
        buffer.append(XML.tagClose("lines"));
      }
      else
      {
        buffer.append(XML.tagOpen("lines"));
        buffer.append(XML.tagOpen("from"));
        buffer.append(XML.PCData(node.lineFrom()));
        buffer.append(XML.tagClose("from"));
        buffer.append(XML.tagOpen("to"));
        buffer.append(XML.PCData(node.lineTo()));
        buffer.append(XML.tagClose("to"));
        buffer.append(XML.tagClose("lines"));
      }
      buffer.append(XML.tagOpen("modifiers"));
      for (final NodeModifier modifier : node.modifiers())
      {
        buffer.append(XML.tagOpen("modifier"));
        buffer.append(XML.CData(NodeXML.nodeModifier(modifier)));
        buffer.append(XML.tagClose("modifier"));
      }
      buffer.append(XML.tagClose("modifiers"));
    }

    private static String nodeVisibility(final NodeVisibility visibility)
    {
      return visibility.name().substring(3).toLowerCase();
    }

    private static void rootToXML(final StringBuffer buffer, final IRootNode node)
    {
      // common
      NodeXML.nodeToXML(buffer, node);
      buffer.append(XML.tagOpen("types"));
      // primitives
      buffer.append(XML.tagOpen("primitives"));
      for (final ITypeNode type : node.types())
      {
        if (type.kind() == NodeKind.NK_PRIMITIVE)
        {
          NodeXML.xmlNodeReference(buffer, "primitive", type);
        }
      }
      buffer.append(XML.tagClose("primitives"));
      // arrays
      buffer.append(XML.tagOpen("arrays"));
      for (final ITypeNode type : node.types())
      {
        if (type.kind() == NodeKind.NK_ARRAY)
        {
          NodeXML.xmlNodeReference(buffer, "array", type);
        }
      }
      buffer.append(XML.tagClose("arrays"));
      // classes and interfaces
      NodeXML.xmlTypes(buffer, node.types());
      buffer.append(XML.tagClose("types"));
      buffer.append(XML.tagOpen("files"));
      for (final IFileNode file : node.files())
      {
        buffer.append(XML.tagOpen("file"));
        NodeXML.fileToXML(buffer, file);
        buffer.append(XML.tagClose("file"));
      }
      buffer.append(XML.tagClose("files"));
    }

    private static void typeToXML(final StringBuffer buffer, final ITypeNode node)
    {
      // common
      NodeXML.nodeToXML(buffer, node);
      buffer.append(XML.tagOpen("defaultValue"));
      buffer.append(XML.CData(node.defaultValue().toString()));
      buffer.append(XML.tagClose("defaultValue"));
      NodeXML.xmlNodeReference(buffer, "superClass", node.superClass() != null ? node.superClass()
          .node() : null);
      // open members
      buffer.append(XML.tagOpen("members"));
      // field members
      buffer.append(XML.tagOpen("fields"));
      for (final Integer fieldId : node.dataMembers().keySet())
      {
        buffer.append(XML.tagOpen("field"));
        NodeXML.nodeToXML(buffer, node.dataMembers().get(fieldId));
        buffer.append(XML.tagClose("field"));
      }
      buffer.append(XML.tagClose("fields"));
      // method members
      buffer.append(XML.tagOpen("methods"));
      for (final Integer methodId : node.methodMembers().keySet())
      {
        buffer.append(XML.tagOpen("method"));
        NodeXML.nodeToXML(buffer, node.methodMembers().get(methodId));
        buffer.append(XML.tagClose("method"));
      }
      buffer.append(XML.tagClose("methods"));
      // type members
      buffer.append(XML.tagOpen("types"));
      for (final ITypeNode type : node.typeMembers())
      {
        NodeXML.xmlNodeReference(buffer, "type", type);
      }
      buffer.append(XML.tagClose("types"));
      // close members
      buffer.append(XML.tagClose("members"));
      buffer.append(XML.tagOpen("implements"));
      for (final ITypeNodeRef type : node.superInterfaces())
      {
        NodeXML.xmlNodeReference(buffer, "interface", type.node());
      }
      buffer.append(XML.tagClose("implements"));
    }

    private static void xmlNodeReference(final StringBuffer buffer, final String tagName,
        final INode node)
    {
      buffer.append(XML.tagOpen(tagName));
      if (node != null)
      {
        buffer.append(XML.tagOpen("id"));
        buffer.append(XML.PCData(node.id()));
        buffer.append(XML.tagClose("id"));
        buffer.append(XML.tagOpen("name"));
        buffer.append(XML.CData(node.name()));
        buffer.append(XML.tagClose("name"));
      }
      buffer.append(XML.tagClose(tagName));
    }

    private static void xmlTypes(final StringBuffer buffer, final Set<ITypeNode> types)
    {
      buffer.append(XML.tagOpen("classes"));
      for (final ITypeNode type : types)
      {
        if (type.kind() == NodeKind.NK_CLASS)
        {
          buffer.append(XML.tagOpen("class"));
          NodeXML.typeToXML(buffer, type);
          buffer.append(XML.tagClose("class"));
        }
      }
      buffer.append(XML.tagClose("classes"));
      buffer.append(XML.tagOpen("interfaces"));
      for (final ITypeNode type : types)
      {
        if (type.kind() == NodeKind.NK_INTERFACE)
        {
          buffer.append(XML.tagOpen("interface"));
          NodeXML.typeToXML(buffer, type);
          buffer.append(XML.tagClose("interface"));
        }
      }
      buffer.append(XML.tagClose("interfaces"));
    }
  }

  private static final class XML
  {
    public static String tagOpen(final String tagName, final Map<String, String> attributes)
    {
      final StringBuffer attrs = new StringBuffer("");
      for (final String key : attributes.keySet())
      {
        attrs.append(" ").append(key).append("=\"").append(XML.CData(attributes.get(key)))
            .append("\"");
      }
      return "<" + tagName + attrs.toString() + ">";
    }

    private static String CData(final String value)
    {
      return XML.escape(value);
    }

    private static String escape(final String value)
    {
      // ![CDATA[%s]]
      String result = "";
      for (final char ch : value.toCharArray())
      {
        if (ch == '&')
        {
          result += "&amp;";
        }
        else if (ch == '<')
        {
          result += "&lt;";
        }
        else if (ch == '>')
        {
          result += "&gt;";
        }
        else if (ch == '"')
        {
          result += "&quot;";
        }
        else
        {
          result += ch;
        }
      }
      return result;
    }

    private static String PCData(final long value)
    {
      return String.valueOf(value);
    }

    private static String tagClose(final String tagName)
    {
      return "</" + tagName + ">";
    }

    private static String tagOpen(final String tagName)
    {
      return "<" + tagName + ">";
    }
  }
}