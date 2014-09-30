package edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

import edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.figures.ExecutionOccurrenceFigure;
import edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.figures.LifelineFigure;
import edu.buffalo.cse.jive.model.IEventModel.IInitiatorEvent;
import edu.buffalo.cse.jive.model.IEventModel.ISystemStartEvent;
import edu.buffalo.cse.jive.preferences.PreferencesPlugin;

public class IsolatedExecutionOccurrenceEditPart extends
ExecutionOccurrenceEditPart {


	@Override
	protected void refreshVisuals() {
		final IInitiatorEvent execution = getModel();
		final LifelineEditPart parent = (LifelineEditPart) getParent();
		final int width = PreferencesPlugin.getDefault().getActivationWidth();
		final int eventHeight = PreferencesPlugin.getDefault().eventHeight();
		final LifelineFigure parentFigure = (LifelineFigure) parent.getFigure();
		final ExecutionOccurrenceFigure figure = (ExecutionOccurrenceFigure) getFigure();
		final Dimension headDimension = parentFigure.getLifelineHeadSize();
		final int x = execution instanceof ISystemStartEvent ? 0 : (width + 3)
				* parent.binNumber(execution) + (headDimension.width / 2) - (width / 2);
		// "+1" on the execution because the activation is displayed one event below the initiator event
		final int delta = !(execution instanceof ISystemStartEvent) ? 1 : 0;
				
		final IsoModelAdapter modelAdapter = (IsoModelAdapter) contents().getModelAdapter();
		
		final long unitTop;
		final long unitHeight;

		unitTop = execution.eventId() - modelAdapter.getOffset();
		unitHeight = execution.duration();
		// }
		final int y = (int) (eventHeight * (delta + unitTop) + headDimension.height + 10);
		final Rectangle constraint = new Rectangle(x, y, execution instanceof ISystemStartEvent ? 1
				: width, (int) (eventHeight * (unitHeight + (execution instanceof ISystemStartEvent ? 20
						: 0))));
		parent.setLayoutConstraint(this, figure, constraint);
	}


}
