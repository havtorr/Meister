package edu.buffalo.cse.jive.ui.view.diagram.sequence;

import org.eclipse.gef.EditPartFactory;

/**
 * Specialized {@link SequenceDiagramView} for viewing an isolated part of a larger sequence
 * @author håvard
 *
 */
public class IsolatedSequenceDiagramView extends SequenceDiagramView{

	public IsolatedSequenceDiagramView()
	{
		super();
	}


	@Override
	protected EditPartFactory createEditPartFactory()
	{
		return new IsolatedSequenceDiagramEditPartFactory();
	}

	@Override
	protected String getDisplayTargetDropDownText()
	{
		return "Display Isolated Sequence Diagram";
	}
}