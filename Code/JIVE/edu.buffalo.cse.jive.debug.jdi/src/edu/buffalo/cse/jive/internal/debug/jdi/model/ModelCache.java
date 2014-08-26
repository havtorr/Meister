package edu.buffalo.cse.jive.internal.debug.jdi.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import edu.buffalo.cse.jive.model.IModelCache;
import edu.buffalo.cse.jive.model.lib.Tools;

public class ModelCache implements IModelCache
{
	/**
	 * A set containing classes that are known to be accepted by the filter.
	 */
	private final Set<String> acceptedClassCache;
	/**
	 * A set containing method names that are known to be accepted by the filter.
	 */
	private final Set<String> acceptedMethodsCache;
	/**
	 * The list of excluded package filters. Each element is a String specifying a regular expression
	 * filter, such as "java.*".
	 */
	private final List<String> exclusionList;
	/**
	 * The list of excluded method name filters. Each element is a String specifying a regular
	 * expression filter, such as "get*".
	 */
	private final List<String> exclusionPatterns;
	/**
	 * A set containing classes that are known to be rejected by the filter.
	 */
	private final Set<String> rejectedClassCache;
	/**
	 * A set containing method names that are known to be rejected by the filter.
	 */
	private final Set<String> rejectedMethodsCache;
	/**
	 * The list of included package filters. Each element is a String specifying a regular expression
	 * filter, such as "java.*".
	 */
	private final List<String> inclusionList;
	/**
	 * The list of included method name filters. Each element is a String specifying a regular
	 * expression filter, such as "get*".
	 */
	private final List<String> inclusionPatterns;
	
	private final List<String> filter;
	
