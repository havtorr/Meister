package edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts;

import java.util.ArrayList;
import java.util.List;

import edu.buffalo.cse.jive.model.IEventModel.IInitiatorEvent;
import edu.buffalo.cse.jive.model.IEventModel.IJiveEvent;

public class IsolatedSequenceDiagramEditPart extends SequenceDiagramEditPart {

	 public IsolatedSequenceDiagramEditPart()
	  {
	   super();
	  }
	
	
	/**
	   * collapses everything except the provided event end its children
	   * @param execution
	   */
	  public void collapseAllBut(final IInitiatorEvent execution){
		  List<Integer>		eventIDs	= new ArrayList<Integer>();

		  for (int i = 0; i < execution.eventId(); i++) {
			  eventIDs.add(new Integer(i));
		  }

		  int lastEvendID = (int) execution.lastChildEvent().eventId();
		  IJiveEvent finalEvent = finalEvent(execution);
		  int finalEventID = (int) finalEvent.eventId();

		  for (int i = lastEvendID+1; i < finalEventID; i++) {
			  eventIDs.add(new Integer(i));
		  }
		  
		  uiAdapter.collapseSet(eventIDs);
		  forceUpdate();
	  }

	  /**
	   * Helper method to get the very last event in the execution trace
	   * @param event
	   * @return
	   */
	  private IJiveEvent finalEvent(IInitiatorEvent event) {
		  if (event.parent() == null ){
			  return  event.lastChildEvent();
		  }else {
			  return finalEvent(event.parent());
		  }

	  }
}
