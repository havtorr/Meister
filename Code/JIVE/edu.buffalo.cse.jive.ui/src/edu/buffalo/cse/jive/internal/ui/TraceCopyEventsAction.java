package edu.buffalo.cse.jive.internal.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;

import edu.buffalo.cse.jive.model.IEventModel.IJiveEvent;
import edu.buffalo.cse.jive.model.IEventModel.OutputFormat;
import edu.buffalo.cse.jive.ui.IUpdatableAction;

/**
 * Copies selected events to the clipboard in the requested form.
 */
final class TraceCopyEventsAction extends Action implements IUpdatableAction
{
  private final boolean isXML;
  private final StructuredViewer viewer;

  TraceCopyEventsAction(final StructuredViewer viewer, final boolean isXML)
  {
    super(isXML ? "Copy as XML" : "Copy as CSV");
    this.viewer = viewer;
    this.isXML = isXML;
  }

  @Override
  public void run()
  {
    final StringBuilder sb = buildString(getSelection(viewer));
    final TextTransfer textTransfer = TextTransfer.getInstance();
    final Clipboard cb = new Clipboard(Display.getDefault());
    cb.setContents(new Object[]
    { sb.toString() }, new Transfer[]
    { textTransfer });
  }

  @Override
  public void update()
  {
    setEnabled(checkEnabled());
  }

  private StringBuilder buildString(final List<IJiveEvent> eventList)
  {
    final StringBuilder sb = new StringBuilder();
    if (!eventList.isEmpty())
    {
      if (isXML)
      {
        sb.append("<events>");
      }
      final OutputFormat format = isXML ? OutputFormat.OUT_XML : OutputFormat.OUT_CSV;
      for (final IJiveEvent event : eventList)
      {
        sb.append(event.details(format));
      }
      if (isXML)
      {
        sb.append("</events>");
      }
    }
    return sb;
  }

  private boolean checkEnabled()
  {
    final ISelection selection = viewer.getSelection();
    return selection != null && !selection.isEmpty();
  }

  private List<IJiveEvent> getSelection(final StructuredViewer viewer)
  {
    final ISelection selection = viewer.getSelection();
    final List<IJiveEvent> result = new ArrayList<IJiveEvent>();
    if (selection != null && selection instanceof IStructuredSelection)
    {
      final IStructuredSelection sel = (IStructuredSelection) selection;
      for (@SuppressWarnings("unchecked")
      final Iterator<IJiveEvent> iterator = sel.iterator(); iterator.hasNext();)
      {
        result.add(iterator.next());
      }
    }
    return result;
  }
}
