package edu.buffalo.cse.jive.console;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class JiveConsole implements BundleActivator
{
  private CommandProvider service;

  @Override
  public void start(final BundleContext context) throws Exception
  {
    service = new JiveCommandProvider();
    // register the service
    context.registerService(CommandProvider.class.getName(), service, null);
  }

  @Override
  public void stop(final BundleContext context) throws Exception
  {
    service = null;
  }
}