	/**
	 * List of all java 8 API packages, and other APIs, as they are not all available through {@link Package}
	 * 
	 */
	private static final String[] javaAPIPackages =
		{"java.applet*", "java.awt*", "java.awt.color*", "java.awt.datatransfer*", "java.awt.dnd*",
		"java.awt.event*", "java.awt.font*", "java.awt.geom*", "java.awt.im*", "java.awt.im.spi*",
		"java.awt.image*", "java.awt.image.renderable*", "java.awt.print*", "java.beans*",
		"java.beans.beancontext*", "java.io*", "java.lang*", "java.lang.annotation*", "java.lang.instrument*",
		"java.lang.invoke*", "java.lang.management*", "java.lang.ref*", "java.lang.reflect*", "java.math*",
		"java.net*", "java.nio*", "java.nio.channels*", "java.nio.channels.spi*", "java.nio.charset*",
		"java.nio.charset.spi*", "java.nio.file*", "java.nio.file.attribute*", "java.nio.file.spi*",
		"java.rmi*", "java.rmi.activation*", "java.rmi.dgc*", "java.rmi.registry*", "java.rmi.server*",
		"java.security*", "java.security.acl*", "java.security.cert*", "java.security.interfaces*",
		"java.security.spec*", "java.sql*", "java.text*", "java.text.spi*", "java.time*", "java.time.chrono*",
		"java.time.format*", "java.time.temporal*", "java.time.zone*", "java.util*", "java.util.concurrent*",
		"java.util.concurrent.atomic*", "java.util.concurrent.locks*", "java.util.function*", "java.util.jar*",
		"java.util.logging*", "java.util.prefs*", "java.util.regex*", "java.util.spi*", "java.util.stream*",
		"java.util.zip*", "javax.accessibility*", "javax.activation*", "javax.activity*", "javax.annotation*",
		"javax.annotation.processing*", "javax.crypto*", "javax.crypto.interfaces*", "javax.crypto.spec*",
		"javax.imageio*", "javax.imageio.event*", "javax.imageio.metadata*", "javax.imageio.plugins.bmp*",
		"javax.imageio.plugins.jpeg*", "javax.imageio.spi*", "javax.imageio.stream*", "javax.jws*",
		"javax.jws.soap*", "javax.lang.model*", "javax.lang.model.element*", "javax.lang.model.type*",
		"javax.lang.model.util*", "javax.management*", "javax.management.loading*",
		"javax.management.modelmbean*", "javax.management.monitor*", "javax.management.openmbean*",
		"javax.management.relation*", "javax.management.remote*", "javax.management.remote.rmi*",
		"javax.management.timer*", "javax.naming*", "javax.naming.directory*", "javax.naming.event*",
		"javax.naming.ldap*", "javax.naming.spi*", "javax.net*", "javax.net.ssl*", "javax.print*",
		"javax.print.attribute*", "javax.print.attribute.standard*", "javax.print.event*", "javax.rmi*",
		"javax.rmi.CORBA*", "javax.rmi.ssl*", "javax.script*", "javax.security.auth*",
		"javax.security.auth.callback*", "javax.security.auth.kerberos*", "javax.security.auth.login*",
		"javax.security.auth.spi*", "javax.security.auth.x500*", "javax.security.cert*",
		"javax.security.sasl*", "javax.sound.midi*", "javax.sound.midi.spi*", "javax.sound.sampled*",
		"javax.sound.sampled.spi*", "javax.sql*", "javax.sql.rowset*", "javax.sql.rowset.serial*",
		"javax.sql.rowset.spi*", "javax.swing*", "javax.swing.border*", "javax.swing.colorchooser*",
		"javax.swing.event*", "javax.swing.filechooser*", "javax.swing.plaf*", "javax.swing.plaf.basic*",
		"javax.swing.plaf.metal*", "javax.swing.plaf.multi*", "javax.swing.plaf.nimbus*",
		"javax.swing.plaf.synth*", "javax.swing.table*", "javax.swing.text*", "javax.swing.text.html*",
		"javax.swing.text.html.parser*", "javax.swing.text.rtf*", "javax.swing.tree*", "javax.swing.undo*",
		"javax.tools*", "javax.transaction*", "javax.transaction.xa*", "javax.xml*", "javax.xml.bind*",
		"javax.xml.bind.annotation*", "javax.xml.bind.annotation.adapters*", "javax.xml.bind.attachment*",
		"javax.xml.bind.helpers*", "javax.xml.bind.util*", "javax.xml.crypto*", "javax.xml.crypto.dom*",
		"javax.xml.crypto.dsig*", "javax.xml.crypto.dsig.dom*", "javax.xml.crypto.dsig.keyinfo*",
		"javax.xml.crypto.dsig.spec*", "javax.xml.datatype*", "javax.xml.namespace*", "javax.xml.parsers*",
		"javax.xml.soap*", "javax.xml.stream*", "javax.xml.stream.events*", "javax.xml.stream.util*",
		"javax.xml.transform*", "javax.xml.transform.dom*", "javax.xml.transform.sax*",
		"javax.xml.transform.stax*", "javax.xml.transform.stream*", "javax.xml.validation*", "javax.xml.ws*",
		"javax.xml.ws.handler*", "javax.xml.ws.handler.soap*", "javax.xml.ws.http*", "javax.xml.ws.soap*",
		"javax.xml.ws.spi*", "javax.xml.ws.spi.http*", "javax.xml.ws.wsaddressing*", "javax.xml.xpath*",
		"org.ietf.jgss*", "org.omg.CORBA*", "org.omg.CORBA_2_3*", "org.omg.CORBA_2_3.portable*",
		"org.omg.CORBA.DynAnyPackage*", "org.omg.CORBA.ORBPackage*", "org.omg.CORBA.portable*",
		"org.omg.CORBA.TypeCodePackage*", "org.omg.CosNaming*", "org.omg.CosNaming.NamingContextExtPackage*",
		"org.omg.CosNaming.NamingContextPackage*", "org.omg.Dynamic*", "org.omg.DynamicAny*",
		"org.omg.DynamicAny.DynAnyFactoryPackage*", "org.omg.DynamicAny.DynAnyPackage*", "org.omg.IOP*",
		"org.omg.IOP.CodecFactoryPackage*", "org.omg.IOP.CodecPackage*", "org.omg.Messaging*",
		"org.omg.PortableInterceptor*", "org.omg.PortableInterceptor.ORBInitInfoPackage*",
		"org.omg.PortableServer*", "org.omg.PortableServer.CurrentPackage*",
		"org.omg.PortableServer.POAManagerPackage*", "org.omg.PortableServer.POAPackage*",
		"org.omg.PortableServer.portable*", "org.omg.PortableServer.ServantLocatorPackage*",
		"org.omg.SendingContext*", "org.omg.stub.java.rmi*", "org.w3c.dom*", "org.w3c.dom.bootstrap*",
		"org.w3c.dom.events*", "org.w3c.dom.ls*", "org.w3c.dom.views*", "org.xml.sax*",
		"org.xml.sax.ext*", "org.xml.sax.helpers*", "sun*", "sun.awt*", "sun.awt.resources*", "sun.font*",
		"sun.java2d*", "com*", "com.sun*", "com.sun.swing*", "com.sun.swing.internal*", "com.sun.swing.internal.plaf*",
		"com.sun.swing.internal.plaf.basic*", "com.sun.swing.internal.plaf.basic.resources*",
		"com.sun.swing.internal.plaf.metal*", "com.sun.swing.internal.plaf.metal.resources*",
		"com.sun.swing.internal.plaf.synth*", "com.sun.swing.internal.plaf.synth.resources*"
		};
	


