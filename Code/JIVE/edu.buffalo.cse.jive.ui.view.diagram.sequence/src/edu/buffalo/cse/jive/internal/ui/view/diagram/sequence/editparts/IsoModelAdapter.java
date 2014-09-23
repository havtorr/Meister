package edu.buffalo.cse.jive.internal.ui.view.diagram.sequence.editparts;


import edu.buffalo.cse.jive.model.IEventModel.IInitiatorEvent;
import edu.buffalo.cse.jive.model.IEventModel.IThreadStartEvent;

/**
 * Modified version of {@link ModelAdapter}, to allow children of hidden lifelines to be visible
 * @author håvard
 *
 */
public class IsoModelAdapter extends ModelAdapter {



	IsoModelAdapter(UIAdapter uiAdapter) {
		super(uiAdapter);
	}


	/**
	 * Visits the execution and generates the appropriate source/target connections.
	 */
	@Override
	protected void visitExecution(final IInitiatorEvent initiator){

		// a life line must exist on which to place the execution.
		if (!uiAdapter.isCollapsed(initiator)) {/*only add the lifelines that are relevant,
							while still allowing the children of hidden ones to be visible*/
			if (initiator instanceof IThreadStartEvent){
				if (showThreadActivations){
					lifelines.add(initiator.thread());
				}
			}else{
				lifelines.add(showExpandedLifeLines ? initiator.executionContext() : initiator
						.executionContext().concreteContour());				
			}
		}
		// process nested children only if the execution has children
		if (initiator.nestedInitiators().isEmpty()){
			return;
		}
		// no out-of-model calls detected yet
		IInitiatorEvent modelCaller = null;
		IInitiatorEvent modelReturner = null;
		// nested executions are always in-model
		for (final IInitiatorEvent nestedExecution : initiator.nestedInitiators()){
			// skip marker nested executions
			if (nestedExecution == null){
				// execution <-- + <-- modelReturner
				if (modelReturner != null){
					// outstanding broken terminator
					addBrokenTerminatorMessage(modelReturner, initiator);
				}
				// caller for the next broken initiator
				modelCaller = initiator;
				modelReturner = null;
				continue;
			}
			// nested execution called from a thread activation that is not visible
			if (initiator instanceof IThreadStartEvent && !showThreadActivations){
				// * --> nestedExecution
				addFoundInitiatorMessage(nestedExecution);
				// * <-- nestedExecution
				addLostTerminatorMessage(nestedExecution);
				// nested execution called from out-of-model
			}else if (initiator.eventId() != nestedExecution.parent().eventId()){
				// modelCaller --> + --> nestedExecution
				if (modelCaller != null){
					// broken initiator for this nested execution
					addBrokenInitiatorMessage(nestedExecution, initiator);
				}else{
					// outstanding lost terminator
					addLostTerminatorMessage(modelReturner);
					// found initiator for this nested execution
					addFoundInitiatorMessage(nestedExecution);
				}
				modelCaller = null;
				modelReturner = nestedExecution;
				// nested execution called from the in-model parent execution
			}else{
				// execution --> nestedExecution
				addInModelInitiatorMessage(nestedExecution);
				// execution <-- nestedExecution
				addInModelTerminatorMessage(nestedExecution);
			}
			// recursively visit the nested execution
			visitExecution(nestedExecution);
		}
		// outstanding lost terminator: execution <-- + <-- modelReturner OR * <-- modelReturner
		if (modelReturner != null){
			// terminator is the last event on the thread: use a simple lost message
			if (modelReturner.terminator() != null
					&& initiator.lastChildEvent().eventId() <= modelReturner.terminator().eventId())
			{
				addLostTerminatorMessage(modelReturner);
				// terminator is broken
			}else if (modelReturner.terminator() != null){
				addBrokenTerminatorMessage(modelReturner, initiator);
			}
		}
	}
}



