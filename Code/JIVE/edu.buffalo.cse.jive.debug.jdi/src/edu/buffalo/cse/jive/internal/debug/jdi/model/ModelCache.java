package edu.buffalo.cse.jive.internal.debug.jdi.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;

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
		//get object which represents the workspace
		IWorkspace	workspace	= ResourcesPlugin.getWorkspace();
		IProject[]	projects	= workspace.getRoot().getProjects();

		Set<String>	packageSet	= new HashSet<String>();

		//get all packages
		for (IProject project : projects) {
			try {
				if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
					IJavaProject javaProject = JavaCore.create(project);

					IPackageFragment[] packages = javaProject.getPackageFragments();

					for (IPackageFragment pack : packages) {
						if ((pack.getKind() == 2) && (!pack.getElementName().isEmpty())) {
							packageSet.add(pack.getElementName() + "*");
						}
					}
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		//combine and remove duplicates
		String[] packageStrings = new String[packageSet.size()];
		packageStrings = (String[]) packageSet.toArray(packageStrings);


		ArrayList<ArrayList<String>> exDepthList = new ArrayList<ArrayList<String>>();
		ArrayList<ArrayList<String>> inDepthList = new ArrayList<ArrayList<String>>();

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
