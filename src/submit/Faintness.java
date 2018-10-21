package submit;

// some useful things to import. add any additional imports you need.
import joeq.Compiler.Quad.*;
import flow.Flow;

/**
 * Skeleton class for implementing a faint variable analysis
 * using the Flow.Analysis interface.
 */
public class Faintness implements Flow.Analysis {

    /**
     * Class for the dataflow objects in the Faintness analysis.
     * You are free to change this class or move it to another file.
     */
    public static class MyDataflowObject implements Flow.DataflowObject {
        /**
         * Methods from the Flow.DataflowObject interface.
         * See Flow.java for the meaning of these methods.
         * These need to be filled in.
         */
	
	private Set<String> set;
	public static Set<String> universalSet; // Top

	public MyDataflowObject() { // should be initilaized to TOP
		assert(universalSet != null);
		set = new TreeSet<String>(universalSet);
	}

        public void setToTop() {
		assert(universalSet != null);
		set = new TreeSet<String>(universalSet);
	}

        public void setToBottom() {
		set = new TreeSet<String>();
	}

        public void meetWith (Flow.DataflowObject o) { // intersection
		ArrayList<String> removeList = new ArrayList<String>();
		MyDataflowObject a = (MyDataflowObject) o;
		for (String var: set) {
			if (! a.contains(var)) {
				removeList.add(var);
			}
		}
		set.removeAll(removeList);
	}

        public void copy (Flow.DataflowObject o) {
		MyDataflowObject a = (MyDataflowObject) o;
		set = new TreeSet<String>(a.set);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof MyDataflowObject) {
			MyDataflowObject a = (MyDataflowObject) o;
			return set.equals(a.set);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return set.hashCode();
	}

        /**
         * toString() method for the dataflow objects which is used
         * by postprocess() below.  The format of this method must
         * be of the form "[REG0, REG1, REG2, ...]", where each REG is
         * the identifier of a register, and the list of REGs must be sorted.
         * See src/test/TestFaintness.out for example output of the analysis.
         * The output format of your reaching definitions analysis must
         * match this exactly.
         */
        @Override
        public String toString() {
		return set.toString();
	}

	public void genFan(String v) {
		set.add(v);
	}

	public void killFan(String v) {
		set.remove(v);
	}

	public boolean isDstFant(List<String> dstList) {
		for (String dst: dstList) {
			if (! set.contains(dst)) {
				return false;
			}
		}
		return true; // if all destinations are fant, the srcs should be fant
	}

    }

    /**
     * Dataflow objects for the interior and entry/exit points
     * of the CFG. in[ID] and out[ID] store the entry and exit
     * state for the input and output of the quad with identifier ID.
     *
     * You are free to modify these fields, just make sure to
     * preserve the data printed by postprocess(), which relies on these.
     */
    private MyDataflowObject[] in, out;
    private MyDataflowObject entry, exit;

    /**
     * This method initializes the datflow framework.
     *
     * @param cfg  The control flow graph we are going to process.
     */
    public void preprocess(ControlFlowGraph cfg) {
        // this line must come first.
        System.out.println("Method: "+cfg.getMethod().getName().toString());

        // get the amount of space we need to allocate for the in/out arrays.
        QuadIterator qit = new QuadIterator(cfg);
        int max = 0;
        while (qit.hasNext()) {
            int id = qit.next().getID();
            if (id > max) 
                max = id;
        }
        max += 1;

	// initialize universal set
	Set<String> s = new TreeSet<String>();
	int numargs = cfg.getMethod().getParamTypes.length;
	for (int i = 0; i < numargs; ++ i) {
		s.add("R" + i);
	}
	qit = new QuadIterator(cfg);
	while (qit.hasNext()) {
		Quad q = qit.next();
		for (RegisterOperand def: q.getDefinedRegisters()) {
			s.add(def.getRegister().toString());
		}
		for (RegisterOperand use: q.getUsedRegisters()) {
			s.add(use.getRegister().toString());
		}
	}
	MyDataflowObject.universalSet = s;

        // allocate the in and out arrays.
        in = new MyDataflowObject[max];
        out = new MyDataflowObject[max];

        // initialize the contents of in and out.
        qit = new QuadIterator(cfg);
        while (qit.hasNext()) {
            int id = qit.next().getID();
            in[id] = new MyDataflowObject();
            out[id] = new MyDataflowObject();
        }

        // initialize the entry and exit points.
        entry = new MyDataflowObject();
        exit = new MyDataflowObject();

        /************************************************
         * Your remaining initialization code goes here *
         ************************************************/

	transferfn.val = new MyDataflowObject();
    }

