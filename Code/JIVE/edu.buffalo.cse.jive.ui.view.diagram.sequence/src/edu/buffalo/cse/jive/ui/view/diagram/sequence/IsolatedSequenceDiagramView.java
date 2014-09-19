package edu.buffalo.cse.jive.ui.view.diagram.sequence;

import org.eclipse.gef.EditPartFactory;

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