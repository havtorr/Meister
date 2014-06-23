package edu.buffalo.cse.jive.console;

import java.util.Arrays;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import edu.buffalo.cse.jive.internal.console.ConsoleProxy;
import edu.buffalo.cse.jive.model.IExecutionModel.IProgramSlice;

public class JiveCommandProvider implements CommandProvider
{
  private static final String CMD_DUMP = "dump";
  private static final String CMD_DUMP_HELP = "Displays detailed information about the specified element in textual form. The 'kind' can be EVENT, CONTOUR, or NODE. The 'id' is the integer identifier of the element.";
  private static final String CMD_DUMP_SYNTAX = JiveCommandProvider.CMD_DUMP + " <tid> <kind> <id>";
  private static final String CMD_EXPORT = "export";
  private static final String CMD_EXPORT_HELP = "Exports the specified model kind (one of: AST, MDG, STATIC, TRACE) to the specified target. If the target is a file, its extension determines the output format-- XML (*.xml) or textual (any other extension); for STATIC and TRACE, a JDBC url is also supported (the respective JDBC driver must be in the class path).";
  private static final String CMD_EXPORT_SYNTAX = JiveCommandProvider.CMD_EXPORT
      + " <tid> <kind> <target>";
  private static final String CMD_HELP = "help";
  private static final String CMD_HELP_HELP = "If no command is specified, shows the help for all Jive-specific commands, otherwise shows the help only for the specified command.";
  private static final String CMD_HELP_SYNTAX = JiveCommandProvider.CMD_HELP + " [command]";
  private static final String CMD_QUERY = "query";
  private static final String CMD_QUERY_HELP = "Evaluates the query and displays the results on both the search answers window and the sequence diagram.";
  private static final String CMD_QUERY_SYNTAX = JiveCommandProvider.CMD_QUERY
      + " <tid> <query string>";
  private static final String CMD_SLICE = "slice";
  private static final String CMD_SLICE_HELP = "Computes the kind of dynamic slice specified over the execution trace, starting from the given eventId, which must represent an assignment event. If CLEAR is used instead, any active slices are removed from the model.";
  private static final String CMD_SLICE_SYNTAX = JiveCommandProvider.CMD_SLICE
      + " <tid> <eventId | CLEAR>";
  private static final String CMD_START = "start";
  private static final String CMD_START_HELP = "Starts Jive on the given target if it is not already running.";
  private static final String CMD_START_SYNTAX = JiveCommandProvider.CMD_START + " <tid>";
  private static final String CMD_STATISTICS = "statistics";
  private static final String CMD_STATISTICS_HELP = "Shows available statistics for the current debug target, including number of threads, start/end events for each thread, number of events and model elements created.";
  private static final String CMD_STATISTICS_SYNTAX = JiveCommandProvider.CMD_STATISTICS + "<tid>";
  private static final String CMD_STATUS = "status";
  private static final String CMD_STATUS_HELP = "Shows the current debug target's status.";
  private static final String CMD_STATUS_SYNTAX = JiveCommandProvider.CMD_STATUS;
  private static final String CMD_STEP = "step";
  private static final String CMD_STEP_HELP = "Steps the debug target the given number of steps, which is either a positive or negative integer, or the symbolic values BOF and EOF.";
  private static final String CMD_STEP_SYNTAX = JiveCommandProvider.CMD_STEP + " <tid> <delta>";
  private static final String CMD_STOP = "stop";
  private static final String CMD_STOP_HELP = "Stops Jive if it is running on the currently active debug target.";
  private static final String CMD_STOP_SYNTAX = JiveCommandProvider.CMD_STOP + " <tid>";
  private static final String CMD_VERSION = "version";
  private static final String CMD_VERSION_HELP = "Shows Jive's version information.";
  private static final String CMD_VERSION_SYNTAX = JiveCommandProvider.CMD_VERSION;
  private static final int ID_DUMP = 0;
  private static final int ID_EXPORT = 1;
  private static final int ID_HELP = 2;
  private static final int ID_QUERY = 3;
  private static final int ID_SLICE = 4;
  private static final int ID_START = 5;
  private static final int ID_STATISTICS = 6;
  private static final int ID_STATUS = 7;
  private static final int ID_STEP = 8;
  private static final int ID_STOP = 9;
  private static final int ID_VERSION = 10;
  private static final List<String> CMDS = Arrays
      .asList(new String[]
      { JiveCommandProvider.CMD_DUMP, JiveCommandProvider.CMD_EXPORT, JiveCommandProvider.CMD_HELP,
          JiveCommandProvider.CMD_QUERY, JiveCommandProvider.CMD_SLICE,
          JiveCommandProvider.CMD_START, JiveCommandProvider.CMD_STATISTICS,
          JiveCommandProvider.CMD_STATUS, JiveCommandProvider.CMD_STEP,
          JiveCommandProvider.CMD_STOP, JiveCommandProvider.CMD_VERSION });
  private static final List<String> CMDS_SYNTAX = Arrays.asList(new String[]
  { JiveCommandProvider.CMD_DUMP_SYNTAX, JiveCommandProvider.CMD_EXPORT_SYNTAX,
      JiveCommandProvider.CMD_HELP_SYNTAX, JiveCommandProvider.CMD_QUERY_SYNTAX,
      JiveCommandProvider.CMD_SLICE_SYNTAX, JiveCommandProvider.CMD_START_SYNTAX,
      JiveCommandProvider.CMD_STATISTICS_SYNTAX, JiveCommandProvider.CMD_STATUS_SYNTAX,
      JiveCommandProvider.CMD_STEP_SYNTAX, JiveCommandProvider.CMD_STOP_SYNTAX,
      JiveCommandProvider.CMD_VERSION_SYNTAX });
  private static final List<String> CMDS_HELP = Arrays.asList(new String[]
  { JiveCommandProvider.CMD_DUMP_HELP, JiveCommandProvider.CMD_EXPORT_HELP,
      JiveCommandProvider.CMD_HELP_HELP, JiveCommandProvider.CMD_QUERY_HELP,
      JiveCommandProvider.CMD_SLICE_HELP, JiveCommandProvider.CMD_START_HELP,
      JiveCommandProvider.CMD_STATISTICS_HELP, JiveCommandProvider.CMD_STATUS_HELP,
      JiveCommandProvider.CMD_STEP_HELP, JiveCommandProvider.CMD_STOP_HELP,
      JiveCommandProvider.CMD_VERSION_HELP });