    /**
     * This method is called after the fixpoint is reached.
     * It must print out the dataflow objects associated with
     * the entry, exit, and all interior points of the CFG.
     * Unless you modify in, out, entry, or exit you shouldn't
     * need to change this method.
     *
     * @param cfg  Unused.
     */
    public void postprocess (ControlFlowGraph cfg) {
        System.out.println("entry: " + entry.toString());
        for (int i=1; i<in.length; i++) {
            if (in[i] != null) {
                System.out.println(i + " in:  " + in[i].toString());
                System.out.println(i + " out: " + out[i].toString());
            }
        }
        System.out.println("exit: " + exit.toString());
    }

    /**
     * Other methods from the Flow.Analysis interface.
     * See Flow.java for the meaning of these methods.
     * These need to be filled in.
     */
    public boolean isForward () { 
	    return false; 
    }

    public Flow.DataflowObject getEntry() {
	    Flow.DataflowObject result = newTempVar();
	    result.copy(entry);
	    return result;
    }

    public Flow.DataflowObject getExit() {
	    Flow.DataflowObject result = newTempVar();
	    result.copy(exit);
	    return result;
    }

    public void setEntry(Flow.DataflowObject value) {
	    entry.copy(value);
    }

    public void setExit(Flow.DataflowObject value) {
	    exit.copy(value);
    }

    public Flow.DataflowObject getIn(Quad q) {
	    Flow.DataflowObject result = newTempVar();
	    result.copy(in[q.getID()]);
	    return result;
    }

    public Flow.DataflowObject getOut(Quad q) {
	    Flow.DataflowObject result = newTempVar();
	    result.copy(out[q.getID()]);
	    return result;
    }

    public void setIn(Quad q, Flow.DataflowObject value) {
	    in[q.getID()].copy(value);
    }

    public void setOut(Quad q, Flow.DataflowObject value) {
	    out[q.getID()].copy(value);
    }

    public Flow.DataflowObject newTempVar() {
	    return MyDataflowObject();
    }

    private TransferFunction transferfn = new TransferFunction();
    public void processQuad(Quad q) {
	    transferfn.val.copy(out[q.getID()]);
	    transferfn.visitQuad(q);
	    in[q.getID()].copy(transferfn.val);
    }

    public static class TransferFunction extends QuadVisitor.EmptyVisitor {
	    MyDataflowObject val;

	    @Override 
	    public void visitQuad(Quad q) {
		    for (RegisterOperand def: q.getDefinedRegisters()) {
			    val.genFan(def.getRegister().toString());
		    }
		    if (q.getOperator() instanceof Operator.Move || q.getOperator() instanceof Operator.Binary) {
			    ArrayList<String> dstList = new ArrayList<String>();
			    for (RegisterOperand def : q.getDefinedRegisters()) {
				    dstList.add(def.getRegister().toString());
			    }
			    boolean isDstFant = val.isDstFant(dstList);
			    if (! isDstFant) {
				    for (RegisterOperand use: q.getUsedRegisters()) {
					    val.killFan(use.getRegister().toString());
				    }
			    }
		    } else {
			    for (RegisterOperand use: q.getUsedRegisters()) {
				    val.killFan(use.getRegister().toString());
			    }
		    }
	    }
    }

}