	public ModelCache()
	{
		acceptedClassCache		= Tools.newHashSet();
		acceptedMethodsCache	= Tools.newHashSet();
		exclusionList			= Tools.newArrayList();
		exclusionPatterns		= Tools.newArrayList();
		inclusionList			= Tools.newArrayList();
		inclusionPatterns		= Tools.newArrayList();
		rejectedClassCache		= Tools.newHashSet();
		rejectedMethodsCache	= Tools.newHashSet();
		filter					= Tools.newArrayList();
	}

	@Override
	public boolean acceptsClass(final String clazz)
	{
		if (acceptedClassCache.contains(clazz))
		{
			return true;
		}
		else if (rejectedClassCache.contains(clazz))
		{
			return false;
		}
		else
		{
			for (final String exFilter : filter)
			{
				if (match(clazz, exFilter))
				{
					rejectedClassCache.add(clazz);
					return false;
				}
			}
			acceptedClassCache.add(clazz);
			return true;
		}
	}

	/**
	 * Adds a package filter to the exclusion list.
	 * 
	 * @param filter
	 *          the regular expression filter, such as "java.*"
	 */
	@Override
	public void addExclusionFilter(final String filter)
	{
		if (!exclusionList.contains(filter))
		{
			exclusionList.add(filter);
		}
		acceptedClassCache.clear();
		rejectedClassCache.clear();
	}

	/**
	 * Adds a method exclusion pattern to the exclusion list.
	 * 
	 * @param pattern
	 *          the regular expression filter, such as "get*"
	 */
	@Override
	public void addMethodExclusionPattern(final String pattern)
	{
		if (!exclusionPatterns.contains(pattern))
		{
			exclusionPatterns.add(pattern);
		}
		acceptedMethodsCache.clear();
		rejectedMethodsCache.clear();
	}

	@Override
	public List<String> getExclusionList()
	{
		return exclusionList;
	}

	/**
	 * Match an input string against a pattern. Patterns may contain the wildcard character '*' at
	 * either the beginning or end of the pattern (but not both).
	 * 
	 * @param input
	 *          the string to match against the pattern
	 * @param pattern
	 *          the pattern
	 * @return true if 'in' matches 'pat'
	 */
	@Override
	public boolean match(final String input, final String pattern)
	{
		final int wildcardIndex = pattern.indexOf('*');
		if (wildcardIndex == -1)
		{
			return input.equals(pattern);
		}
		else if (wildcardIndex == 0)
		{
			if (pattern.length() == 1)
			{
				return true;
			}
			else
			{
				return input.endsWith(pattern.substring(1));
			}
		}
		else if (wildcardIndex == pattern.length() - 1)
		{
//			System.out.println(input+" "+wildcardIndex+" "+pattern);
			return input.startsWith(pattern.substring(0, wildcardIndex - 1));
		}
		else
		{
			return input.equals(pattern);
		}
	}