  public Object _jive(final CommandInterpreter ci)
  {
    String commandLine = "jive";
    String nextArgument = ci.nextArgument();
    // if an argument was passed, it must be a known command
    if (!checkCommand(ci, commandLine, nextArgument))
    {
      return null;
    }
    final int commandId = JiveCommandProvider.CMDS.indexOf(nextArgument);
    commandLine = commandLine + " " + nextArgument;
    nextArgument = ci.nextArgument();
    switch (commandId)
    {
      case ID_DUMP:
        handleDump(ci, nextArgument);
        break;
      case ID_EXPORT:
        handleExport(ci, nextArgument, commandLine);
        break;
      case ID_HELP:
        handleHelp(ci, nextArgument, commandLine);
        break;
      case ID_QUERY:
        ci.println(String.format("Jive will run '%s' now.", nextArgument));
        break;
      case ID_SLICE:
        handleSlice(ci, nextArgument, commandLine);
        break;
      case ID_START:
        handleStart(ci, nextArgument, commandLine);
        break;
      case ID_STATISTICS:
        ci.println(String.format("Jive will run '%s' now.", nextArgument));
        break;
      case ID_STATUS:
        handleStatus(ci, nextArgument, commandLine);
        break;
      case ID_STEP:
        ci.println(String.format("Jive will run '%s' now.", nextArgument));
        break;
      case ID_STOP:
        handleStop(ci, nextArgument, commandLine);
        break;
      case ID_VERSION:
        ci.println(String.format("Jive will run '%s' now.", nextArgument));
        break;
      default:
        invalid(ci, commandLine + nextArgument);
    }
    return null;
  }

