package edu.buffalo.cse.jive.internal.console;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.buffalo.cse.jive.debug.model.IJiveDebugTarget;
import edu.buffalo.cse.jive.launch.JiveLaunchPlugin;
import edu.buffalo.cse.jive.model.IExecutionModel.IProgramSlice;
import edu.buffalo.cse.jive.model.IStaticModel.IRootNode;
import edu.buffalo.cse.jive.model.lib.StringTools;
import edu.buffalo.cse.jive.model.lib.XMLTools;

public enum ConsoleProxy
{
  INSTANCE;
  public boolean exportAST(final int targetId, final String filePath)
  {
    final IJiveDebugTarget target = JiveLaunchPlugin.getDefault().getLaunchManager()
        .lookupTarget(targetId);
    if (target == null)
    {
      return false;
    }
    FileOutputStream fos = null;
    try
    {
      fos = new FileOutputStream(filePath);
      if (filePath.endsWith("xml"))
      {
        fos.write(target.model().staticModelFactory().lookupRoot().toXML().getBytes());
      }
      else
      {
        fos.write(target.model().staticModelFactory().lookupRoot().toString().getBytes());
      }
      fos.flush();
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (fos != null)
      {
        try
        {
          fos.close();
        }
        catch (final IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    return true;
  }

  public boolean exportMDG(final int targetId, final String filePath)
  {
    if (filePath == null || filePath.trim().length() == 0)
    {
      return false;
    }
    final IJiveDebugTarget target = getTarget(targetId);
    if (target == null)
    {
      return false;
    }
    final IRootNode root = target.model().staticModelFactory().lookupRoot();
    FileOutputStream fos = null;
    try
    {
      fos = new FileOutputStream(filePath);
      if (filePath.endsWith("xml"))
      {
        fos.write(XMLTools.javaDependenceGraph(root).getBytes());
      }
      else
      {
        fos.write(StringTools.javaDependenceGraph(root).getBytes());
      }
      fos.flush();
    }
    catch (final IOException e)
    {
      e.printStackTrace();
    }
    finally
    {
      if (fos != null)
      {
        try
        {
          fos.close();
        }
        catch (final IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    return true;
  }

  public boolean exportTrace(final int targetId, final String exportTarget)
  {
    final IJiveDebugTarget target = getTarget(targetId);
    if (target == null)
    {
      return false;
    }
    target.model().traceView().exportTo(exportTarget);
    return true;
  }

  public void sliceClear(final int targetId)
  {
    final IJiveDebugTarget target = getTarget(targetId);
    if (target == null)
    {
      return;
    }
    target.model().sliceView().clearSlice();
  }

  public IProgramSlice sliceProgram(final int targetId, final int eventId)
  {
    final IJiveDebugTarget target = getTarget(targetId);
    if (target == null)
    {
      return null;
    }
    return target.model().sliceView().computeSlice(eventId);
  }

  public boolean startTarget(final int targetId)
  {
    final IJiveDebugTarget target = getTarget(targetId);
    if (target == null || (target.isStarted() && !target.isStopped()))
    {
      return false;
    }
    // start this target
    target.start();
    // signal that the target is started
    return true;
  }

  public boolean stopTarget(final int targetId)
  {
    final IJiveDebugTarget target = getTarget(targetId);
    if (target == null || target.isStopped())
    {
      return false;
    }
    // stop this target
    target.stop();
    // signal that the target is stopped
    return true;
  }

  public List<String> targetList()
  {
    final Collection<IJiveDebugTarget> targets = JiveLaunchPlugin.getDefault().getLaunchManager()
        .lookupTargets();
    final List<String> result = new ArrayList<String>();
    result.add("  TID   STATUS    CLASS");
    final IJiveDebugTarget active = JiveLaunchPlugin.getDefault().getLaunchManager().activeTarget();
    for (final IJiveDebugTarget t : targets)
    {
      result.add(String.format("%5d%s  %s   %s", t.targetId(), t == active ? "*" : " ",
          (t.isStopped() ? "stopped" : t.isStarted() ? "started" : "manual "), t.getName()));
    }
    return result;
  }

  private IJiveDebugTarget getTarget(final int targetId)
  {
    return JiveLaunchPlugin.getDefault().getLaunchManager().lookupTarget(targetId);
  }
}