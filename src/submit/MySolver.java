package submit;

// some useful things to import. add any additional imports you need.
import joeq.Compiler.Quad.*;
import flow.Flow;
import java.util.*;

/**
 * Skeleton class for implementing the Flow.Solver interface.
 */
public class MySolver implements Flow.Solver {

    protected Flow.Analysis analysis;

    //public class BackwardBasicBlockVisitor extends QuadVisitor.EmptyVisitor implements BasicBlockVisitor {
    //        private isChanged = false;
    //        void visitBasicBlock(BasicBlock bb) {
    //    	    if (bb.isEntry() || bb.isExit()) return ;
    //    	    // running from last Quad to the first one 
    //    	    ListIterator<Quad> quadIter = bb.iterator();
    //    	    ArrayList<Quad> quadList = new ArrayList<Quad>();
    //    	    while (quadIter.hasNext()) quadIter.next();

    //    	    // update the IN using OUT
    //        }
    //}

    /**
     * Sets the analysis.  When visitCFG is called, it will
     * perform this analysis on a given CFG.
     *
     * @param analyzer The analysis to run
     */
    public void registerAnalysis(Flow.Analysis analyzer) {
        this.analysis = analyzer;
    }

    /**
     * Runs the solver over a given control flow graph.  Prior
     * to calling this, an analysis must be registered using
     * registerAnalysis
     *
     * @param cfg The control flow graph to analyze.
     */
    public void visitCFG(ControlFlowGraph cfg) {

        // this needs to come first.
        analysis.preprocess(cfg);

        /***********************
         * Your code goes here *
         ***********************/

	if (analysis.isForward()) {
		//boolean isChanged = true;
		//while (isChanged) {
		//	isChanged = false;
		//	QuadIterator qit = new QuadIterator(cfg);
		//	while (qit.hasNext()) {
		//	}
		//}
	} else {
		boolean isChanged = true;
		while (isChanged) {
			isChanged = false;
			QuadIterator qit = new QuadIterator(cfg);
			while (qit.hasNext()) {
				Quad q = qit.next();
				Flow.DataflowObject newOut = analysis.newTempVar();
				newOut.setToTop();
				Iterator<Quad> succIter = qit.successors();
				while (succIter.hasNext()) {
					Quad succ = succIter.next();
					if (succ == null) { // which indicates that this quad is connected to EXIT
						newOut.meetWith(analysis.getExit());
					} else {
						newOut.meetWith(analysis.getIn(succ));
					}
				}
				// so far we have already got the OUT orginal from the IN[all the successors]
				Flow.DataflowObject oldIn = analysis.getIn(q);
				analysis.setOut(q, newOut);
				analysis.processQuad(q);
				if (!oldIn.equals(analysis.getIn(q))) {
					isChanged = true;
				}
			}
		}
		Flow.DataflowObject entryValue = analysis.newTempVar();
		entryValue.setToTop();
		QuadIterator qit = new QuadIterator(cfg);
		while (qit.hasNext()) {
			Quad q = qit.next();
			Iterator<Quad> preIter = qit.predecessors();
			while (preIter.hasNext()) {
				Quad pre = preIter.next();
				if (pre == null) {
					entryValue.meetWith(analysis.getIn(q));
					break;
				}
			}
		}
		analysis.setEntry(entryValue);
	} 

        // this needs to come last.
        analysis.postprocess(cfg);
    }
}