  @Override
  public String getHelp()
  {
    final StringBuffer buffer = new StringBuffer("---JIVE commands---\n");
    for (int commandId = JiveCommandProvider.ID_DUMP; commandId <= JiveCommandProvider.ID_VERSION; commandId++)
    {
      buffer.append(String.format("\t%s\n", helpCommand(commandId)));
    }
    return buffer.toString();
  }

  private boolean checkArgument(final String commandLine, final String nextArgument,
      final CommandInterpreter ci)
  {
    // if no further argument is passed, it is a syntax error
    if (nextArgument == null)
    {
      invalid(ci, commandLine);
      return false;
    }
    return true;
  }

  private boolean checkCommand(final CommandInterpreter ci, final String commandLine,
      final String nextArgument)
  {
    if (!JiveCommandProvider.CMDS.contains(nextArgument))
    {
      invalid(ci, commandLine + (nextArgument == null ? " " + nextArgument : ""));
      return false;
    }
    return true;
  }

  private int checkIntegerArgument(final String commandLine, final String nextArgument,
      final CommandInterpreter ci)
  {
    // the next argument must be an integer
    if (checkArgument(commandLine, nextArgument, ci))
    {
      try
      {
        return Integer.valueOf(nextArgument);
      }
      catch (final NumberFormatException nfe)
      {
        invalid(ci, commandLine + " " + nextArgument);
      }
    }
    return -1;
  }

  private boolean checkNoArgument(final String commandLine, final String nextArgument,
      final CommandInterpreter ci)
  {
    // if another argument is passed, it is a syntax error
    if (nextArgument != null)
    {
      invalid(ci, commandLine + " " + nextArgument);
      return false;
    }
    return true;
  }

  private String commandsHelp()
  {
    final StringBuffer buffer = new StringBuffer();
    for (int commandId = JiveCommandProvider.ID_DUMP; commandId <= JiveCommandProvider.ID_VERSION; commandId++)
    {
      buffer.append(String.format("%s\n", helpCommand(commandId)));
    }
    return buffer.toString();
  }

  private void handleDump(final CommandInterpreter ci, final String nextArgument)
  {
    ci.println(String.format("Jive will run '%s' now.", nextArgument));
  }

  private void handleExport(final CommandInterpreter ci, String nextArgument, String commandLine)
  {
    if (checkArgument(commandLine, nextArgument, ci))
    {
      commandLine = commandLine + " " + nextArgument;
      // targetId
      int targetId = -1;
      if ((targetId = checkIntegerArgument(commandLine, nextArgument, ci)) != -1)
      {
        commandLine = commandLine + " " + nextArgument;
        nextArgument = ci.nextArgument();
        // element
        if (checkArgument(commandLine, nextArgument, ci))
        {
          commandLine = commandLine + " " + nextArgument;
          final String element = nextArgument;
          nextArgument = ci.nextArgument();
          // filePath
          if (checkArgument(commandLine, nextArgument, ci))
          {
            commandLine = commandLine + " " + nextArgument;
            if (element.equalsIgnoreCase("AST"))
            {
              if (ConsoleProxy.INSTANCE.exportAST(targetId, nextArgument))
              {
                ci.println("done");
              }
            }
            else if (element.equalsIgnoreCase("MDG"))
            {
              if (ConsoleProxy.INSTANCE.exportMDG(targetId, nextArgument))
              {
                ci.println("done");
              }
            }
            else if (element.equalsIgnoreCase("TRACE"))
            {
              if (ConsoleProxy.INSTANCE.exportTrace(targetId, nextArgument))
              {
                ci.println("done");
              }
            }
          }
        }
      }
    }
  }

  private void handleHelp(final CommandInterpreter ci, String nextArgument, String commandLine)
  {
    if (nextArgument == null)
    {
      ci.println(commandsHelp());
      return;
    }
    // if another argument is passed, it must be a known command
    if (checkCommand(ci, commandLine, nextArgument))
    {
      final int helpCommandId = JiveCommandProvider.CMDS.indexOf(nextArgument);
      commandLine = commandLine + " " + nextArgument;
      nextArgument = ci.nextArgument();
      // if no further arguments, print the command specific help
      if (checkNoArgument(commandLine, nextArgument, ci))
      {
        ci.println(helpCommand(helpCommandId));
      }
    }
  }