	@Override
	public void addInclusionFilter(String filter) {
		if (!inclusionList.contains(filter))
		{
			inclusionList.add(filter);
		}
		acceptedClassCache.clear();
		rejectedClassCache.clear();	
	}

	@Override
	public void addMethodInclusionPattern(String pattern) {
		if (!inclusionPatterns.contains(pattern))
		{
			inclusionPatterns.add(pattern);
		}
		acceptedMethodsCache.clear();
		rejectedMethodsCache.clear();	
	}

	@Override
	public List<String> getInclusionList() {
		return inclusionList;
	}
	
	

	@Override
	public void buildFilter() {
		 
//		  get list of all packages, and combine with api-list
//		  sort exclusion- and inclusion-list by depth		-done
//		  while ex/in-lists not empty, n = 0:
//		  		compare exlist depth n with package-list
//		  		add matches to filter
//		  		compare inlist depth n with filter
//		  		remove matches from filter
//		  		n++
		 
		 
		
		
				
		Package[] packs = Package.getPackages();
		String[] packageStrings = new String[packs.length];
				
		ArrayList<ArrayList<String>> exDepthList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> inDepthList = new ArrayList<ArrayList<String>>();
		
		
		for (int i = 0; i < packageStrings.length; i++) {
			packageStrings[i] = packs[i].getName() + "*";
		}
		
		packageStrings = Tools.combineStringArrays(packageStrings, javaAPIPackages);
		
		exDepthList = makeHierarchical(exclusionList);
		inDepthList = makeHierarchical(inclusionList);	
		
		
		boolean[] added	= new boolean[packageStrings.length];
		
		//compare lists with package-list and add/remove from filter as appropriate 
		for (int i = 0; i < Math.max(inDepthList.size(), exDepthList.size()); i++) {
			if(exDepthList.size() > i){
				for (int j = 1; j < exDepthList.get(i).size(); j++) {
					boolean matchFound = false;
					for (int j2 = 0; j2 < packageStrings.length; j2++) {
						if (match(packageStrings[j2], exDepthList.get(i).get(j))) {
							if(!added[j2]){
								filter.add(packageStrings[j2]);
								added[j2] = true;
								matchFound = true;
							}
						}
					}
					if(!matchFound){
						filter.add(exDepthList.get(i).get(j));
					}
				}
			}
			if(inDepthList.size() > i){
				if(inDepthList.get(i) != null){
					for (int j = 1; j < inDepthList.get(i).size(); j++) {
						List<String> toRemove = new ArrayList<String>();
						for (String filtered : filter) {
							if (match(inDepthList.get(i).get(j), filtered)){
								toRemove.add(filtered);
							}
						}
						filter.removeAll(toRemove);
						System.out.println("removed" + toRemove);
					}
				}
			}
		}
	}
	
	/**
	 * takes a {@link List} of strings, and organizes them in a multidimensional {@link ArrayList}
	 * according to the number of '.'s in each string.
	 * @param list
	 * @return
	 */
	private ArrayList<ArrayList<String>> makeHierarchical(List<String> list){
		
		ArrayList<ArrayList<String>> hierarchy = new ArrayList<ArrayList<String>>();
		//prepass to check how deep the hierarchy must be.
				int depth = 0;
				for (String string : list) {
					int d = Tools.countOccurrences(string, '.');
					if( d > depth){
						depth = d;
					}
				}
				for (int i = 0; i < depth; i++) {
					hierarchy.add(i, new ArrayList<String>());
					hierarchy.get(i).add(Integer.toString(i));
				}
				//sort lists by depth
				for (String in : list) {
					int index	= Tools.countOccurrences(in, '.') -1;
					if (index == -1) {
						index = 0;
					}
					
					hierarchy.get(index).add(in);
				}
		return hierarchy;
	}
	

	@Override
	public List<String> getFilter() {
		return filter;
	}
}
