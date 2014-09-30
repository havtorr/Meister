package edu.buffalo.cse.jive.ui.view.diagram.sequence;

import org.eclipse.gef.EditPart;

import edu.buffalo.cse.jive.debug.model.IJiveDebugTarget;
import edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts.EventOccurrenceEditPart;
import edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts.Gutter;
import edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts.InitiatorMessageEditPart;
import edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts.IsolatedExecutionOccurrenceEditPart;
import edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts.IsolatedSequenceDiagramEditPart;
import edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts.LifelineEditPart;
import edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts.TerminatorMessageEditPart;
import edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts.Message.InitiatorMessage;
import edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts.Message.TerminatorMessage;
import edu.buffalo.cse.jive.model.IContourModel.IContextContour;
import edu.buffalo.cse.jive.model.IEventModel.IInitiatorEvent;
import edu.buffalo.cse.jive.model.IModel.IThreadValue;

/**
 * Specialized {@link SequenceDiagramEditPartFactory} for viewing an isolated
 * part of a larger sequence
 * @author håvard
 *
 */
public class IsolatedSequenceDiagramEditPartFactory extends	SequenceDiagramEditPartFactory{



	@Override
	public EditPart createEditPart(final EditPart context, final Object model){
		if (model == null){
			// System.err.println("returning empty edit part for context: " + context);
			return SequenceDiagramEditPartFactory.EMPTY_EDIT_PART;
		}
		if (model instanceof IJiveDebugTarget){
			final EditPart editPart = new IsolatedSequenceDiagramEditPart();
			editPart.setModel(model);
			return editPart;
		}
		if (model instanceof IContextContour || model instanceof IThreadValue
				|| model instanceof Gutter){
			final EditPart editPart = new LifelineEditPart();
			editPart.setModel(model);
			return editPart;
		}
		if (model instanceof IInitiatorEvent){
			final EditPart editPart = new IsolatedExecutionOccurrenceEditPart();
			editPart.setModel(model);
			return editPart;
		}
		if (model instanceof Long){
			final EditPart editPart = new EventOccurrenceEditPart();
			editPart.setModel(model);
			return editPart;
		}
		if (model instanceof InitiatorMessage){
			final EditPart editPart = new InitiatorMessageEditPart();
			editPart.setModel(model);
			return editPart;
		}
		if (model instanceof TerminatorMessage)	{
			final EditPart editPart = new TerminatorMessageEditPart();
			editPart.setModel(model);
			return editPart;
		}
		throw new IllegalArgumentException("Unknown element type:  " + model.getClass());
	}
}