  private void handleSlice(final CommandInterpreter ci, String nextArgument, String commandLine)
  {
    if (checkArgument(commandLine, nextArgument, ci))
    {
      commandLine = commandLine + " " + nextArgument;
      // targetId
      int targetId = -1;
      if ((targetId = checkIntegerArgument(commandLine, nextArgument, ci)) != -1)
      {
        commandLine = commandLine + " " + nextArgument;
        nextArgument = ci.nextArgument();
        // eventId
        int eventId = -1;
        // the next argument could be an integer
        if (checkArgument(commandLine, nextArgument, ci))
        {
          try
          {
            eventId = Integer.valueOf(nextArgument);
          }
          catch (final NumberFormatException nfe)
          {
            eventId = -1;
          }
        }
        // the next argument was indeed an integer
        if (eventId != -1)
        {
          commandLine = commandLine + " " + nextArgument;
          final IProgramSlice slice = ConsoleProxy.INSTANCE.sliceProgram(targetId, eventId);
          if (slice == null)
          {
            ci.println("Invalid slice type or no slice produced.");
          }
          else
          {
            ci.print(slice);
          }
        }
        // the next argument was the CLEAR keyword
        else if (nextArgument.equalsIgnoreCase("CLEAR"))
        {
          commandLine = commandLine + " " + nextArgument;
          ConsoleProxy.INSTANCE.sliceClear(targetId);
          ci.println("Slice cleared.");
        }
        else
        {
          ci.println("Invalid slice type or no slice produced.");
        }
      }
    }
  }

  private void handleStart(final CommandInterpreter ci, final String nextArgument,
      String commandLine)
  {
    if (checkArgument(commandLine, nextArgument, ci))
    {
      commandLine = commandLine + " " + nextArgument;
      // targetId
      int targetId = -1;
      if ((targetId = checkIntegerArgument(commandLine, nextArgument, ci)) != -1)
      {
        commandLine = commandLine + " " + nextArgument;
        if (ConsoleProxy.INSTANCE.startTarget(targetId))
        {
          ci.println("snapshot created and target started");
        }
        else
        {
          ci.println("target unavailable or already started");
        }
      }
    }
  }

  private void handleStatus(final CommandInterpreter ci, final String nextArgument,
      final String commandLine)
  {
    // if no further arguments, handle the status
    if (checkNoArgument(commandLine, nextArgument, ci))
    {
      final List<String> targets = ConsoleProxy.INSTANCE.targetList();
      // header only
      if (targets.size() == 1)
      {
        ci.println("No Jive debug targets found.");
      }
      // header and targets
      else
      {
        for (final String target : targets)
        {
          ci.println(target);
        }
      }
    }
  }

  private void handleStop(final CommandInterpreter ci, final String nextArgument, String commandLine)
  {
    if (checkArgument(commandLine, nextArgument, ci))
    {
      commandLine = commandLine + " " + nextArgument;
      // targetId
      int targetId = -1;
      if ((targetId = checkIntegerArgument(commandLine, nextArgument, ci)) != -1)
      {
        commandLine = commandLine + " " + nextArgument;
        if (ConsoleProxy.INSTANCE.stopTarget(targetId))
        {
          ci.println("target stopped");
        }
        else
        {
          ci.println("target unavailable or already stopped");
        }
      }
    }
  }

  private String helpCommand(final int commandId)
  {
    if (commandId >= JiveCommandProvider.ID_DUMP && commandId <= JiveCommandProvider.ID_VERSION)
    {
      final String commandSyntax = "jive " + JiveCommandProvider.CMDS_SYNTAX.get(commandId);
      final String commandHelp = JiveCommandProvider.CMDS_HELP.get(commandId);
      return commandSyntax + " - " + commandHelp;
    }
    return "Unknown command identifier: " + commandId + ".";
  }

  private void invalid(final CommandInterpreter ci, final String value)
  {
    ci.println(String
        .format(
            "'%s' is an unknown or invalid command. User 'jive help' to learn which commands are available and their syntax.",
            value));
  }
}